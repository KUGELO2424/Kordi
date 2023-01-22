import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileDonatesComponent } from './profile-donates.component';

describe('ProfileDonatesComponent', () => {
  let component: ProfileDonatesComponent;
  let fixture: ComponentFixture<ProfileDonatesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProfileDonatesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileDonatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
