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
import {FormControl} from "@angular/forms";
import {TableViewConfig} from "@shared/components/parts-table/table-view-config.model";
import {PartsTableConfigUtils} from "@shared/components/parts-table/parts-table-config.utils";

export class PartsAsBuiltSupplierConfigurationModel {

    public static filterConfiguration(): TableViewConfig {

        return {
            filterColumns: this.filterColumns,
            displayedColumns: this.displayedColumns,
            displayFilterColumnMappings: this.displayColumnsToFilterColumnsMapping,
            filterFormGroup: this.formGroup,
            sortableColumns: this.sortableColumns
        }
    }

    private static sortableColumns: Record<string, boolean> = {
        select: false,
        semanticDataModel: true,
        nameAtManufacturer: true,
        manufacturerName: true,
        manufacturerPartId: true,
        semanticModelId: true,
        manufacturingDate: true,
        receivedActiveAlerts: true,
        receivedActiveInvestigations: true,
        sentActiveAlerts: true,
        sentActiveInvestigations: true,
        //menu: false
    };
    private static displayedColumns = Object.keys(this.sortableColumns);

    private static formGroup: Record<string, FormControl> = PartsTableConfigUtils.createFormGroup(this.displayedColumns);

    private static filterColumns: string[] = PartsTableConfigUtils.createFilterColumns(this.displayedColumns);

    private static dateFields = ['manufacturingDate'];
    private static singleSearchFields = ['receivedActiveAlerts', 'sentActiveAlerts', 'receivedActiveInvestigations', 'sentActiveInvestigations'];
    private static displayColumnsToFilterColumnsMapping: any[] = PartsTableConfigUtils.generateFilterColumnsMapping(this.sortableColumns, this.dateFields, this.singleSearchFields);

}

