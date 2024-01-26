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

import { Injectable } from '@angular/core';
import { AlertsFacade } from '@page/alerts/core/alerts.facade';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AlertHelperService {
  constructor(private readonly alertsFacade: AlertsFacade) {
  }

  public approve(id: string): Observable<void> {
    return this.alertsFacade.approveAlert(id);
  }

  public cancel(id: string): Observable<void> {
    return this.alertsFacade.cancelAlert(id);
  }

  public close(id: string, reason: string): Observable<void> {
    return this.alertsFacade.closeAlert(id, reason);
  }

  public acknowledge(id: string): Observable<void> {
    return this.alertsFacade.acknowledgeAlert(id);
  }

  public accept(id: string, reason: string): Observable<void> {
    return this.alertsFacade.acceptAlert(id, reason);
  }

  public decline(id: string, reason: string): Observable<void> {
    return this.alertsFacade.declineAlert(id, reason);
  }

}
