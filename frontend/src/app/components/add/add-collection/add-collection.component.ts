import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CollectionToAdd } from 'app/common/collectionToAdd';
import { ItemCategory, ItemToAdd, ItemType, NewItem } from 'app/common/itemToAdd';
import { LocationToAdd, Location} from 'app/common/location';
import { AddCollectionStateService } from 'app/services/add-collection-state.service';
import { ConfirmationService, MenuItem, Message, MessageService } from 'primeng/api';

@Component({
  selector: 'app-add-collection',
  templateUrl: './add-collection.component.html',
  styleUrls: ['./add-collection.component.css'],
})
export class AddCollectionComponent implements OnInit {
  items: MenuItem[] = [{
      label: 'Informacje zbiórki',
      routerLink: 'info'
    },
    {
      label: 'Lokalizacje',
      routerLink: 'locations'
    },
    {
      label: 'Przedmioty',
      routerLink: 'items'
    },
  ];
  itemIndex: number = 0;

  isRightDisabled: boolean = false;
  isLeftDisabled: boolean = false;

  routedComponent: any;

  errorMessage: Message[];

  state: any;

  constructor(private router: Router, 
    private stateService: AddCollectionStateService, 
    private confirmationService: ConfirmationService, 
    private messageService: MessageService) { }

  ngOnInit(): void {
    this.setParams(this.router.url);
    this.stateService.state$.next(this.state);
  }

  addCollection() {
    this.routedComponent.saveState()
    this.state = this.stateService.state$.getValue() || {}
    if (!("info" in this.state)) {
      this.errorMessage = [
        {severity:'error', detail: "Brakuje informacji dotyczących zbiórki"}
      ]
    } else if (this.state.info.title === "" || this.state.info.title.length < 3 || this.state.info.title.length > 30) {
      this.errorMessage = [
        {severity:'error', detail: "Nieprawidłowy tytuł zbiórki"}
      ]
    } else if (this.state.info.description === "" || this.state.info.description.length < 3 || this.state.info.description.length > 500) {
      this.errorMessage = [
        {severity:'error', detail: "Nieprawidłowy opis zbiórki"}
      ]
    } else if (!("locations" in this.state) || this.state.locations.length === 0) {
      this.errorMessage = [
        {severity:'error', detail: "Nie dodano lokalizacji do zbiórki"}
      ]
    } else if (!("items" in this.state) || this.state.items.length === 0) {
      this.errorMessage = [
        {severity:'error', detail: "Nie dodano przedmiotów do zbiórki"}
      ]
    } else {
      this.confirmationService.confirm({
        message: 'Jesteś pewny że chcesz utworzyć nową zbiórkę?',
        accept: () => {
          const collection = this.prepareCollection();
          this.messageService.add({key: 'tc', severity:'success', summary: 'Sukces', detail: 'Zbiórka została dodana'});
        }
      });
    }
  }

  prepareCollection() {
    const collection = new CollectionToAdd();
    this.state = this.stateService.state$.getValue() || {}
    collection.title = this.state.info.title;
    collection.description = this.state.info.description;
    collection.endDate = this.state.info.endDate;
    collection.image = this.state.info.image;
    collection.userId = 1;
    collection.addresses = this.mapLocations(this.state.locations);
    collection.items = this.mapItems(this.state.items);
    console.log(collection);
  }

  saveCollection() {

  }

  mapItems(tempItems: NewItem[]) {
    const items: ItemToAdd[] = [];
    for(let tempItem of tempItems) {
      const item = new ItemToAdd();
      item.name = tempItem.name;
      item.category = Object.keys(ItemCategory)[Object.values(ItemCategory).indexOf(tempItem.category)];
      item.type = Object.keys(ItemType)[Object.values(ItemType).indexOf(tempItem.type)];
      item.maxAmount = tempItem.maxAmount;
      items.push(item);
    }
    return items;
  }

  mapLocations(tempLocations: Location[]) {
    const locations: LocationToAdd[] = [];
    for(let tempLoc of tempLocations) {
      const location = new LocationToAdd();
      location.city = tempLoc.city;
      location.street = tempLoc.street;
      locations.push(location);
    }
    return locations;
  }

  navigateRight() {
    this.routedComponent.saveState()
    this.itemIndex = this.itemIndex + 1;
    const id = this.itemIndex;
    const link = this.items[id].routerLink;
    this.router.navigate(['/add-collection/' + link])
    this.isLeftDisabled = false;
    if (this.itemIndex == 2) {
      this.isRightDisabled = true;
    } else {
      this.isRightDisabled = false;
    }
  }

  navigateLeft() {
    this.routedComponent.saveState()
    this.itemIndex = this.itemIndex - 1;
    const id = this.itemIndex;
    const link = this.items[id].routerLink;
    this.router.navigate(['/add-collection/' + link])
    this.isRightDisabled = false;
    if (this.itemIndex == 0) {
      this.isLeftDisabled = true;
    } else {
      this.isLeftDisabled = false;
    }
  }
  setParams(currentUrl: string) {
    this.items.forEach((value, index) => {
      if (currentUrl.includes(value.routerLink)) {
        this.itemIndex = index;
      }
    });
    if (this.itemIndex == 0) {
      this.isLeftDisabled = true;
    } else if (this.itemIndex == 2) {
      this.isRightDisabled = true;
    }
  }
}


