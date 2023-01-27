import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CollectionPanelInfoComponent } from './collection-panel-info.component';

describe('CollectionPanelComponent', () => {
  let component: CollectionPanelInfoComponent;
  let fixture: ComponentFixture<CollectionPanelInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollectionPanelInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionPanelInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
