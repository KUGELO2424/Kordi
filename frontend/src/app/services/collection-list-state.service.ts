import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CollectionListStateService {

  state$ = new BehaviorSubject<any>(null);

  constructor() { }
}
