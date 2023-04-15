import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common'


@Component({
  selector: 'app-collection-closed',
  templateUrl: './collection-closed.component.html',
  styleUrls: ['./collection-closed.component.css']
})
export class CollectionClosedComponent implements OnInit {

  collectionNotFound: boolean = false;
  collectionNotFoundMessage: string;

  constructor(private location: Location) { }

  ngOnInit(): void {
  }

  back() {
    this.location.back();
  }

}
