import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ItemListComponent implements OnInit {

  values: any[] = [{value: 0}, {value:0}, {value:0}, {value:0}, {value:0}, {value:0}, {value:0}];

  constructor() { }

  ngOnInit(): void {
  }

}
