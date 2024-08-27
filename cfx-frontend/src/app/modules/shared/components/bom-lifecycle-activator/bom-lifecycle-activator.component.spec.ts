import { BomLifecycleActivatorComponent } from './bom-lifecycle-activator.component';
import { SharedModule } from '@shared/shared.module';
import { BomLifecycleSettingsService, UserSettingView } from "@shared/service/bom-lifecycle-settings.service";
import { renderComponent } from "@tests/test-render.utils";
import { BomLifecycleType } from './bom-lifecycle-activator.model';

describe('BomLifecycleActivatorComponent', () => {

    const renderBomLifecycleActivator = (view: UserSettingView = UserSettingView.PARTS) => {
        return renderComponent(BomLifecycleActivatorComponent, {
            imports: [SharedModule],
            providers: [BomLifecycleSettingsService],
            componentProperties: { view },
        });
    };

    it('should create the component', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;
        expect(componentInstance).toBeTruthy();
    });

    it('should initialize bomLifecycleConfig correctly', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;
        componentInstance.bomLifecycleConfig = componentInstance.bomLifeCycleUserSetting.getUserSettings(componentInstance.view);

        expect(componentInstance.bomLifecycleConfig.asBuiltActive).toBe(true);
        expect(componentInstance.bomLifecycleConfig.asPlannedActive).toBe(true);
        expect(componentInstance.bomLifecycleConfig.asDesignedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asOrderedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asSupportedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asRecycledActive).toBe(false);
    });

    it('should disable all selected lifecycles when cleared', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;

        componentInstance.disabledAllLifecycleStates()

        expect(componentInstance.bomLifecycleConfig.asBuiltActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asPlannedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asDesignedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asOrderedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asSupportedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asRecycledActive).toBe(false);

        componentInstance.selectionChanged([BomLifecycleType.AS_PLANNED, BomLifecycleType.AS_BUILT]);
    });

    it('should return true if lifecycle is selected', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;
        componentInstance.selectedLifecycles = [BomLifecycleType.AS_DESIGNED];

        expect(componentInstance.isSelected(BomLifecycleType.AS_DESIGNED)).toBe(true);

        componentInstance.selectionChanged([BomLifecycleType.AS_PLANNED, BomLifecycleType.AS_BUILT]);

    });

    it('should deselect lifecycle when removed is called', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;
        componentInstance.selectionChanged([BomLifecycleType.AS_DESIGNED])

        expect(componentInstance.bomLifecycleConfig.asDesignedActive).toBe(true);
        componentInstance.removeLifeCycle(BomLifecycleType.AS_DESIGNED);

        expect(componentInstance.bomLifecycleConfig.asDesignedActive).toBe(false);

        componentInstance.selectionChanged([BomLifecycleType.AS_PLANNED, BomLifecycleType.AS_BUILT]);

    });

    it('should disable option if 2 options are already selected', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;
        componentInstance.selectedLifecycles = [BomLifecycleType.AS_PLANNED, BomLifecycleType.AS_BUILT];

        componentInstance.selectionChanged([BomLifecycleType.AS_PLANNED, BomLifecycleType.AS_BUILT])
        expect(componentInstance.disableOption(BomLifecycleType.AS_DESIGNED)).toBe(true);
    });

    it('should not disable option if less than 2 options are selected', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;
        componentInstance.selectedLifecycles = [];

        expect(componentInstance.disableOption(BomLifecycleType.AS_PLANNED)).toBe(false);
        componentInstance.selectedLifecycles = [BomLifecycleType.AS_PLANNED];

        expect(componentInstance.disableOption(BomLifecycleType.AS_BUILT)).toBe(false);
        componentInstance.selectionChanged([BomLifecycleType.AS_PLANNED, BomLifecycleType.AS_BUILT]);
    });

    it('should update the lifecycles when the selection has changed', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;

        componentInstance.selectedLifecycles = [];
        componentInstance.selectionChanged([BomLifecycleType.AS_DESIGNED, BomLifecycleType.AS_SUPPORTED]);

        expect(componentInstance.bomLifecycleConfig.asBuiltActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asPlannedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asRecycledActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asOrderedActive).toBe(false);
        expect(componentInstance.bomLifecycleConfig.asSupportedActive).toBe(true);
        expect(componentInstance.bomLifecycleConfig.asDesignedActive).toBe(true);

        componentInstance.selectionChanged([BomLifecycleType.AS_PLANNED, BomLifecycleType.AS_BUILT]);
    });

    it('should update the lifecycle state when updateLifecycleConfig is called', async () => {
        const { fixture } = await renderBomLifecycleActivator(UserSettingView.PARTS);
        const { componentInstance } = fixture;
        componentInstance.selectedLifecycles = [];

        expect(componentInstance.bomLifecycleConfig.asOrderedActive).toBe(false);

        componentInstance.updateLifecycleConfig(BomLifecycleType.AS_ORDERED, true);

        expect(componentInstance.bomLifecycleConfig.asOrderedActive).toBe(true);
        componentInstance.selectionChanged([BomLifecycleType.AS_PLANNED, BomLifecycleType.AS_BUILT]);
    });
});
