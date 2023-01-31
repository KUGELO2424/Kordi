import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionPanelSubmittedItemsComponent } from './collection-panel-submitted-items.component';

describe('CollectionPanelSubmittedItemsComponent', () => {
  let component: CollectionPanelSubmittedItemsComponent;
  let fixture: ComponentFixture<CollectionPanelSubmittedItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionPanelSubmittedItemsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionPanelSubmittedItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
