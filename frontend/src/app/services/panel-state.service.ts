import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PanelStateService {

  state$ = new BehaviorSubject<any>(null);

  constructor() { }
}
