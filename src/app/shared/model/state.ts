import { BehaviorSubject, Observable } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';

export class State<T> {
  private readonly store$: BehaviorSubject<T>;
  private readonly initialValue: T;
  private stateValue: T;

  constructor(initialValue: T) {
    this.initialValue = initialValue;
    this.stateValue = initialValue;
    this.store$ = new BehaviorSubject<T>(initialValue);
  }

  get observable(): Observable<T> {
    return this.store$.asObservable().pipe(distinctUntilChanged());
  }

  get snapshot(): T {
    return this.stateValue;
  }

  public update(value: T): void {
    this.stateValue = value;
    this.store$.next(this.stateValue);
  }

  public reset(): void {
    this.stateValue = this.initialValue;
    this.store$.next(this.stateValue);
  }
}
