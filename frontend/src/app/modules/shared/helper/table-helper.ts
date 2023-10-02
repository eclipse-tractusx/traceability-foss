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
import {Pagination} from "@core/model/pagination.model";
import {FlattenObjectPipe} from "@shared/pipes/flatten-object.pipe";

export function getPartsPaginationData(page: number, pageSize: number, totalItems: number, content: any): Pagination<unknown>{
    const flatter = new FlattenObjectPipe();
    const newContent = content.map(part => flatter.transform(part));

    return {
        page: page,
        pageCount: null,
        pageSize: pageSize,
        totalItems: totalItems,
        content: newContent
    }

}

export function removeSelectedValues(selection: any, itemsToRemove: unknown[]): void {
    const shouldDelete = (row: unknown) => !!itemsToRemove.find(data => JSON.stringify(data) === JSON.stringify(row));
    const rowsToDelete = selection.selected.filter(data => shouldDelete(data));

    selection.deselect(...rowsToDelete);
};

export function addSelectedValues(selection: any, newData: unknown[]): void {
    const newValues = newData.filter(data => !selection.isSelected(data));
    selection.select(...newValues);
}
