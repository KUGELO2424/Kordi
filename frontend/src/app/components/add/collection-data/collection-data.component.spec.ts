import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionDataComponent } from './collection-data.component';

describe('CollectionDataComponent', () => {
  let component: CollectionDataComponent;
  let fixture: ComponentFixture<CollectionDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionDataComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
