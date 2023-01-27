import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionPanelCommentsComponent } from './collection-panel-comments.component';

describe('CollectionPanelCommentsComponent', () => {
  let component: CollectionPanelCommentsComponent;
  let fixture: ComponentFixture<CollectionPanelCommentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionPanelCommentsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionPanelCommentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
