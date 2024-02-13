import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-country-flag-generator',
  templateUrl: './country-flag-generator.component.html',
  styleUrls: [ './sass/flag-css.scss' ],
})
export class CountryFlagGeneratorComponent {
  @Input() countryCode: string = '';
}
