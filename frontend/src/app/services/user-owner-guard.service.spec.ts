import { TestBed } from '@angular/core/testing';

import { UserOwnerGuardService } from './user-owner-guard.service';

describe('UserOwnerGuardService', () => {
  let service: UserOwnerGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserOwnerGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
