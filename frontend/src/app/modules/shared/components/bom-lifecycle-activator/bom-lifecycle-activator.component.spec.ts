import {BomLifecycleActivatorComponent} from './bom-lifecycle-activator.component';
import {SharedModule} from '@shared/shared.module';
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {I18NEXT_SERVICE, I18NextModule, ITranslationService} from "angular-i18next";
import {KeycloakService} from "keycloak-angular";
import {MockedKeycloakService} from "@core/auth/mocked-keycloak.service";
import {APP_INITIALIZER} from "@angular/core";

describe('BomLifecycleActivatorComponent', () => {


    let component: BomLifecycleActivatorComponent;
    let fixture: ComponentFixture<BomLifecycleActivatorComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [BomLifecycleActivatorComponent],
            imports: [I18NextModule.forRoot(), SharedModule],
            providers: [
                {
                    provide: KeycloakService,
                    useClass: MockedKeycloakService,
                },
                {
                    provide: APP_INITIALIZER,
                    useFactory: (i18next: ITranslationService) => {
                        return () =>
                            i18next.init({
                                lng: 'en',
                                supportedLngs: ['en', 'de'],
                                resources: {},
                            });
                    },
                    deps: [I18NEXT_SERVICE],
                    multi: true,
                }]
        });
        fixture = TestBed.createComponent(BomLifecycleActivatorComponent);
        component = fixture.componentInstance;
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize bomLifecycleConfig correctly', () => {
        expect(component.bomLifecycleConfig.asBuiltActive).toBe(true);
        expect(component.bomLifecycleConfig.asPlannedActive).toBe(true);
    });

    it('should toggle asPlannedActive when toggleAsPlanned is called', () => {
        component.toggleAsPlanned();
        expect(component.bomLifecycleConfig.asPlannedActive).toBe(false);

        component.toggleAsPlanned();
        expect(component.bomLifecycleConfig.asPlannedActive).toBe(true);
    });

    it('should toggle asBuiltActive when toggleAsBuilt is called', () => {
        component.toggleAsBuilt();
        expect(component.bomLifecycleConfig.asBuiltActive).toBe(false);

        component.toggleAsBuilt();
        expect(component.bomLifecycleConfig.asBuiltActive).toBe(true);
    });


    it('should emit the correct size when only asPlannedActive is active', () => {
        let emittedSize: any;
        component.buttonClickEvent.subscribe((size) => {
            emittedSize = size;
        });

        component.toggleAsBuilt();

        expect(emittedSize).toEqual({asBuiltSize: 0, asPlannedSize: 100});
    });

    it('should emit the correct size when only asBuiltActive is active', () => {
        let emittedSize: any;
        component.buttonClickEvent.subscribe((size) => {
            emittedSize = size;
        });

        component.toggleAsPlanned();

        expect(emittedSize).toEqual({asBuiltSize: 100, asPlannedSize: 0});
    });
});
