import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionClosedComponent } from './collection-closed.component';

describe('CollectionClosedComponent', () => {
  let component: CollectionClosedComponent;
  let fixture: ComponentFixture<CollectionClosedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionClosedComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionClosedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
