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

import { QualityType } from '@page/parts/model/parts.model';
import { QualityTypeComponent } from '@shared/components/quality-type/quality-type.component';
import { SharedModule } from '@shared/shared.module';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('QualityTypeComponent', () => {
  const renderQualityType = (qualityType: QualityType) => {
    return renderComponent(`<app-quality-type [type]='qualityType'>Test</app-quality-type>`, {
      imports: [SharedModule],
      componentProperties: { qualityType },
    });
  };

  it('should render qualityType', async () => {
    await renderQualityType(QualityType.Ok);
    expect(screen.getByText('qualityType.Ok')).toBeInTheDocument();
  });

  it('should render correct Ok icon', async () => {
    await renderQualityType(QualityType.Ok);
    expect(screen.getByText('check_circle_outline')).toBeInTheDocument();
  });

  // it('should render correct Minor icon', async () => {
  //   await renderQualityType(QualityType.Minor);
  //   expect(screen.getByText('info')).toBeInTheDocument();
  // });

  // it('should render correct Major icon', async () => {
  //   await renderQualityType(QualityType.Major);
  //   expect(screen.getByText('warning')).toBeInTheDocument();
  // });

  // it('should render correct Critical icon', async () => {
  //   await renderQualityType(QualityType.Critical);
  //   expect(screen.getByText('error_outline')).toBeInTheDocument();
  // });

  // it('should render correct LifeThreatening icon', async () => {
  //   await renderQualityType(QualityType.LifeThreatening);
  //   expect(screen.getByText('error')).toBeInTheDocument();
  // });
});
