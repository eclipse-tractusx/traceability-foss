import {
  AfterContentChecked,
  AfterContentInit,
  Component,
  ContentChildren,
  EventEmitter,
  Output,
  QueryList,
} from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { delay, map, startWith } from 'rxjs/operators';
import { LayoutFacade } from '../../abstraction/layout-facade';
import { TabItemComponent } from './tab.item.component';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss'],
})
export class TabsComponent implements AfterContentInit, AfterContentChecked {
  @ContentChildren(TabItemComponent) tabs: QueryList<TabItemComponent>;

  @Output() active: EventEmitter<string> = new EventEmitter<string>();

  public tabItems$: Observable<TabItemComponent[]>;
  public activeTab: TabItemComponent;
  public tabIndex$: Observable<number>;

  constructor(private layoutFacade: LayoutFacade) {
    this.tabIndex$ = this.layoutFacade.tabIndex$;
  }

  ngAfterContentInit(): void {
    this.tabItems$ = this.tabs.changes
      .pipe(startWith(''))
      .pipe(delay(0))
      .pipe(map(() => this.tabs.toArray()));
  }

  ngAfterContentChecked(): void {
    // choose the default tab
    // we need to wait for a next VM turn,
    // because Tab item content, will not be initialized yet
    if (!this.activeTab) {
      Promise.resolve().then(() => {
        this.tabIndex$.subscribe((index: number) => {
          this.activeTab = index !== -1 ? this.tabs.toArray()[index] : this.tabs.first;
        });
      });
    }
  }

  public selectTab(tabItem: TabItemComponent): void {
    if (this.activeTab === tabItem) {
      return;
    }

    if (this.activeTab) {
      this.activeTab.isActive = false;
    }

    this.activeTab = tabItem;
    this.active.next(this.activeTab.label);

    tabItem.isActive = true;
  }
}
