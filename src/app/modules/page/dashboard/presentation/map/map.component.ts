// ToDo: Complexity of this component is high

import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { IconLayer } from '@deck.gl/layers';
import { MapboxLayer } from '@deck.gl/mapbox';
import * as mapbox from 'mapbox-gl';
import { Map, NavigationControl } from 'mapbox-gl';
import { Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AssetsPerPlant, MAPPING, PartsCoordinates } from '../../model/assets-per-plant.model';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapComponent implements OnChanges {
  @Input() mapData: PartsCoordinates[];

  public assetsPerPlant: AssetsPerPlant = {} as AssetsPerPlant;
  public offsetX = 0;
  public offsetY = 0;

  private map: Map;

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

  public resize(): void {
    if (this.map) {
      this.map.resize();
    }
  }

  private getIconName(size: number): string {
    const digits = this.getDigits(size) - 1;
    const zeros = Array(digits)
      .fill(0)
      .join('');
    const denominator = +`1${zeros}`;
    return `marker-${Math.floor(size / denominator)}${zeros}`;
  }

  private getDigits(size: number): number {
    return (Math.log(size) * Math.LOG10E + 1) | 0;
  }
}
