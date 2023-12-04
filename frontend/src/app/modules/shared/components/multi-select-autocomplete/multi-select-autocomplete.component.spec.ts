import { renderComponent } from '@tests/test-render.utils';
import { SharedModule } from '@shared/shared.module';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import { SemanticDataModel } from '@page/parts/model/parts.model';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { DatePipe } from '@angular/common';
import { screen } from '@testing-library/angular';

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

  it('should emit data correctly when changeEvent of Datepicker is triggered', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent(true);
    const { componentInstance } = fixture;

    const inputValue = new Date('2023-10-12'); // Replace with your desired date

    // Create a mock event with the selected date
    const event: MatDatepickerInputEvent<Date> = {
      value: inputValue,
      target: undefined,
      targetElement: undefined,
    };

    // Call the function to test
    componentInstance.dateSelectionEvent(event);

    // Expectations
    expect(componentInstance.formControl.value).toBe('2023-10-12'); // Replace with your actual form control variable
    expect(componentInstance.selectedValue).toBe('2023-10-12');
    expect(componentInstance.theSearchElement).toBe('2023-10-12');
  });
});
