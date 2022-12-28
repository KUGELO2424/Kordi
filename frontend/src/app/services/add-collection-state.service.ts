import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AddCollectionStateService {

  state$ = new BehaviorSubject<any>(null);

  constructor() { }
}
