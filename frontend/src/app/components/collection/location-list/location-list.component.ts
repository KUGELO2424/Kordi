import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Location } from 'app/common/location';

@Component({
  selector: 'app-location-list',
  templateUrl: './location-list.component.html',
  styleUrls: ['./location-list.component.css']
})
export class LocationListComponent implements OnInit {

  @Input() locationsData: Location[];
  locations: Location[];
  searchLocation = "";

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.locations = this.locationsData;
  }

  search() {
    this.locations = [];
    for (let location of this.locationsData) {
      if (location.city.toLocaleLowerCase().includes(this.searchLocation.toLocaleLowerCase()) 
       || location.street.toLocaleLowerCase().includes(this.searchLocation.toLocaleLowerCase())) {
        this.locations.push(location);
      }
    }
  }

}
