import { TestBed } from '@angular/core/testing';

import { PanelStateService } from './panel-state.service';

describe('PanelStateService', () => {
  let service: PanelStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PanelStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
