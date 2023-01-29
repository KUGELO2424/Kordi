import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Location, LocationToAdd } from 'app/common/location';
import { CollectionService } from 'app/services/collection.service';
import { PanelStateService } from 'app/services/panel-state.service';
import { Message, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';

@Component({
  selector: 'app-collection-panel-locations',
  templateUrl: './collection-panel-locations.component.html',
  styleUrls: ['./collection-panel-locations.component.css']
})
export class CollectionPanelLocationsComponent implements OnInit {

  @ViewChild('dt') table: Table;
  @Input() collectionId: number;
  @Input() locationsData: Location[];

  submitted: boolean;
  locationDialog: boolean;

  locations: Location[] = [];
  pageSize: number = 10;
  totalRecords: number;
  page: number = 0;

  state: any;

  errorMessages: Message[] = [];

  form: UntypedFormGroup = new UntypedFormGroup({
    city: new UntypedFormControl('', [Validators.required, Validators.maxLength(30)]),
    street: new UntypedFormControl('', [Validators.required, Validators.maxLength(50)]),
  });

  constructor(private collectionService: CollectionService, private messageService: MessageService, 
    private stateService: PanelStateService, private router: Router, private translate: TranslateService) {
    this.state = this.stateService.state$.getValue() || {}
    this.stateService.state$.next("");
    if (Object.keys(this.state).length !== 0) {
      if ("message" in this.state) {
        this.messageService.add({severity:'success', detail: this.state.message});
      }
    }
  }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    this.locations = this.locationsData;
    this.totalRecords = this.locations.length;
  }

  saveLocation() {
    let location = new LocationToAdd();
    location.city = this.form.value.city;
    location.street = this.form.value.street;

    this.collectionService.addLocation(location, this.collectionId.toString()).subscribe({
      next: (data) => {
        this.submitted = true;
        this.locationDialog = false;
        this.table.reset();
        this.form.reset();
        this.reloadComponent(this.translate.instant('panel.location_added'));
      },
      error: (error) => {
        this.errorMessages = [
          {severity:'error', detail: this.translate.instant(error.error.error)}
        ]
      }
    })
  }

  addNewLocationDialog() {
    this.submitted = false;
    this.locationDialog = true;
  }

  deleteLocation(location: Location) {
    this.collectionService.deleteLocation(location, this.collectionId.toString()).subscribe({
      next: (data) => {
        this.reloadComponent(this.translate.instant('panel.location_deleted'));
      },
      error: (error) => {
        console.log(error);
      }
    })
  }

  hideDialog() {
    this.locationDialog = false;
    this.submitted = false;
    this.form.reset();
    this.errorMessages = [];
  }

  checkValidation(input: string){
    const validation = this.form.get(input)?.invalid && (this.form.get(input)?.dirty || this.form.get(input)?.touched)
    return validation;
  }

  reloadComponent(message: any) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.state.message = message
    this.stateService.state$.next(this.state);
    this.router.navigateByUrl('/collections/' + this.collectionId + "/panel");
  }

}
