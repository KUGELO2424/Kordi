import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionPanelItemsComponent } from './collection-panel-items.component';

describe('CollectionPanelItemsComponent', () => {
  let component: CollectionPanelItemsComponent;
  let fixture: ComponentFixture<CollectionPanelItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionPanelItemsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionPanelItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
