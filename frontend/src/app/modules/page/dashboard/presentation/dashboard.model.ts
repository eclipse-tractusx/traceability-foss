import {Observable} from "rxjs";
import {View} from "@shared/model/view.model";

export interface MetricData {
    metricName: string,
    value: Observable<View<number>>,
    metricUnit: string,
}
