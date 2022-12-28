import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionItemsComponent } from './collection-items.component';

describe('CollectionItemsComponent', () => {
  let component: CollectionItemsComponent;
  let fixture: ComponentFixture<CollectionItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionItemsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
