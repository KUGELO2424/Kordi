import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-collection-panel-no-submitted-items',
  templateUrl: './collection-panel-no-submitted-items.component.html',
  styleUrls: ['./collection-panel-no-submitted-items.component.css']
})
export class CollectionPanelNoSubmittedItemsComponent implements OnInit {

  @Input() message: string;

  constructor() { }

  ngOnInit(): void {
  }

}
