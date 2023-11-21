import {Inject, LOCALE_ID, Pipe, PipeTransform} from "@angular/core";

@Pipe({ name: 'abbreviateNumber', pure: false })
export class AbbreviateNumberPipe implements PipeTransform {
    constructor(@Inject(LOCALE_ID) private locale: string) {}

    transform(value: number | string): string {
        // Convert value to a number if it's a string
        const numericValue = typeof value === 'string' ? parseFloat(value.replace(/,/g, '')) : value;

        if (numericValue >= 1000000) {
            if (this.locale === 'de') {
                return (numericValue / 1000000).toFixed(1) + ' Mio.';
            } else if (this.locale === 'en') {
                return (numericValue / 1000000).toFixed(1) + ' M.';
            } else {
                return String(numericValue);
            }
        } else if (numericValue > 9999999) {
            return 'Out of Range';
        } else {
            return String(numericValue);
        }
    }
}
