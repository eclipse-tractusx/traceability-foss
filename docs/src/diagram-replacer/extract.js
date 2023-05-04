/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
const { spawn } = require("child_process");
const TARGET_PATH = "../../target/";

fs.readdirSync(TARGET_PATH).forEach((file) => {
  if (path.extname(file) === ".adoc") {
    let plantuml = spawn("java", [
      "-jar",
      "./plantuml.jar",
      "-tpng",
      "../../target/" + file,
      "-o",
      `../src/diagram-replacer/assets/${path.parse(file).name}`,
    ]);
    plantuml.on("close", (code) => {
      console.log(`extracted .PNGs from ${file} with code ${code}`);
    });
  }


});
