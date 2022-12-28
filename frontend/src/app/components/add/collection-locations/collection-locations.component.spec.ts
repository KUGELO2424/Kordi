import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionLocationsComponent } from './collection-locations.component';

describe('CollectionLocationsComponent', () => {
  let component: CollectionLocationsComponent;
  let fixture: ComponentFixture<CollectionLocationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionLocationsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionLocationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
