import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-collection-list',
  templateUrl: './collection-list.component.html',
  styleUrls: ['./collection-list.component.css']
})
export class CollectionListComponent implements OnInit {

  searchTerm = "";

  constructor() { }

  ngOnInit(): void {
  }

}
