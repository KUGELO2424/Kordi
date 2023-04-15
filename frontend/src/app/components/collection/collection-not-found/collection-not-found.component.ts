import { Component, Input, OnInit } from '@angular/core';
import { Location } from '@angular/common'

@Component({
  selector: 'app-collection-not-found',
  templateUrl: './collection-not-found.component.html',
  styleUrls: ['./collection-not-found.component.css']
})
export class CollectionNotFoundComponent implements OnInit {

  @Input() collectionNotFoundMessage: string;

  constructor(private location: Location) { }

  ngOnInit(): void {
  }

  back() {
    this.location.back();
  }

}
