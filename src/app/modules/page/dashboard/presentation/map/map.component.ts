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

import { ChangeDetectionStrategy, Component, Inject, Input, OnInit } from '@angular/core';
import { KnownLocale } from '@core/i18n/global-i18n.providers';
import { IconLayer } from '@deck.gl/layers';
import { MapboxLayer } from '@deck.gl/mapbox';
import MapboxLanguage from '@mapbox/mapbox-gl-language';
import { I18NEXT_SERVICE, ITranslationService } from 'angular-i18next';
import * as mapboxGl from 'mapbox-gl';
import { Map, NavigationControl } from 'mapbox-gl';
import { distinctUntilChanged } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { MAPPING, PartsCoordinates, supportedLanguages } from './map.model';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapComponent implements OnInit {
  @Input()
  set mapData(data: PartsCoordinates[]) {
    this._mapData = data;
    this.renderMap(this._mapData);
  }

  get mapData(): PartsCoordinates[] {
    return this._mapData;
  }

  private _mapData: PartsCoordinates[];
  private map: Map;
  private currentZoom: number;

  constructor(@Inject(I18NEXT_SERVICE) private readonly i18NextService: ITranslationService) {}

  public ngOnInit() {
    this.i18NextService.events.languageChanged.pipe(distinctUntilChanged()).subscribe((language: KnownLocale) => {
      if (language) {
        this.currentZoom = null;
        this.renderMap(this.mapData);
      }
    });
  }

  public renderMap(data: PartsCoordinates[]): void {
    if (this.map) {
      this.map.remove();
    }

    const locale: Record<string, string> = this.i18NextService.t('map', { returnObjects: true });
    this.map = new Map({
      accessToken: environment.mapBoxAccessToken,
      container: 'map',
      style: 'mapbox://styles/mapbox/light-v10',
      center: { lng: 14, lat: 52 },
      maxZoom: 13,
      minZoom: 1,
      attributionControl: false,
      locale,
    });

    const defaultLanguage = supportedLanguages.includes(this.i18NextService.language)
      ? this.i18NextService.language
      : 'mul';

    const language = new MapboxLanguage({ defaultLanguage });
    this.map.addControl(language);

    this.map.addControl(
      new NavigationControl({
        showZoom: true,
        showCompass: true,
        visualizePitch: true,
      }),
      'top-right',
    );

    this.map.on('load', event => this.renderLayers(event, data));
    this.map.on('zoom', event => this.renderLayers(event, data));
  }

  public setLayers(map: Map, data: PartsCoordinates[], sizeScale = 20): Map {
    const id = 'icon-layer';
    const layer = map.getLayer(id);

    if (layer) {
      map.removeLayer(id);
    }

    const iconLayer = new MapboxLayer({
      id,
      data,
      sizeScale,
      type: IconLayer,
      iconAtlas: '../assets/images/location-icon.png',
      iconMapping: MAPPING,
      getPosition: d => [d.coordinates[0], d.coordinates[1]],
      getIcon: d => this.getIconName(d.numberOfParts),
      getSize: 5,
    });

    // Change map layer here
    map.addLayer(iconLayer);
    return map;
  }

  public resize(): void {
    if (this.map) {
      this.map.resize();
    }
  }

  private renderLayers(event: mapboxGl.MapboxEvent<unknown> & mapboxGl.EventData, data: PartsCoordinates[]) {
    const currentZoom = event.target.getZoom();
    const zoom = currentZoom > 4.5 ? 20 : 10;

    if (this.currentZoom === zoom) {
      return;
    }
    this.currentZoom = zoom;
    this.map.resize();
    this.setLayers(this.map, data, zoom);
  }

  private getIconName(size: number): string {
    const digits = this.getDigits(size) - 1;
    const zeros = Array(digits).fill(0).join('');
    const denominator = +`1${zeros}`;
    return `marker-${Math.floor(size / denominator)}${zeros}`;
  }

  private getDigits(size: number): number {
    return (Math.log(size) * Math.LOG10E + 1) | 0;
  }
}
