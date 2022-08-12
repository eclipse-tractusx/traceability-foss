/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
    expect(screen.getByText('Ok')).toBeInTheDocument();
  });

  it('should render correct Ok icon', async () => {
    await renderQualityType(QualityType.Ok);
    expect(screen.getByText('check_circle_outline')).toBeInTheDocument();
  });

  it('should render correct Minor icon', async () => {
    await renderQualityType(QualityType.Minor);
    expect(screen.getByText('info')).toBeInTheDocument();
  });

  it('should render correct Major icon', async () => {
    await renderQualityType(QualityType.Major);
    expect(screen.getByText('warning')).toBeInTheDocument();
  });

  it('should render correct Critical icon', async () => {
    await renderQualityType(QualityType.Critical);
    expect(screen.getByText('error_outline')).toBeInTheDocument();
  });

  it('should render correct LifeThreatening icon', async () => {
    await renderQualityType(QualityType.LifeThreatening);
    expect(screen.getByText('error')).toBeInTheDocument();
  });
});
