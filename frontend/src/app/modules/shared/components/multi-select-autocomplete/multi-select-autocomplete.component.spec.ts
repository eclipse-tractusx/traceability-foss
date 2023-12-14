import { renderComponent } from '@tests/test-render.utils';
import { SharedModule } from '@shared/shared.module';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import { SemanticDataModel } from '@page/parts/model/parts.model';
import { DatePipe } from '@angular/common';
import { screen, waitFor } from '@testing-library/angular';
import { Severity } from '@shared/model/severity.model';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';

describe('MultiSelectAutocompleteComponent', () => {
  const renderMultiSelectAutoCompleteComponent = (isDate = false, multiple = false, filterActive = '') => {
    const placeholder = 'test';
    let options: Array<any>;
    let textSearch = true;
    let searched = false;
    if (isDate) {
      multiple = false;
      textSearch = false;
    } else if (multiple) {
      textSearch = false;
    }
    if (filterActive !== '') {
      searched = true;
    }
    options = [
      {
        display: 'semanticDataModels.' + SemanticDataModel.PARTASPLANNED,
        value: SemanticDataModel.PARTASPLANNED,
        checked: false,
      },
      {
        display: 'semanticDataModels.' + SemanticDataModel.BATCH,
        value: SemanticDataModel.BATCH,
        checked: false,
      },
    ];

    return renderComponent(MultiSelectAutocompleteComponent, {
      imports: [SharedModule],
      providers: [DatePipe],
      componentProperties: {
        placeholder: placeholder,
        options: options,
        multiple,
        textSearch,
        isDate,
        filterActive,
        searched,
      },
    });
  };

  it('should create the component', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    expect(componentInstance).toBeTruthy();
  });

  it('should create the textComponent when it gets a textSearch but not render the button', async () => {
    await renderMultiSelectAutoCompleteComponent(false, false);

    const selectTextElement = screen.getByTestId('multi-select-autocomplete--text-search-input');
    expect(selectTextElement).toBeInTheDocument();
    try {
      screen.getByTestId('multi-select-autocomplete--text-search-button');
    } catch (error) {
      expect(error).toBeTruthy();
    }
    //    const selectItemElements = screen.getAllByTestId('select-one--test-id');
    //    expect(selectItemElements.length).toBe(tableSize);
  });

  it('should create the textComponent when it gets a textSearch', async () => {
    await renderMultiSelectAutoCompleteComponent(false, false, 'Test');

    const selectTextElement = screen.getByTestId('multi-select-autocomplete--text-search-input');
    expect(selectTextElement).toBeInTheDocument();

    const selectButtonElement = screen.getByTestId('multi-select-autocomplete--text-search-button');
    expect(selectButtonElement).toBeInTheDocument();
  });

  it('should create options selection with the give options and an select all option.', async () => {
    await renderMultiSelectAutoCompleteComponent(false, true);

    const selectTextElement = screen.getByTestId('multi-select-autocomplete--multi-option-search-selct-all');
    expect(selectTextElement).toBeInTheDocument();

    const selectItemElements = screen.getAllByTestId('multi-select-autocomplete--multi-option-search-selct-one');
    expect(selectItemElements.length).toBe(2);
  });
  it('should create the date selection.', async () => {
    await renderMultiSelectAutoCompleteComponent(true);

    const selectTextElement = screen.getByTestId('multi-select-autocomplete--date-search-form');
    expect(selectTextElement).toBeInTheDocument();
  });

  it('should clear values when clickClear is called', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.searchInput = { value: 'initialValue' };
    componentInstance.theSearchElement = 'initialValue';
    componentInstance.selectedValue = ['initialValue'];

    componentInstance.clickClear();

    // Assert
    expect(componentInstance.searchInput.value).toBe('');
    expect(componentInstance.theSearchElement).toBe(null);
    expect(componentInstance.selectedValue).toEqual([]);
  });

  it('should clear values when clickClear is called from a external source', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.searchInput = { value: 'initialValue' };
    componentInstance.theSearchElement = 'initialValue';
    componentInstance.selectedValue = ['initialValue'];
    componentInstance.searched = true;
    componentInstance.filterActive = 'initialValue';
    componentInstance.formControl.patchValue('initalValue');


    const wasSet = componentInstance.clickClear(true);

    // Assert
    expect(componentInstance.searchInput.value).toBe('');
    expect(componentInstance.theSearchElement).toBe(null);
    expect(componentInstance.selectedValue).toEqual([]);
    expect(wasSet).toBe(true);
    expect(componentInstance.searched).toBe(false);
    expect(componentInstance.filterActive).toBe('');

  });

  it('should select all options when val is true for toggleAll', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.options = [
      { value: 'value1', display: 'Display1', checked: false },
      { value: 'value2', display: 'Display2', checked: false },
      { value: 'value3', display: 'Display3', checked: false },
    ];

    const val = { checked: true };
    componentInstance.toggleSelect(val, true);

    expect(componentInstance.options).toEqual([
      { value: 'value1', display: 'Display1', checked: true },
      { value: 'value2', display: 'Display2', checked: true },
      { value: 'value3', display: 'Display3', checked: true },
    ]);
  });

  it('should deselect all options when val is false for toggleAll', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.options = [
      { value: 'value1', display: 'Display1', checked: true },
      { value: 'value2', display: 'Display2', checked: true },
      { value: 'value3', display: 'Display3', checked: true },
    ];

    const val = { checked: false };

    componentInstance.toggleSelect(val, true);

    expect(componentInstance.options).toEqual([
      { value: 'value1', display: 'Display1', checked: false },
      { value: 'value2', display: 'Display2', checked: false },
      { value: 'value3', display: 'Display3', checked: false },
    ]);
  });

  it('should turn the selectAllChecked marker on if all options where manualy selected', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.options = [
      { value: 'value1', display: 'Display1', checked: true },
      { value: 'value2', display: 'Display2', checked: true },
      { value: 'value3', display: 'Display3', checked: true },
    ];
    const val = { checked: true };

    componentInstance.toggleSelect(val, false);

    expect(componentInstance.selectAllChecked).toEqual(true);
  });

  it('should turn the selectAllChecked marker off if one option was manualy deselected after all where selected', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.options = [
      { value: 'value1', display: 'Display1', checked: true },
      { value: 'value2', display: 'Display2', checked: true },
      { value: 'value3', display: 'Display3', checked: false },
    ];
    componentInstance.selectAllChecked = true;

    const val = { checked: false };

    componentInstance.toggleSelect(val, false);

    expect(componentInstance.selectAllChecked).toEqual(false);
  });

  it('someSelected should return true only if not all are selected and at least one option is true ', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.options = [
      { value: 'value1', display: 'Display1', checked: true },
      { value: 'value2', display: 'Display2', checked: false },
      { value: 'value3', display: 'Display3', checked: false },
    ];

    expect(componentInstance.someSelected()).toEqual(true);
    componentInstance.options = [
      { value: 'value1', display: 'Display1', checked: true },
      { value: 'value2', display: 'Display2', checked: true },
      { value: 'value3', display: 'Display3', checked: true },
    ];
    componentInstance.selectAllChecked = true;

    expect(componentInstance.someSelected()).toEqual(false);
  });

  it('should set the filterActive the theSearchElement that was typed in', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    componentInstance.theSearchElement = 'Search value';
    componentInstance.setFilterActive();

    expect(componentInstance.filterActive).toEqual('Search value');
  });

  it('should emit data correctly when the date is selected in the calender', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent(true);
    const { componentInstance } = fixture;

    const date = new Date(2023, 9, 12);
    componentInstance.theSearchDate.patchValue(date);
    componentInstance.runningTimer = true;
    spyOn(componentInstance.triggerFilter, 'emit');

    // Call the function to test
    const event: MatDatepickerInputEvent<Date> = { target: undefined, targetElement: undefined, value: componentInstance.theSearchDate.value };
    componentInstance.dateSelectionEvent(event);

    // Expectations
    expect(componentInstance.formControl.value).toEqual('2023-10-12');
    expect(componentInstance.runningTimer).toEqual(false);
    expect(componentInstance.cleared).toEqual(false);
    expect(componentInstance.triggerFilter.emit).toHaveBeenCalled();
  });

  it('should format the data correctly when the date is manually typed into the date filter.', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent(true);
    const { componentInstance } = fixture;

    componentInstance.theSearchDate.patchValue(new Date(2023, 9, 12));
    componentInstance.runningTimer = true;
    spyOn(componentInstance.triggerFilter, 'emit');

    // Call the function to test
    componentInstance.onBlur('dateFilter');

    // Expectations
    expect(componentInstance.formControl.value).toBe('2023-10-12');
    expect(componentInstance.runningTimer).toBe(false);
    expect(componentInstance.cleared).toBe(false);
    expect(componentInstance.triggerFilter.emit).toHaveBeenCalled();
  });


  it('should reset the date search only once if input empty.', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent(true);
    const { componentInstance } = fixture;

    componentInstance.runningTimer = true;
    componentInstance.formControl.patchValue('2023-12-06');
    componentInstance.theSearchDate.patchValue(null);
    componentInstance.cleared = false;
    spyOn(componentInstance.triggerFilter, 'emit');

    // Call the function to test
    componentInstance.triggerFiltering('date');
    componentInstance.triggerFiltering('date');
    // Expectations
    expect(componentInstance.formControl.value).toBe(null);
    expect(componentInstance.runningTimer).toBe(false);
    expect(componentInstance.triggerFilter.emit).toHaveBeenCalledTimes(1);
  });

  it('should create the correct class lists for the status options', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent(false, true);
    const { componentInstance } = fixture;

    //modify wanted changes
    componentInstance.options = [{
      display: 'status.ACCEPTED',
      value: 'ACCEPTED',
      checked: false,
      displayClass: 'notification-display-status--ACCEPTED',
    },
    {
      display: 'status.DECLINDED',
      value: 'DECLINDED',
      checked: false,
      displayClass: 'notification-display-status--DECLINDED',
    }];
    componentInstance.optionClasses = {};

    //apply changes via ngOnInit();
    componentInstance.ngOnInit();

    //expected value
    const expectedClasses = {
      'status.ACCEPTED': { 'body-large': true, 'notification-display-status': true, 'notification-display-status--ACCEPTED': true },
      'status.DECLINDED': { 'body-large': true, 'notification-display-status': true, 'notification-display-status--DECLINDED': true },
    }

    //expect
    expect(componentInstance.optionClasses).toEqual(expectedClasses);
  });

  it('should create the correct class lists for the severity options', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent(false, true);
    const { componentInstance } = fixture;

    //modify wanted changes
    componentInstance.multiple = true;
    componentInstance.isDate = false;
    componentInstance.textSearch = false;
    componentInstance.options = [
      {
        display: 'severity.' + Severity.MINOR,
        value: 0,
        checked: false,
        severity: Severity.MINOR,
      },
      {
        display: 'severity.' + Severity.MAJOR,
        value: 1,
        checked: false,
        severity: Severity.MAJOR,
      },
      {
        display: 'severity.' + Severity.CRITICAL,
        value: 2,
        checked: false,
        severity: Severity.CRITICAL
      },
      {
        display: 'severity.' + Severity.LIFE_THREATENING,
        value: 3,
        checked: false,
        severity: Severity.LIFE_THREATENING,
      },
    ];
    componentInstance.optionClasses = {};

    //apply changes via ngOnInit();
    componentInstance.ngOnInit();

    //expected value
    const expectedClasses = {
      'severity.MINOR': { 'body-large': true, 'notification-display-severity': true },
      'severity.MAJOR': { 'body-large': true, 'notification-display-severity': true },
      'severity.CRITICAL': { 'body-large': true, 'notification-display-severity': true },
      'severity.LIFE-THREATENING': { 'body-large': true, 'notification-display-severity': true },
    }

    //epxect
    expect(componentInstance.optionClasses).toEqual(expectedClasses);
    expect(componentInstance.isSeverity).toEqual(true);

  });

  it('should trigger text search when the onBlur function is called.', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    //setup
    componentInstance.runningTimer = true;
    componentInstance.theSearchElement = 'Search Value';
    spyOn(componentInstance.triggerFilter, 'emit');

    // Call the function to test
    componentInstance.onBlur('textFilter');

    // Expectations
    expect(componentInstance.filterActive).toEqual('Search Value');
    expect(componentInstance.runningTimer).toBe(false);
    expect(componentInstance.triggerFilter.emit).toHaveBeenCalled();
  });

  it('should trigger option search when the onBlur function is called.', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    //setup
    componentInstance.runningTimer = true;
    spyOn(componentInstance.triggerFilter, 'emit');

    // Call the function to test
    componentInstance.onBlur('selectionFilter');

    // Expectations
    expect(componentInstance.runningTimer).toBe(false);
    expect(componentInstance.triggerFilter.emit).toHaveBeenCalled();
  });

});
