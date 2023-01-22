import { TestBed } from '@angular/core/testing';

import { CollectionListStateService } from './collection-list-state.service';

describe('CollectionListStateService', () => {
  let service: CollectionListStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CollectionListStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
