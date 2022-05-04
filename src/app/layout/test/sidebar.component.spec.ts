/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { HttpClient, HttpHandler } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, RouterEvent } from '@angular/router';
import { SvgIconsModule } from '@ngneat/svg-icon';
import { KeycloakService } from 'keycloak-angular';
import { ReplaySubject } from 'rxjs';
import { Realm } from 'src/app/core/model/realm.model';
import { UserService } from 'src/app/core/user/user.service';
import { LayoutFacade } from 'src/app/shared/abstraction/layout-facade';
import { icons } from 'src/app/shared/shared-icons.module';
import { SharedModule } from 'src/app/shared/shared.module';

import { SidebarComponent } from '../presentation/sidebar/sidebar.component';

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;
  const eventSubject = new ReplaySubject<RouterEvent>(1);
  const mockRouter = {
    navigate: jasmine.createSpy('navigate'),
    events: eventSubject,
    url: 'test/url',
  };
  const userData = {
    getOrgPreferences() {
      const mspId = 'Lion';
      const user: Realm = {} as Realm;
      if (mspId !== 'Lion' && mspId !== '92a2bd') {
        user.assetsTile = 'My components';
        user.componentsTile = 'Other parts';
      } else {
        user.assetsTile = 'My vehicles';
        user.componentsTile = 'Components';
      }
      return user;
    },

    getRoles() {
      return ['quality_manager'];
    },
  };
  let stubService: Partial<KeycloakService>;

  const mockFacade = {
    getOrgPreferences() {
      return;
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        SvgIconsModule.forRoot({
          defaultSize: 'sm',
          sizes: {
            xs: '18px',
            sm: '24px',
            md: '36px',
            lg: '48px',
            xl: '64px',
            xxl: '128px',
          },
        }),
        SvgIconsModule.forChild(icons),
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [SidebarComponent],
      providers: [
        { provide: UserService, useValue: userData },
        { provide: Router, useValue: mockRouter },
        { provide: KeycloakService, useValue: stubService },
        { provide: LayoutFacade, useValue: mockFacade },
        HttpClient,
        HttpHandler,
      ],
    });
    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
