import { DatePipe } from '@angular/common';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MatSelectChange } from '@angular/material/select';
import { SemanticDataModel } from '@page/parts/model/parts.model';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import { FilterOperator } from '@shared/model/filter.model';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';

describe('MultiSelectAutocompleteComponent', () => {
  const renderMultiSelectAutoCompleteComponent = (multiple = true, prefilterValue = null) => {
    const placeholder = 'test';
    const options = [ SemanticDataModel.PARTASPLANNED, SemanticDataModel.BATCH ];

    return renderComponent(MultiSelectAutocompleteComponent, {
      imports: [ SharedModule ],
      providers: [ DatePipe, FormatPartSemanticDataModelToCamelCasePipe ],
      componentProperties: { placeholder: placeholder, options: options, prefilterValue: prefilterValue },
    });
  };

  beforeEach(() => {
    localStorage.clear();
    sessionStorage.clear();
  });


  it('should create the component', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    expect(componentInstance).toBeTruthy();
  });

  it('should initialize with empty selectedValue when no input selectedOptions', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    fixture.detectChanges();
    expect(componentInstance.selectedValue).toEqual(null);
  });


  it('should clear values when clickClear is called', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.searchInput = { value: 'initialValue' };
    componentInstance.searchElement = 'initialValue';
    componentInstance.selectedValue = [ 'initialValue' ];
    componentInstance.startDate = new Date('2022-02-04');
    componentInstance.endDate = new Date('2022-02-04');
    componentInstance.searchedOptions = [];
    componentInstance.allOptions = [];

    componentInstance.clickClear();

    // Assert
    expect(componentInstance.searchElement).toBe('');
    expect(componentInstance.selectedValue).toEqual([]);
  });

  it('should return correct display string when textSearch is true', async () => {

    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.selectedValue = [ 'TestValue' ];
    componentInstance.searchElement = 'TestValue';
    const result = componentInstance.displayValue();

    expect(result).toEqual([ 'TestValue', '' ]);
  });

  it('should return correct display string when textSearch is false and multiple is true', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.selectedValue = [ 'value1', 'value2', 'value3' ]; // Replace with your test values
    componentInstance.labelCount = 2; // Replace with the number of labels you expect

    componentInstance.searchElement = 'v';
    componentInstance.options = [
      { value: 'value1', display: 'Display1' },
      { value: 'value2', display: 'Display2' },
      { value: 'value3', display: 'Display3' },
    ];

    const result = componentInstance.displayValue();

    expect(result).toEqual([ 'value1', ' + 2 undefined' ]);
  });

  it('should return correct display string when textSearch is false and multiple is false', async () => {

    const { fixture } = await renderMultiSelectAutoCompleteComponent(false);
    const { componentInstance } = fixture;

    componentInstance.selectedValue = [ 'value1' ]; // Replace with your test value

    componentInstance.searchElement = 'v';
    componentInstance.options = [
      { value: 'value1', display: 'Display1' },
      { value: 'value2', display: 'Display2' },
      { value: 'value3', display: 'Display3' },
    ];

    const result = componentInstance.displayValue();

    expect(result).toEqual([ 'value1', '' ]);
  });


  it('should select all filtered options when val is true', async () => {

    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.searchedOptions = [
      { value: 'value1', display: 'Display1' },
      { value: 'value2', display: 'Display2' },
      { value: 'value3', display: 'Display3' },
    ];

    componentInstance.selectedValue = [ 'value1' ];

    const val = { checked: true };

    componentInstance.toggleSelectAll(val);

    expect(componentInstance.selectedValue).toEqual([ 'value1', 'value2', 'value3' ]);
  });

  it('should deselect options that are not in filteredOptions when val is false', async () => {

    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.searchedOptions = [
      { value: 'value1', display: 'Display1' },
      { value: 'value2', display: 'Display2' },
      { value: 'value3', display: 'Display3' },
    ];

    componentInstance.selectedValue = [ 'value1', 'value2' ];
    const val = { checked: false };
    componentInstance.toggleSelectAll(val);

    expect(componentInstance.selectedValue).toEqual([]);
  });

  it('should emit data correctly when changeEvent of Datepicker is triggered', async () => {

    const { fixture } = await renderMultiSelectAutoCompleteComponent(false);
    const { componentInstance } = fixture;

    const inputValue = new Date('2023-10-12'); // Replace with your desired date

    // Create a mock event with the selected date
    const event: MatDatepickerInputEvent<Date> = {
      value: inputValue,
      target: undefined,
      targetElement: undefined,
    };

    // Call the function to test
    componentInstance.startDateSelected(event);


    // Expectations
    expect(componentInstance.searchElement).toEqual('2023-10-12');
  });


  it('should emit date range correctly when changeEvent of Datepicker is triggered', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent(false);
    const { componentInstance } = fixture;

    const startDate = new Date('2023-10-12'); // Replace with your desired date

    // Create a mock event with the selected date
    const startEvent: MatDatepickerInputEvent<Date> = {
      value: startDate,
      target: undefined,
      targetElement: undefined,
    };

    // Call the function to test the start date
    componentInstance.startDateSelected(startEvent);

    // Expectations
    expect(componentInstance.searchElement).toBe('2023-10-12');

    const endDate = new Date('2023-10-20'); // Replace with your desired date

    // Create a mock event with the selected date
    const endEvent: MatDatepickerInputEvent<Date> = {
      value: endDate,
      target: undefined,
      targetElement: undefined,
    };

    // Call the function to test the end date
    componentInstance.endDateSelected(endEvent);

    // Expectations
    expect(componentInstance.searchElement).toBe('2023-10-12,2023-10-20');

    componentInstance.searchElement = '';
    componentInstance.endDate = null;
    const emptyEndEvent: MatDatepickerInputEvent<Date> = {
      value: null,
      target: undefined,
      targetElement: undefined,
    };
    componentInstance.endDateSelected(emptyEndEvent);

    expect(componentInstance.searchElement).toEqual('');
  });

  it('should filter date with dateFilter()', async function() {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    // @ts-ignore
    componentInstance.searchElement = [ '2023-12-10' ];
    componentInstance.filterColumn = 'createdDate';
    componentInstance.dateFilter();

    expect(componentInstance.formControl.value).toEqual([ {
      value: [ Object({
        value: '2023-12-10',
        strategy: 'AT_LOCAL_DATE',
      }) ], operator: 'AND',
    } ]);
  });

  it('should subscribe to searchElementChange and call filterItem when delayTimeoutId is present', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    // Mock clearTimeout function
    let clearSpy = spyOn(window, 'clearTimeout');
    let filterSpy = spyOn(componentInstance, 'filterItem');

    // Initial setup
    componentInstance.delayTimeoutId = 123; // Mocking a non-null delayTimeoutId
    const searchElementValue = 'mockValue';

    // Trigger ngOnInit
    fixture.detectChanges();
    // Emit a value to simulate the searchElementChange event

    componentInstance.searchElementChange.next(searchElementValue);
    // Expectations
    expect(clearSpy).toHaveBeenCalledWith(123);
    expect(filterSpy).toHaveBeenCalledWith(searchElementValue);
    expect(componentInstance.delayTimeoutId).toBeNull();
  });

  it('should return when calling filterItem() without searchElement', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.searchElement = '';
    componentInstance.searchedOptions = [];
    componentInstance.filterItem('');

    const option = componentInstance.searchedOptions;
    expect(option).toEqual([]);
  });

  it('should set prefilter value', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent(false, 'hello');
    const { componentInstance } = fixture;
    fixture.detectChanges();
    expect(componentInstance.searchElement).toEqual('hello');
    expect(componentInstance.selectedValue).toEqual([ 'hello' ]);

  });

  it('should stop event propagation for Enter key and Ctrl+A combination', async () => {
    // Arrange
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    const eventMock = {
      key: 'Enter',
      ctrlKey: false,
      stopPropagation: jasmine.createSpy('stopPropagation'),
    };

    // Act
    componentInstance.filterKeyCommands(eventMock);

    // Assert
    expect(eventMock.stopPropagation).toHaveBeenCalled();
  });

  it('should stop event propagation for Ctrl+A combination', async () => {
    // Arrange
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    const eventMock = {
      key: 'a',
      ctrlKey: true,
      stopPropagation: jasmine.createSpy('stopPropagation'),
    };

    // Act
    componentInstance.filterKeyCommands(eventMock);

    // Assert
    expect(eventMock.stopPropagation).toHaveBeenCalled();
  });

  it('should not stop event propagation for other keys', async () => {
    // Arrange
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    const eventMock = {
      key: 'B',
      ctrlKey: false,
      stopPropagation: jasmine.createSpy('stopPropagation'),
    };

    // Act
    componentInstance.filterKeyCommands(eventMock);

    // Assert
    expect(eventMock.stopPropagation).not.toHaveBeenCalled();
  });

  it('should not stop event propagation for space key', async () => {
    // Arrange
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    const eventMock = {
      key: ' ',
      ctrlKey: false,
      stopPropagation: jasmine.createSpy('stopPropagation'),
    };

    // Act
    componentInstance.filterKeyCommands(eventMock);

    // Assert
    expect(eventMock.stopPropagation).toHaveBeenCalled();
  });

  it('should handle selection change and update filter', async () => {
    const filterServiceSpy = jasmine.createSpyObj('FilterService', [ 'setFilter' ]);
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;

    componentInstance.filterService = filterServiceSpy;

    const matSelectChange: MatSelectChange = {
      value: [ 'Option1', 'Option2' ],
      source: {} as any,
    };

    spyOn(componentInstance.formControl, 'patchValue');
    componentInstance.onSelectionChange(matSelectChange);
    expect(componentInstance.formControl.patchValue).toHaveBeenCalledWith([ 'Option1', 'Option2' ]);

    const expectedFilterValues = [
      { value: 'Option1', strategy: FilterOperator.EQUAL },
      { value: 'Option2', strategy: FilterOperator.EQUAL },
    ];
    expect(filterServiceSpy.setFilter).toHaveBeenCalledWith(componentInstance.tableType, {
      [componentInstance.filterColumn]: { value: expectedFilterValues, operator: 'OR' },
    });
  });

  describe('handleDateFilterChange', () => {
    it('should clear dates when no filter attribute is present', async () => {
      const { fixture } = await renderMultiSelectAutoCompleteComponent();
      const { componentInstance } = fixture;

      // Access private method via type assertion
      (componentInstance as any).handleDateFilterChange({});

      expect(componentInstance.startDate).toBeNull();
      expect(componentInstance.endDate).toBeNull();
    });

    it('should clear dates when filter attribute has no value', async () => {
      const { fixture } = await renderMultiSelectAutoCompleteComponent();
      const { componentInstance } = fixture;
      componentInstance.filterColumn = 'testColumn';

      // Access private method via type assertion
      (componentInstance as any).handleDateFilterChange({
        testColumn: {},
      });

      expect(componentInstance.startDate).toBeNull();
      expect(componentInstance.endDate).toBeNull();
    });

    it('should set startDate for AT_LOCAL_DATE strategy', async () => {
      const { fixture } = await renderMultiSelectAutoCompleteComponent();
      const { componentInstance } = fixture;
      componentInstance.filterColumn = 'testColumn';
      const testDate = '2023-01-01';

      // Access private method via type assertion
      (componentInstance as any).handleDateFilterChange({
        testColumn: {
          value: [ {
            strategy: FilterOperator.AT_LOCAL_DATE,
            value: testDate,
          } ],
        },
      });

      expect(componentInstance.startDate).toEqual(new Date(testDate));
      expect(componentInstance.endDate).toBe(undefined);
    });

    it('should set startDate for AFTER_LOCAL_DATE strategy', async () => {
      const { fixture } = await renderMultiSelectAutoCompleteComponent();
      const { componentInstance } = fixture;
      componentInstance.filterColumn = 'testColumn';
      const testDate = '2023-01-01';

      // Access private method via type assertion
      (componentInstance as any).handleDateFilterChange({
        testColumn: {
          value: [ {
            strategy: FilterOperator.AFTER_LOCAL_DATE,
            value: testDate,
          } ],
        },
      });

      expect(componentInstance.startDate).toEqual(new Date(testDate));
      expect(componentInstance.endDate).toBe(undefined);
    });

    it('should set endDate for BEFORE_LOCAL_DATE strategy', async () => {
      const { fixture } = await renderMultiSelectAutoCompleteComponent();
      const { componentInstance } = fixture;
      componentInstance.filterColumn = 'testColumn';
      const testDate = '2023-01-01';

      // Access private method via type assertion
      (componentInstance as any).handleDateFilterChange({
        testColumn: {
          value: [ {
            strategy: FilterOperator.BEFORE_LOCAL_DATE,
            value: testDate,
          } ],
        },
      });

      expect(componentInstance.startDate).toBe(undefined);
      expect(componentInstance.endDate).toEqual(new Date(testDate));
    });

    it('should handle multiple date filters correctly', async () => {
      const { fixture } = await renderMultiSelectAutoCompleteComponent();
      const { componentInstance } = fixture;
      componentInstance.filterColumn = 'testColumn';
      const startDate = '2023-01-01';
      const endDate = '2023-12-31';

      // Access private method via type assertion
      (componentInstance as any).handleDateFilterChange({
        testColumn: {
          value: [
            {
              strategy: FilterOperator.AT_LOCAL_DATE,
              value: startDate,
            },
            {
              strategy: FilterOperator.BEFORE_LOCAL_DATE,
              value: endDate,
            },
          ],
        },
      });

      expect(componentInstance.startDate).toEqual(new Date(startDate));
      expect(componentInstance.endDate).toEqual(new Date(endDate));
    });
  });

  it('should call filterItem when opened and options are undefined', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    spyOn(componentInstance as any, 'filterItem');
    spyOn(componentInstance as any, 'clickClear');
    (componentInstance as any).options = undefined;
    (componentInstance as any).searchElement = 'abc';

    componentInstance.handleOpen(true);

    expect((componentInstance as any).filterItem).toHaveBeenCalledWith('abc');
    expect((componentInstance as any).clickClear).not.toHaveBeenCalled();
  });

  it('should call filterItem when opened and options are empty', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    spyOn(componentInstance as any, 'filterItem');
    spyOn(componentInstance as any, 'clickClear');
    (componentInstance as any).options = [];
    (componentInstance as any).searchElement = '';

    componentInstance.handleOpen(true);

    expect((componentInstance as any).filterItem).toHaveBeenCalledWith('');
    expect((componentInstance as any).clickClear).not.toHaveBeenCalled();
  });

  it('should call clickClear when opened and options are present', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    spyOn(componentInstance as any, 'filterItem');
    spyOn(componentInstance as any, 'clickClear');
    (componentInstance as any).options = [ 'something' ];
    (componentInstance as any).searchElement = 'abc';

    componentInstance.handleOpen(true);

    expect((componentInstance as any).filterItem).not.toHaveBeenCalled();
    expect((componentInstance as any).clickClear).toHaveBeenCalled();
  });

  it('should call clickClear when not opened', async () => {
    const { fixture } = await renderMultiSelectAutoCompleteComponent();
    const { componentInstance } = fixture;
    spyOn(componentInstance as any, 'filterItem');
    spyOn(componentInstance as any, 'clickClear');
    (componentInstance as any).options = undefined;
    (componentInstance as any).searchElement = 'abc';

    componentInstance.handleOpen(false);

    expect((componentInstance as any).filterItem).not.toHaveBeenCalled();
    expect((componentInstance as any).clickClear).toHaveBeenCalled();
  });

});
