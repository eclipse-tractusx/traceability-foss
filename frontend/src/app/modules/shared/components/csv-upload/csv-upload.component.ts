import { Component, NgZone } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { CsvFilterService } from '@shared/service/csv-filter.service';
import { FileSystemFileEntry, NgxFileDropEntry } from 'ngx-file-drop';

@Component({
  selector: 'app-file-upload',
  templateUrl: './csv-upload.component.html',
  styleUrls: [ './csv-upload.component.scss' ],
})
export class CsvUploadComponent {
  constructor(private readonly dialogRef: MatDialogRef<CsvUploadComponent>,
              private readonly csvFilterService: CsvFilterService,
              private readonly ngZone: NgZone) {
  }

  onFileDrop(event: NgxFileDropEntry[]) {
    for (const droppedFile of event) {
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          this.ngZone.run(() => {
            this.csvFilterService.parseCsvFile(file);
            this.dialogRef.close();
          });
        });
      }
    }
  }
}


