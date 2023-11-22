import {Component, Input} from '@angular/core';
import {Observable} from "rxjs";
import {View} from "@shared/model/view.model";

@Component({
    selector: 'app-card-metric',
    templateUrl: './card-metric.component.html',
    styleUrls: ['./card-metric.component.scss']
})
export class CardMetricComponent {
    @Input() headerLabelKey: string;
    @Input() value: Observable<View<number>>;
    @Input() footerLabelKey: string;

}
