import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'appAssetDate' })
export class AssetDatePipe implements PipeTransform {
  transform(timestamp: string): string {
    if (!timestamp) {
      return '';
    }
    const date: string = timestamp.split('T')[0];
    const time: string = timestamp.split('T')[1].split('.')[0];
    const splitDate = date.split('-');
    return `${splitDate[2]}/${splitDate[1]}/${splitDate[0]}, ${time}`;
  }
}
