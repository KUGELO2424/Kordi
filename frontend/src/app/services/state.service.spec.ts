import { TestBed } from '@angular/core/testing';

import { StateService } from './state.service';

describe('AddCollectionStateService', () => {
  let service: StateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
