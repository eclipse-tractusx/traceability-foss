import { Observable } from 'rxjs';

export interface MetricData {
  metricName: string,
  value: Observable<number>,
  metricUnit: string,
}
