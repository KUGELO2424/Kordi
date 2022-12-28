import { TestBed } from '@angular/core/testing';

import { AddCollectionStateService } from './add-collection-state.service';

describe('AddCollectionStateService', () => {
  let service: AddCollectionStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddCollectionStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
