import { ViewportScroller } from '@angular/common';
import { Component, ElementRef, Input, OnInit, SimpleChanges, ViewChild, ViewEncapsulation } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Item } from 'app/common/itemToAdd';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ItemListComponent implements OnInit {

  @Input() itemsData: Item[];
  items: Item[];

  searchItem: string = "";

  pageSize: number = 10;
  totalRecords: number;
  page: number = 0;

  constructor(private confirmationService: ConfirmationService, private scroller: ViewportScroller, 
    private translate: TranslateService) { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.items = this.itemsData;
    this.totalRecords = this.items.length;
    this.clearItemsValue();
  }

  confirm(event: Event) {
    this.confirmationService.confirm({
        target: event.target ? event.target : undefined,
        message: 'Czy na pewno chcesz przejść do podsumowania?',
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
            console.log("AKCEPT")
        },
        reject: () => {
          console.log("CANCLE")
        }
    });
  }

  search() {
    this.items = [];
    for (let item of this.itemsData) {
      const category = this.translate.instant("category." + item.category.toString().toLocaleLowerCase());
      if (item.name.toLocaleLowerCase().includes(this.searchItem.toLocaleLowerCase()) 
       || category.toLocaleLowerCase().includes(this.searchItem.toLocaleLowerCase())) {
        this.items.push(item);
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

}
