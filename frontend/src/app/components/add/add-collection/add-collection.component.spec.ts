import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCollectionComponent } from './add-collection.component';

describe('AddCollectionComponent', () => {
  let component: AddCollectionComponent;
  let fixture: ComponentFixture<AddCollectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddCollectionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddCollectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
