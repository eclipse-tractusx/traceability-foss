import { Pipe, PipeTransform } from '@angular/core';

// ToDo: This can be done with CSS
@Pipe({
  name: 'shortenSerialNumber',
})
export class ShortenPipe implements PipeTransform {
  transform(serialNumber: string): string {
    if (!serialNumber) {
      return;
    }

    return serialNumber.length > 23
      ? `${serialNumber.substring(0, 10)} ... ${serialNumber.substring(serialNumber.length - 10)}`
      : serialNumber;
  }
}
