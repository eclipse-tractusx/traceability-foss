import { renderComponent } from "@tests/test-render.utils";
import { SharedModule } from "@shared/shared.module";
import { MultiSelectAutocompleteComponent } from "@shared/components/multi-select-autocomplete/multi-select-autocomplete.component";
import { SemanticDataModel } from "@page/parts/model/parts.model";

describe('MultiSelectAutocompleteComponent', () => {
    const renderMultiSelectAutoCompleteComponent = () => {
        const placeholder = "test";
        const options = [SemanticDataModel.PARTASPLANNED, SemanticDataModel.BATCH];

        return renderComponent(MultiSelectAutocompleteComponent, {
            imports: [SharedModule],
            providers: [],
            componentProperties: { placeholder: placeholder, options: options },
        });
    };

    it('should create the component', async () => {
        const { fixture } = await renderMultiSelectAutoCompleteComponent();
        const { componentInstance } = fixture;
        expect(componentInstance).toBeTruthy();
    });

    it('should initialize with empty selectedValue when no input selectedOptions', async () => {
        const { fixture } = await renderMultiSelectAutoCompleteComponent();
        const { componentInstance } = fixture;
        expect(componentInstance.selectedValue).toEqual([]);
    });


    it('should emit selectionChange event when options are selected', async () => {
        const { fixture } = await renderMultiSelectAutoCompleteComponent();
        const { componentInstance } = fixture;

        const selectedOptions = [SemanticDataModel.BATCH];
        componentInstance.selectedOptions = selectedOptions;
        fixture.detectChanges();

        const spy = spyOn(componentInstance.selectionChange, 'emit');
        componentInstance.onSelectionChange({ value: selectedOptions });

        expect(spy).toHaveBeenCalledWith(selectedOptions);
    });
});
