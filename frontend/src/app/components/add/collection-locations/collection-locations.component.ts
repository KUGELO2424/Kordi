import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AddCollectionStateService } from 'app/services/add-collection-state.service';
import { Table } from 'primeng/table';
import { Location } from '../../../common/location';
@Component({
  selector: 'app-collection-locations',
  templateUrl: './collection-locations.component.html',
  styleUrls: ['./collection-locations.component.css']
})
export class CollectionLocationsComponent implements OnInit {

  @ViewChild('dt') table: Table;

  locationDialog: boolean;

  locations: Location[] = [];

  location: Location;

  submitted: boolean;

  state: any;

  form: UntypedFormGroup = new UntypedFormGroup({
    city: new UntypedFormControl('', [Validators.required, Validators.maxLength(30)]),
    street: new UntypedFormControl('', [Validators.required, Validators.maxLength(50)]),
  });

  constructor(private stateService: AddCollectionStateService) { }

  ngOnInit(): void {
    this.state = this.stateService.state$.getValue() || {}
    if (Object.keys(this.state).length === 0) {
      return;
    }
    if ("locations" in this.state && this.state.locations.length !== 0) {
      this.locations = this.state.locations;
    }
  }

  saveState() {
    this.state.locations = this.locations
    this.stateService.state$.next(this.state);
  }

  addNewLocation() {
    this.submitted = false;
    this.locationDialog = true;
  }

  saveLocation() {
    this.location = new Location();
    this.location.id = this.createId();
    this.location.city = this.form.value.city;
    this.location.street = this.form.value.street;
    this.submitted = true;
    this.locations.push(this.location);
    this.locationDialog = false;
    this.table.reset();
    this.form.reset();
  }

  deleteLocation(index: number) {
    this.locations.splice(index, 1);
    this.table.reset();
  }
  
  hideDialog() {
    this.locationDialog = false;
    this.submitted = false;
    this.form.reset();
  }

  createId(): string {
    let id = '';
    var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for ( var i = 0; i < 5; i++ ) {
        id += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return id;
  }

  checkValidation(input: string){
    const validation = this.form.get(input)?.invalid && (this.form.get(input)?.dirty || this.form.get(input)?.touched)
    return validation;
  }

}
