import { Injectable } from '@angular/core';
import { Part } from '@page/parts/model/parts.model';

@Injectable({
  providedIn: 'root'
})
export class SharedPartService {
  affectedParts: Part[]
}
