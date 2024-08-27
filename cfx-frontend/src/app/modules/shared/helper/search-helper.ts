import { QueryList } from "@angular/core";
import { NotificationComponent } from "@shared/modules/notification/presentation/notification.component";
import { PartsTableComponent } from "@shared/components/parts-table/parts-table.component";
import { MultiSelectAutocompleteComponent } from "@shared/components/multi-select-autocomplete/multi-select-autocomplete.component";
import { ToastService } from "@shared/components/toasts/toast.service";

export class SearchHelper {

  private resetFilterSelector(multiSelectAutocompleteComponents: QueryList<MultiSelectAutocompleteComponent>, oneFilterSet): boolean {
    for (const multiSelectAutocompleteComponent of multiSelectAutocompleteComponents) {
      multiSelectAutocompleteComponent.clickClear();
    }
    return oneFilterSet;
  };

  public resetFilterForAssetComponents(partsTableComponents: QueryList<PartsTableComponent>, oneFilterSet: boolean): boolean {
    for (const partsTableComponent of partsTableComponents) {
      oneFilterSet = this.resetFilterSelector(partsTableComponent.multiSelectAutocompleteComponents, oneFilterSet);
      partsTableComponent.resetFilterActive();
    }
    return oneFilterSet;
  };

  public resetFilterForNotificationComponents(notificationComponent: NotificationComponent, oneFilterSet: boolean): boolean {
    for (const notificationTabComponent of notificationComponent.notificationTabComponents) {
      notificationTabComponent.onFilterChange();
      notificationTabComponent.tableComponent.resetFilter();
      oneFilterSet = this.resetFilterSelector(notificationTabComponent.tableComponent.multiSelectAutocompleteComponents, oneFilterSet);
    }
    return oneFilterSet;
  };

  public resetFilterAndShowToast(isAsset: boolean, component, toastService: ToastService) {
    const filterIsSet = isAsset ? this.resetFilterForAssetComponents(component, false) : this.resetFilterForNotificationComponents(component, false);
    if (filterIsSet) {
      toastService.info('parts.input.global-search.toastInfoTitle', 'parts.input.global-search.toastInfo');

    }
  };
}