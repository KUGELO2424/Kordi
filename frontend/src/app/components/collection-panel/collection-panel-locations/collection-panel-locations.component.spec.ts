import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionPanelLocationsComponent } from './collection-panel-locations.component';

describe('CollectionPanelLocationsComponent', () => {
  let component: CollectionPanelLocationsComponent;
  let fixture: ComponentFixture<CollectionPanelLocationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionPanelLocationsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionPanelLocationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
