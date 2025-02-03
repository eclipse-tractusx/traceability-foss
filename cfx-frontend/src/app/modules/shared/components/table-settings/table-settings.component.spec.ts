import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TableSettingsComponent } from './table-settings.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { SharedModule } from '@shared/shared.module';
import { of } from 'rxjs';
import { I18NextModule } from 'angular-i18next';

class MatDialogMock {
  open() {
    return {
      afterClosed: () => of(true),
    };
  }

  closeAll() {
    // Mock implementation
  }
}
describe('TableSettingsComponent', () => {
  let component: TableSettingsComponent;
  let fixture: ComponentFixture<TableSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TableSettingsComponent],
      imports: [
        SharedModule,
        I18NextModule.forRoot(),
      ],
      providers: [
        { provide: MatDialog, useClass: MatDialogMock },
        {
          provide: MatDialogRef,
          useValue: {
            close: () => { },
          },
        },
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TableSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

