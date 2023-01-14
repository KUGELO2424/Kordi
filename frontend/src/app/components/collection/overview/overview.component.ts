import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Item } from 'app/common/itemToAdd';
import { SubmittedItem } from 'app/common/submittedItem';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit {

  items: Item[];
  itemsToSubmit: SubmittedItem[];

  constructor(private router: Router, private translate: TranslateService, private confirmationService: ConfirmationService) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as {collectionId: string, items: Item[]};
    this.items = state?.items;
    this.mapItemsToSubmittedItems();
    if (state === undefined) {
      this.router.navigateByUrl("/");
    }
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
  }

  confirm(event: Event) {
    this.confirmationService.confirm({
      target: event.target ? event.target : undefined,
      acceptLabel: this.translate.instant("add-collection.yes"),
      rejectLabel: this.translate.instant("add-collection.no"),
      message: "Czy na pewno chcesz przekazać te przedmioty jako darowiznę?",
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
      },
      reject: () => {
      }
  });
  }
}
