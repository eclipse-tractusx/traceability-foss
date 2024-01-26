/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

const fs = require('fs');
const path = require('path');

const getArg = argName => {
  const argPrefix = `--${argName}=`;

  for (const arg of process.argv) {
    if (arg.startsWith(argPrefix) && arg.length > argPrefix.length) {
      return arg.substring(argPrefix.length);
    }
  }

  throw new Error('Cannot find arg with prefix ' + argPrefix);
};

const licenseHeader = fs.readFileSync(path.resolve(__dirname, 'LICENSE_HEADER'), 'utf8').trim();
const srcPath = getArg('src');

const LICENSE_COMMENT_WIDTH = 80;

const buildAddComment =
  ({ openComment, closeComment, trimLicense, printLicense = () => licenseHeader }) =>
  content => {
    const firstHtmlCommentStarts = content.indexOf(openComment());
    const firstHtmlCommentsEnd =
      firstHtmlCommentStarts > -1 ? content.indexOf(closeComment().trim(), firstHtmlCommentStarts) : -1;
    const hasAnyComment =
      firstHtmlCommentStarts > -1 &&
      (firstHtmlCommentStarts === 0 || !content.substring(0, firstHtmlCommentStarts).trim());

    const prependNewLicenseComment = (fileContent = content) =>
      openComment(LICENSE_COMMENT_WIDTH) +
      '\n' +
      printLicense() +
      '\n' +
      closeComment(LICENSE_COMMENT_WIDTH) +
      '\n\n' +
      fileContent;

    if (!hasAnyComment) {
      return prependNewLicenseComment();
    } else {
      if (firstHtmlCommentsEnd === -1) {
        throw new Error(`Cannot check invalid comment, there is no "${closeComment}" close comment`);
      }

      const maybeLicense = trimLicense(
        content.substring(firstHtmlCommentStarts + openComment().length, firstHtmlCommentsEnd),
      );

      if (maybeLicense !== licenseHeader) {
        const endOfLicenseHeader = content.indexOf('\n', firstHtmlCommentsEnd);
        return prependNewLicenseComment(content.substring(endOfLicenseHeader + 2));
      }
    }

    return content;
  };

const addHtmlComment = buildAddComment({
  openComment: _ => '<!--',
  closeComment: _ => '-->',
  trimLicense: comment =>
    comment
      .split('\n')
      .map(line => line.trimEnd())
      .join('\n')
      .trim(),
});

const addJsLikeComment = buildAddComment({
  openComment: (longestLine = 1) => '/' + new Array(longestLine).fill('*').join(''),
  closeComment: (longestLine = 1) => ' ' + new Array(longestLine).fill('*').join('') + '/',
  trimLicense: comment =>
    comment
      .split('\n')
      .map(line => line.replace(/^\s*\*\s?/, '').trimEnd())
      .join('\n')
      .trim(),
  printLicense: () =>
    licenseHeader
      .split('\n')
      .map(line => (line.trim() ? ` * ${line}` : ' *'))
      .join('\n'),
});

const supportedExtensions = {
  html: addHtmlComment,
  java: addJsLikeComment,
  js: addJsLikeComment,
  ts: addJsLikeComment,
  scss: addJsLikeComment,
};

const ensureThatCommentExists = filePath => {
  for (const ext in supportedExtensions) {
    if (filePath.endsWith('.' + ext)) {
      const fileContent = fs.readFileSync(filePath, 'utf8');
      try {
        const transformedContent = supportedExtensions[ext](fileContent);
        fs.writeFileSync(filePath, transformedContent, 'utf8');
      } catch (err) {
        throw new Error(`Cannot transform ${filePath}`, { cause: err });
      }
    }
  }
};

const lookupForFiles = src =>
  fs.readdirSync(src, { withFileTypes: true }).map(dirent => ({
    isDir: dirent.isDirectory(),
    path: path.resolve(src, dirent.name),
  }));

const filesToIterate = lookupForFiles(srcPath);

while (filesToIterate.length > 0) {
  const fileToIterate = filesToIterate.pop();
  if (fileToIterate.isDir) {
    lookupForFiles(fileToIterate.path).forEach(file => {
      filesToIterate.push(file);
    });
  } else {
    ensureThatCommentExists(fileToIterate.path);
  }
}
