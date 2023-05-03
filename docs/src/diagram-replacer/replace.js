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
const SOURCE_PATH = "../../target/";
const TARGET_PATH = "./generated-adocs/";
const IMAGES_PATH = "./assets/";

/**
 * CAUTION: :imagedir: variables in .adoc get deleted, so that asset references can be properly set
 */

fs.readdirSync(SOURCE_PATH).forEach((file) => {
  if (path.extname(file) === ".adoc") {
    fs.readFile(SOURCE_PATH + file, "utf8", (err, data) => {
      if (err) throw err;

      let imageList = [];
      // loop over images in extracted Image Directory of corresponding adoc
      fs.readdirSync(IMAGES_PATH + path.parse(file).name + "/").forEach(
        (image) => {
          // add suffix _000 to the first png that is exported to  by plantuml.jar
          if (path.parse(image).name === path.parse(file).name) {
            let renamedImage = path.parse(image).name + "_000.png";
            fs.renameSync(
              IMAGES_PATH + path.parse(file).name + "/" + image,
              IMAGES_PATH +
                path.parse(file).name +
                "/" +
                renamedImage
            );
            imageList.push(renamedImage);
          } else {
            imageList.push(image);
          }
          
        }
      );

      let ImageDirectoryPathForAdoc = IMAGES_PATH + path.parse(file).name + "/";

      const lines = data.split("\n");
      let insidePlantUmlBlock = false;
      const output = [];

      for (let i = 0; i < lines.length; i++) {
        // when line starts with plantuml tag replace with corresponding .PNG
        if (lines[i].startsWith("[plantuml, target=")) {
          const filename = lines[i].match(/target=(.*), format=svg/)[1];
          output.push(
            `image::${ImageDirectoryPathForAdoc + imageList.shift()}[]`
          );
        } else if (lines[i].startsWith("@startuml")) {
          insidePlantUmlBlock = true;
        } else if (lines[i].startsWith("@enduml")) {
          insidePlantUmlBlock = false;
          continue;
        } else if (insidePlantUmlBlock) {
          continue;
        } else if (lines[i].startsWith(":imagesdir:")) {
          continue;
        } else {
          output.push(lines[i]);
        }

        // delete empty black box
        if (
          output[output.length - 1] === "...." &&
          output[output.length - 2] === "...."
        ) {
          output.pop();
          output.pop();
        }
      }

      // create target directory for adoc with pngs if not existant
      if (!fs.existsSync(TARGET_PATH)) {
        fs.mkdirSync(TARGET_PATH);
      }

      fs.writeFile(
        `${TARGET_PATH}${file}`,
        output.join("\n"),
        "utf8",
        (err) => {
          if (err) throw err;
          console.log(
            `successfully replaced PlantUML Code inside ${file} with .PNG Images`
          );
        }
      );
    });
  }
});
