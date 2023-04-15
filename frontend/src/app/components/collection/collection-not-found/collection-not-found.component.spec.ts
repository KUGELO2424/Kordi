import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionNotFoundComponent } from './collection-not-found.component';

describe('CollectionNotFoundComponent', () => {
  let component: CollectionNotFoundComponent;
  let fixture: ComponentFixture<CollectionNotFoundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionNotFoundComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionNotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
