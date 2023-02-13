import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificationEmailComponent } from './verification-email.component';

describe('VerificationEmailComponent', () => {
  let component: VerificationEmailComponent;
  let fixture: ComponentFixture<VerificationEmailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VerificationEmailComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerificationEmailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
