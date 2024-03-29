import { ViewportScroller } from '@angular/common';
import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Item, ItemCategory } from 'app/common/itemToAdd';
import { StateService } from 'app/services/state.service';
import { AuthService } from 'app/services/auth.service';
import { ConfirmationService } from 'primeng/api';
import { Collection } from 'app/common/collection';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css'],

})
export class ItemListComponent implements OnInit {

  @Input() collectionId: number;
  @Input() collection: Collection;
  @Input() itemsData: Item[];
  
  items: Item[];
  searchItem: string = "";
  pageSize: number = 10;
  totalRecords: number;
  page: number = 0;
  category = ItemCategory;
  state: any;
  searchTypes: any[] = [];
  selectedSearchType: any;

  daysToEnd: number;

  constructor(private confirmationService: ConfirmationService, private scroller: ViewportScroller, 
    private translate: TranslateService, private router: Router, private stateService: StateService,
    private authService: AuthService) {
      this.translate.get('collection.item_filtr_all').subscribe(() => {
        this.searchTypes = [
          {label: this.translate.instant('collection.item_filtr_all'), 'value': 'all'},
          {label: this.translate.instant('collection.item_filtr_collected'), 'value': 'collected'},
          {label: this.translate.instant('collection.item_filtr_not_collected'), 'value': 'not-collected'}
        ]
      })
      this.selectedSearchType = this.searchTypes[0];
    }

  ngOnInit(): void {
    this.daysToEnd = this.getDaysToEnd();
    this.state = this.stateService.state$.getValue() || {}
    this.stateService.state$.next("");
    if (Object.keys(this.state).length === 0) {
      return;
    }
    if ("items" in this.state) {
      this.itemsData = this.state.items
      this.search();
    }
  }

  ngOnChanges(): void {
    this.items = this.itemsData;
    this.totalRecords = this.items.length;
    this.clearItemsValue();
  }

  confirm(event: Event) {
    this.confirmationService.confirm({
        target: event.target ? event.target : undefined,
        acceptLabel: this.translate.instant("add-collection.yes"),
        rejectLabel: this.translate.instant("add-collection.no"),
        message: this.translate.instant("collection.overview_question"),
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
          const navigationExtras = {
            state: {
              collectionId: this.collectionId,
              items: this.items
            }}; 
          this.saveState();
          this.router.navigateByUrl("/collections/donate/overview", navigationExtras);
        },
        reject: () => {
        }
    });
  }

  saveState() {
    this.state.items = this.items;
    this.stateService.state$.next(this.state);
  }

  search() {
    this.items = [];
    for (let item of this.itemsData) {
      const category = this.translate.instant("category." + item.category.toString().toLocaleLowerCase());
      if (item.name.toLocaleLowerCase().includes(this.searchItem.toLocaleLowerCase()) 
       || category.toLocaleLowerCase().includes(this.searchItem.toLocaleLowerCase())) {
        if (this.selectedSearchType.value == 'collected' && item.currentAmount == item.maxAmount) {
          this.items.push(item);
        } else if (this.selectedSearchType.value == 'not-collected' && item.currentAmount != item.maxAmount) {
          this.items.push(item);
        } else if (this.selectedSearchType.value == 'all') {
          this.items.push(item);
        }
      }
    }
    this.page = 0;
    this.totalRecords = this.items.length;
  }

  clearItemsValue() {
    for(let item of this.items) {
      item.value = 0;
    }
  }

  scroll() {
    this.scroller.scrollToAnchor("itemList");
  }

  isDonateButtonDisabled() {
    for(let item of this.items) {
      if(item.value > 0) {
        return false;
      }
    }
    return true;
  }

  isUserLoggedIn() {
    return this.authService.isUserLoggedIn();
  }

  getDaysToEnd() {
    const msInDay = 24 * 60 * 60 * 1000;
    if (this.collection.endTime != null) {
      let now = new Date().getTime();
      let endTime = Date.parse(this.collection.endTime.toString());
      let diffTime = endTime - now;
      return Math.round((diffTime / msInDay));
    }
    return 999;
  }

}
