import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'appDateSplit' })
export class DateSplitPipe implements PipeTransform {
  transform(date: string): string {
    if (!date) {
      return '';
    }
    const dateWithoutTime = date.split('T')[0];
    const splitDate = dateWithoutTime.split('-');
    return `${splitDate[2]}/${splitDate[1]}/${splitDate[0]}`;
  }
}
