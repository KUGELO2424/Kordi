import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { CollectionToAdd } from 'app/common/collectionToAdd';
import { Item, ItemCategory, ItemToAdd, ItemType } from 'app/common/itemToAdd';
import { LocationToAdd, Location} from 'app/common/location';
import { AddCollectionStateService } from 'app/services/add-collection-state.service';
import { CollectionService } from 'app/services/collection.service';
import { ConfirmationService, MenuItem, Message, MessageService } from 'primeng/api';
import { ReplaySubject } from 'rxjs';

@Component({
  selector: 'app-add-collection',
  templateUrl: './add-collection.component.html',
  styleUrls: ['./add-collection.component.css'],
})
export class AddCollectionComponent implements OnInit {
  items: MenuItem[];
  itemIndex: number = 0;

  isRightDisabled: boolean = false;
  isLeftDisabled: boolean = false;

  routedComponent: any;

  errorMessage: Message[];

  state: any;

  constructor(private router: Router, 
    private stateService: AddCollectionStateService, 
    private confirmationService: ConfirmationService, 
    private messageService: MessageService,
    private collectionService: CollectionService,
    private translate: TranslateService) {
    }

  ngOnInit(): void {
    this.translate.get('add-collection.locations').subscribe(() => {
      this.items = [{
        label: this.translate.instant('add-collection.info'),
        routerLink: 'info'
      },
      {
        label: this.translate.instant('add-collection.locations'),
        routerLink: 'locations'
      },
      {
        label: this.translate.instant('add-collection.items'),
        routerLink: 'items'
      },
      ];
      this.setParams(this.router.url);
    })
    this.stateService.state$.next(this.state);
  }

  addCollection() {
    this.routedComponent.saveState()
    this.state = this.stateService.state$.getValue() || {}
    if (!("info" in this.state)) {
      this.errorMessage = [
        {severity:'error', detail: this.translate.instant('add-collection.info.error')}
      ]
    } else if (this.state.info.title === "" || this.state.info.title.length < 3 || this.state.info.title.length > 30) {
      this.errorMessage = [
        {severity:'error', detail: this.translate.instant('add-collection.title.error')}
      ]
    } else if (this.state.info.description === "" || this.state.info.description.length < 3 || this.state.info.description.length > 500) {
      this.errorMessage = [
        {severity:'error', detail: this.translate.instant('add-collection.desc.error')}
      ]
    } else if (!("locations" in this.state) || this.state.locations.length === 0) {
      this.errorMessage = [
        {severity:'error', detail: this.translate.instant('add-collection.locations.error')}
      ]
    } else if (!("items" in this.state) || this.state.items.length === 0) {
      this.errorMessage = [
        {severity:'error', detail: this.translate.instant('add-collection.items.error')}
      ]
    } else {
      this.confirmationService.confirm({
        message: this.translate.instant('add-collection.create.text'),
        accept: async () => {
          const collection = await this.prepareCollection();
          this.collectionService.addCollection(collection).subscribe({
            next: response => {
              this.stateService.state$.next("");
              this.messageService.add({
                key: 'tc', severity:'success', 
                summary: this.translate.instant('add-collection.success'), 
                detail: this.translate.instant('add-collection.added')});
            },
            error: error => {
              this.messageService.add({
                key: 'tc', severity:'error', 
                summary: this.translate.instant('add-collection.error'), 
                detail: this.translate.instant('add-collection.error.msg')});
            }
          })
        }
      });
    }
  }

  prepareCollection() {
    const collection = new CollectionToAdd();
    this.state = this.stateService.state$.getValue() || {}
    collection.title = this.state.info.title;
    collection.description = this.state.info.description;
    collection.endTime = this.state.info.endDate;
    collection.addresses = this.mapLocations(this.state.locations);
    collection.items = this.mapItems(this.state.items);
    if (this.state.info.image != undefined) {
      return this.blobToBase64(this.state.info.image).then((result:any) => {
        var base64result = result.split(',')[1];
        collection.image = base64result;
        return collection;
      })
    }
    return collection;
  }

  blobToBase64(blob: Blob) {
    return new Promise((resolve, _) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(reader.result);
      reader.readAsDataURL(blob);
    });
  }

  getImageAsBlob(image: any): any {
    if (image == undefined) {
      return;
    }
    const reader = new FileReader();
    const result = new ReplaySubject<string>(1);
    reader.readAsDataURL(image);
    reader.onload = (event) => result.next(btoa(event.target!.result!.toString()));
    return result;
  }

  mapItems(tempItems: Item[]) {
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




