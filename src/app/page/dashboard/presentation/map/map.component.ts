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

import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { environment } from 'src/environments/environment';
import * as mapbox from 'mapbox-gl';
import { Map, NavigationControl } from 'mapbox-gl';
import { MapboxLayer } from '@deck.gl/mapbox';
import { IconLayer } from '@deck.gl/layers';
import { Observable, of } from 'rxjs';
import { AssetsPerPlant, MAPPING, PartsCoordinates } from '../../model/assets-per-plant.model';

/**
 *
 *
 * @export
 * @class MapComponent
 * @implements {OnChanges}
 */
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapComponent implements OnChanges {
  /**
   * Map data
   *
   * @type {MapChart[]}
   * @memberof MapComponent
   */
  @Input() mapData: PartsCoordinates[];

  /**
   * Assets per plant hover object
   *
   * @type {AssetsPerPlant}
   * @memberof MapComponent
   */
  public assetsPerPlant: AssetsPerPlant = {} as AssetsPerPlant;

  /**
   * X axis
   * @type {number}
   * @memberof MapComponent
   */
  public offsetX = 0;

  /**
   * Y axis
   * @type {number}
   * @memberof MapComponent
   */
  public offsetY = 0;

  /**
   * Mapbox element
   *
   * @private
   * @type {Map}
   * @memberof MapComponent
   */
  private map: Map;

  /**
   * Angular lifecycle method - On Changes
   *
   * @param {SimpleChanges} changes
   * @return {void}
   * @memberof MapComponent
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes.mapData) {
      Object.getOwnPropertyDescriptor(mapbox, 'accessToken').set(environment.mapBoxAccessToken);
      if (this.map) {
        this.map.remove();
      }
      const data: PartsCoordinates[] = this.mapData;

      this.renderMap(data);
    }
  }

  /**
   * Map renderer
   *
   * @param {MapChart[]} data
   * @return {void}
   * @memberof MapComponent
   */
  public renderMap(data: PartsCoordinates[]): void {
    this.map = new Map({
      container: 'map',
      style: 'mapbox://styles/mapbox/light-v10?optimize=true',
      center: { lng: 12.4632953, lat: 47.6482795 },
      zoom: 3,
      maxZoom: 13,
      minZoom: 1,
      pitch: 20,
      attributionControl: false,
    });

    this.map.addControl(
      new NavigationControl({
        showZoom: true,
        showCompass: true,
        visualizePitch: true,
      }),
      'top-right',
    );

    this.map.on('load', () => {
      this.map.resize();
      this.setLayers(this.map, data);
    });
  }

  /**
   * Map layers setter
   *
   * @param {Map} map
   * @param {MapChart[]} data
   * @return {Observable<Map>}
   * @memberof MapComponent
   */
  public setLayers(map: Map, data: PartsCoordinates[]): Observable<Map> {
    const layer = map.getLayer('iconLayer');

    if (layer) {
      map.removeLayer('iconLayer');
    }

    const layerProps = {
      data,
      pickable: true,
      iconAtlas: '../assets/images/location-icon.png',
      iconMapping: MAPPING,
      getPosition: d => [+d.coordinates[1], +d.coordinates[0]],
    };

    const iconLayer = new MapboxLayer({
      id: 'icon-layer',
      type: IconLayer,
      ...layerProps,
      getIcon: d => this.getIconName(d.numberOfParts),
      sizeScale: 20,
      getSize: 5,
    });

    // Change map layer here
    map.addLayer(iconLayer);
    return of(map);
  }

  /**
   * Map resize event
   *
   * @return {void}
   * @memberof MapComponent
   */
  public resize(): void {
    if (this.map) {
      this.map.resize();
    }
  }

  /**
   * Get marker depending on the asset count
   *
   * @private
   * @param {number} size
   * @return {string}
   * @memberof MapComponent
   */
  private getIconName(size: number): string {
    const digits = this.getDigits(size) - 1;
    const zeros = Array(digits)
      .fill(0)
      .join('');
    const denominator = +`1${zeros}`;
    return `marker-${Math.floor(size / denominator)}${zeros}`;
  }

  /**
   * Get digits of a number
   *
   * @private
   * @param {number} size
   * @return {number}
   * @memberof MapComponent
   */
  private getDigits(size: number): number {
    return (Math.log(size) * Math.LOG10E + 1) | 0;
  }
}
