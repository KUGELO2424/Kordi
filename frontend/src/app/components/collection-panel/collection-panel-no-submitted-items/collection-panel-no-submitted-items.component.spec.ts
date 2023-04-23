import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionPanelNoSubmittedItemsComponent } from './collection-panel-no-submitted-items.component';

describe('CollectionPanelNoSubmittedItemsComponent', () => {
  let component: CollectionPanelNoSubmittedItemsComponent;
  let fixture: ComponentFixture<CollectionPanelNoSubmittedItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionPanelNoSubmittedItemsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionPanelNoSubmittedItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
