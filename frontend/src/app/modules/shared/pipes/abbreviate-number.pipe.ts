import { Inject, LOCALE_ID, Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'abbreviateNumber', pure: false })
export class AbbreviateNumberPipe implements PipeTransform {
    constructor(@Inject(LOCALE_ID) private locale: string) {}

    transform(value: string ): string {
        if(!value) {
            return;
        }
        // Convert value to a number f.ex. 1.000 to 1000
        const numericValue =  parseFloat(value.replace(/[,.]/g, ''));

        if(numericValue > 9999999) {
            return 'Out of Range';
        }

        if (numericValue >= 1000000) {
            if (this.locale === 'de') {
                return (numericValue / 1000000).toFixed(1) + ' Mio.';
            } else if (this.locale === 'en') {
                return (numericValue / 1000000).toFixed(1) + ' M.';
            } else {
                return value;
            }
        }  else {
            return value;
        }
    }
}
