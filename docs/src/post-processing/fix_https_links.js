/********************************************************************************
 * Copyright (c) 2021,2022,2023
 *       2022: ZF Friedrichshafen AG
 *       2022: ISTOS GmbH
 *       2022,2023: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *       2022,2023: BOSCH AG
 * Copyright (c) 2021,2022,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0. *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
const fs = require("fs");
const path = require("path");
const PATH_TO_MD_FILES = "../../target/generated-docs/";

fs.readdirSync(PATH_TO_MD_FILES).forEach((file) => {
  if (path.extname(file) === ".md") {
    fs.readFile(PATH_TO_MD_FILES + file, "utf8", (err, data) => {
      if (err) throw err;

      const lines = data.split("\n");
      const output = [];

      let isYaml = false;

      for (let i = 0; i < lines.length; i++) {
        if (lines[i].startsWith("```yaml")) {
          isYaml = true;
        } else if (lines[i].startsWith("```")) {
          isYaml = false;
        }

        // when line contains https without marks around we need to add <> before and after (MD034 - Bare URL used)
        if (!isYaml && lines[i].includes(" https://") || lines[i].startsWith("https://")) {
          const firstIndex = lines[i].indexOf("https://");
          const lastIndex = lines[i].indexOf(" ", firstIndex + 1);
          const fixFirst = lines[i].replace("https://", "<https://");
          let afterReplacement;
          if (lastIndex === -1) {
            afterReplacement = fixFirst + ">";
          } else {
            afterReplacement = fixFirst.slice(0, lastIndex + 1) + ">" + fixFirst.slice(lastIndex + 1);
          }

          output.push(afterReplacement);
        } else {
          output.push(lines[i]);
        }

      }

      fs.writeFile(
        `${PATH_TO_MD_FILES}${file}`,
        output.join("\n"),
        "utf8",
        (err) => {
          if (err) throw err;
          console.log(
            `successfully fix https links ${file}`
          );
        }
      );
    });
  }
});
