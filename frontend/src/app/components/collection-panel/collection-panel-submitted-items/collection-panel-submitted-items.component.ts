import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SubmittedItem } from 'app/common/submittedItem';
import { CollectionService } from 'app/services/collection.service';
import { Location, ViewportScroller } from '@angular/common'
import { Collection } from 'app/common/collection';
import { UserData } from 'app/common/userData';
import { Item } from 'app/common/itemToAdd';
import { AuthService } from 'app/services/auth.service';

@Component({
  selector: 'app-collection-panel-submitted-items',
  templateUrl: './collection-panel-submitted-items.component.html',
  styleUrls: ['./collection-panel-submitted-items.component.css']
})
export class CollectionPanelSubmittedItemsComponent implements OnInit {

  responsiveOptions: any;
  collection: Collection | undefined;
  items: Item[] = [];
  submittedItemsData: SubmittedItem[] = [];
  submittedItems: SubmittedItem[] = [];
  choosenItemId: string = '-1';
  user: UserData | undefined;

  display: boolean;

  pageSize: number = 10;
  totalRecords: number;
  page: number = 0;

  constructor(private route: ActivatedRoute, private collectionService: CollectionService, private location: Location,
    private translate: TranslateService, private authService: AuthService, private scroller: ViewportScroller) {
      this.responsiveOptions = [
        {
            breakpoint: '1024px',
            numVisible: 3,
            numScroll: 3
        },
        {
            breakpoint: '768px',
            numVisible: 2,
            numScroll: 2
        },
        {
            breakpoint: '560px',
            numVisible: 1,
            numScroll: 1
        }
    ];
    }

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handleCollectionDetails();
    })
  }

  chooseItem(item: Item) {
    this.submittedItems = [];
    this.choosenItemId = item.id;
    for (let item of this.submittedItemsData) {
      if (item.collectionItemId.toString() == this.choosenItemId) {
        this.submittedItems.push(item);
      }
    }
  }

  showAll() {
    this.choosenItemId = '-1';
    this.submittedItems = this.submittedItemsData;
  }

  showUserDetails(username: string) {
    this.display = true;
    this.authService.getUserByUsername(username).subscribe({
      next: (data) => {
        this.user = data;
      },
    })
  }

  handleCollectionDetails() {
    const collectionId: string = this.route.snapshot.paramMap.get('id')!;
    this.collectionService.getCollectionById(collectionId).subscribe({
      next: (data) => {   
        this.collection = data;
        this.items = data.items;
        this.handleSubmittedItemsDetails(collectionId)
      },
      error: () => {

      }
    })
  }

  handleSubmittedItemsDetails(id: string) {
    this.collectionService.getSubmittedItemsFromCollection(id).subscribe({
      next: (data) => {
        this.submittedItemsData = data;
        this.submittedItems = this.submittedItemsData;
      }
    })
  }

  getMessage() {
    if (this.choosenItemId == '-1') {
      return "panel.not_submitted_items";
    }
    return "panel.not_submitted_item";
  }

  back() {
    this.location.back();
  }

  scroll() {
    this.scroller.scrollToAnchor("itemList");
  }

}
