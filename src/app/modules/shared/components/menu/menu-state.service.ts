import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

// https://indepth.dev/posts/1297/building-a-reusable-menu-component
@Injectable({
  providedIn: 'root',
})
export class MenuStateService {
  public state$: Observable<void>;
  public menuId = new BehaviorSubject<string>(null);

  private state = new Subject<void>();

  constructor() {
    this.state$ = this.state.asObservable();
  }

  public clearMenu(): void {
    this.state.next();
  }
}
