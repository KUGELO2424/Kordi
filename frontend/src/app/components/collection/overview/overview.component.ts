import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Item } from 'app/common/itemToAdd';
import { SubmittedItem } from 'app/common/submittedItem';
import { ConfirmationService } from 'primeng/api';
import { Location } from '@angular/common'
import { CollectionService } from 'app/services/collection.service';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit {

  items: Item[];
  collectionId: string;
  itemsToSubmit: SubmittedItem[];

  constructor(private router: Router, private translate: TranslateService, private confirmationService: ConfirmationService, 
    private location: Location, private collectionService: CollectionService) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as {collectionId: string, items: Item[]};
    if (state === undefined) {
      this.back();
    }
    this.items = state?.items;
    this.collectionId = state?.collectionId;
    this.mapItemsToSubmittedItems();
  }

  ngOnInit(): void {}

  mapItemsToSubmittedItems() {
    let itemsToSubmit: SubmittedItem[] = [];
    for (let item of this.items) {
      let itemToSubmit = new SubmittedItem();
      if (item.value > 0) {
        itemToSubmit.amount = item.value;
        itemToSubmit.collectionItemId = +item.id;
        if (item.type.toString().toLocaleLowerCase() === 'unlimited') {
          itemToSubmit.amount = 0;
        }
        itemsToSubmit.push(itemToSubmit);
      }
    }
    this.itemsToSubmit = itemsToSubmit;
  }

  confirm(event: Event) {
    this.confirmationService.confirm({
      target: event.target ? event.target : undefined,
      acceptLabel: this.translate.instant("add-collection.yes"),
      rejectLabel: this.translate.instant("add-collection.no"),
      message: "Czy na pewno chcesz przekazać te przedmioty jako darowiznę?",
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.collectionService.donateItem(this.collectionId, this.itemsToSubmit).subscribe({
          next: (data) => {
            console.log(data);
          },
          error: (error) => {
            console.log(error);
          }
        })
      },
      reject: () => {
      }
   });
  }

  back() {
    this.location.back();
  }
}
