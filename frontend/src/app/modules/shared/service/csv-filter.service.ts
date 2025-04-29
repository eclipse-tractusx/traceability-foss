import { Injectable } from '@angular/core';
import { ToastService } from '@shared/components/toasts/toast.service';
import * as Papa from 'papaparse';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CsvFilterService {

  private readonly filterValues = new BehaviorSubject<Record<string, string[]>>({});
  searchValues$ = this.filterValues.asObservable();

  constructor(public toastService: ToastService) {
  }

  setValues(values: Record<string, string[]>) {
    this.filterValues.next(values);
  }

  parseCsvFile(file: File) {
    Papa.parse(file, {
      complete: (result) => {
        if (this.isValidCsv(file)) {
          this.handleCsvData(result.data);
        }
      },
      header: true,
      skipEmptyLines: true,
    });
  }

  handleCsvData(csvData: any[]) {
    const allowedColumns = [
      'id',
      'idShort',
      'semanticModelId',
      'manufacturerPartId',
      'customerPartId',
      'businessPartner'
    ];

    const searchMap: Record<string, string[]> = {};

    allowedColumns.forEach(column => {
      searchMap[column] = [];
    });

    csvData.forEach(row => {
      allowedColumns.forEach(column => {
        if (row[column]) {
          searchMap[column].push(row[column]);
        }
      });
    });
    if(Object.values(searchMap).flat().length === 0){
      this.toastService.error("errorMessage.invalidCsvContent")
    }
    this.setValues(searchMap);
  }

  private isValidCsv(file: File) {
    if (file.name.endsWith('.csv') || file.type === 'text/csv') {
      return true;
    }
    this.toastService.error('errorMessage.invalidCsv');
    return false;
  }
}
