import { NgZone } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { CsvFilterService } from '@shared/service/csv-filter.service';
import { CsvUploadComponent } from './csv-upload.component';

describe('CsvUploadComponent', () => {
  let component: CsvUploadComponent;
  let fixture: ComponentFixture<CsvUploadComponent>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<CsvUploadComponent>>;
  let csvFilterServiceSpy: jasmine.SpyObj<CsvFilterService>;
  let ngZone: NgZone;

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
    csvFilterServiceSpy = jasmine.createSpyObj('CsvFilterService', ['parseCsvFile']);
    ngZone = new NgZone({ enableLongStackTrace: false });

    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: CsvFilterService, useValue: csvFilterServiceSpy },
        { provide: NgZone, useValue: ngZone }
      ],
      declarations: [CsvUploadComponent]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CsvUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should process dropped file and close dialog', () => {
    const fileMock = new File(['name,age\nJohn,30'], 'test.csv', { type: 'text/csv' });
    const fileEntryMock = {
      isFile: true,
      file: (callback: (file: File) => void) => callback(fileMock)
    } as unknown as FileSystemFileEntry;
    const eventMock = [{ fileEntry: fileEntryMock }] as any;

    component.onFileDrop(eventMock);

    expect(csvFilterServiceSpy.parseCsvFile).toHaveBeenCalledWith(fileMock);
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });
});
