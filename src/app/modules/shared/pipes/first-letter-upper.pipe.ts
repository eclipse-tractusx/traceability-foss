import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'firstLetterToUpperCase',
})
export class FirstLetterUpperPipe implements PipeTransform {
  transform(value: string): string {
    return value.charAt(0).toUpperCase() + value.slice(1);
  }
}
