/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

import { LayoutModule } from '@layout/layout.module';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsModule } from '@page/parts/parts.module';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { StaticIdService } from '@shared/service/staticId.service';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import {
  MOCK_part_1,
  MOCK_part_2,
} from '../../../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';
import { StartInvestigationComponent } from './start-investigation.component';
import { RequestInvestigationComponent } from '@shared/components/request-notification';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TestBed } from '@angular/core/testing';
import { Subject } from 'rxjs';

class MatDialogRefMock<T, TResult> {
  componentInstance = { deselectPart: new Subject<void>() };
  afterClosed() {
    return new Subject<TResult>();
  }
  close() { }
  closeWithResult(_result: T) { }
}

describe('StartInvestigationComponent', () => {
  const part = { data: PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT) };
  const firstChild = PartsAssembler.assemblePart(MOCK_part_2, MainAspectType.AS_BUILT);

  const renderStartInvestigation = async () => {
    const { fixture } = await renderComponent(StartInvestigationComponent, {
      declarations: [StartInvestigationComponent],
      imports: [PartDetailsModule, PartsModule, OtherPartsModule, LayoutModule],
      providers: [StaticIdService,
        { provide: MatDialog, useValue: { open: () => new MatDialogRefMock<RequestInvestigationComponent, any>() } },
      ],
    });

    fixture.componentInstance.part = part;
    fixture.autoDetectChanges();
    fixture.detectChanges();
    return fixture;
  };

  it('should render component', async () => {
    await renderStartInvestigation();

    expect(await screen.findByText('partDetail.investigation.headline')).toBeInTheDocument();
  });

  it('should sort table data', async () => {
    const fixture = await renderStartInvestigation();
    const spy = spyOn((fixture.componentInstance as any).childPartsState, 'update').and.callThrough();
    const nameHeader = await waitFor(() => screen.getByText('table.column.nameAtManufacturer'));

    fireEvent.click(nameHeader);
    expect(spy).toHaveBeenCalledWith({ data: [firstChild] });

    fireEvent.click(nameHeader);
    expect(spy).toHaveBeenCalledWith({ data: [firstChild] });
  });

  it('should open investigation dialog and subscribe to events', async () => {
    const fixture = await renderStartInvestigation();
    const { componentInstance } = fixture;
    const selectedChildPartsState = (componentInstance as any)['selectedChildPartsState'];
    const dialog = TestBed.inject(MatDialog);

    spyOn(dialog, 'open').and.callThrough();

    const openDialogRef = dialog.open(RequestInvestigationComponent, {
      data: { selectedItems: selectedChildPartsState.snapshot, showHeadline: true },
    }) as MatDialogRef<any>;

    const unsubscribeSpy = spyOn(openDialogRef.componentInstance.deselectPart, 'unsubscribe');

    const afterClosedSpy = spyOn(openDialogRef, 'afterClosed').and.callThrough();
    const closeSpy = spyOn(openDialogRef, 'close');

    componentInstance.openInvestigationDialog();

    expect(dialog.open).toHaveBeenCalledWith(RequestInvestigationComponent, {
      data: { selectedItems: selectedChildPartsState.snapshot, showHeadline: true },
    });

    openDialogRef.componentInstance.deselectPart.next(); // Simulate next event

    expect(afterClosedSpy).not.toHaveBeenCalled(); // Dialog should not have been closed yet

    // Simulate closing the dialog
    openDialogRef.afterClosed().subscribe(() => {
      // After the dialog is closed, the afterClosedSpy should have been called
      expect(afterClosedSpy).toHaveBeenCalled();
      expect(unsubscribeSpy).toHaveBeenCalled();
    });

    openDialogRef.close(); // Close the dialog
    expect(closeSpy).toHaveBeenCalled();
  });
});
