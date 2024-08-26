import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'abbreviateNumber', pure: false })
export class AbbreviateNumberPipe implements PipeTransform {

  transform(value: string): string {
    if (!value) {
      return;
    }
    // Convert value to a number f.ex. 1.000 to 1000
    const numericValue = parseFloat(value.replace(/[,.]/g, ''));

    if (numericValue > 9999999) {
      return 'Out of Range';
    }

    if (numericValue >= 1000000) {
      return (numericValue / 1000000).toFixed(1);
    } else {
      return String(value);
    }
  }
}
