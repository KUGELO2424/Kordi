import { Component, OnInit, ViewChild } from '@angular/core';
import { MatExpansionPanel } from '@angular/material/expansion';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Collection } from 'app/common/collection';
import { SubmittedItem } from 'app/common/submittedItem';
import { AuthService } from 'app/services/auth.service';
import { CollectionService } from 'app/services/collection.service';
import { MessageService } from 'primeng/api';
import { Location, ViewportScroller } from '@angular/common'

@Component({
  selector: 'app-collection',
  templateUrl: './collection-info.component.html',
  styleUrls: ['./collection-info.component.css']
})
export class CollectionInfoComponent implements OnInit {
  @ViewChild('itemPanel') itemPanel: MatExpansionPanel
  collection: Collection | undefined;
  submittedItems: SubmittedItem[] = [];
  numOfpeople: number = 0;
  collectionProgress: number = 0;
  display: boolean = false;

  collectionNotFound: boolean = false;
  collectionNotFoundMessage: string;

  isCollectionOwner: boolean = false;

  constructor(private route: ActivatedRoute, private collectionService: CollectionService, private sanitizer: DomSanitizer,
    private translate: TranslateService, public router: Router, private messageService: MessageService, private scroller: ViewportScroller,
    private authService: AuthService, private location: Location) {
      const navigation = this.router.getCurrentNavigation();
      const state = navigation?.extras.state as {data: string};
      if (state !== undefined) {
        setTimeout(() => {
          this.messageService.add({severity:'success', detail: state.data});
        }, 300);
      }
    }

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handleCollectionDetails();
    })
    this.isUserACollectionOwner();
  }

  handleCollectionDetails() {
    const collectionId: string = this.route.snapshot.paramMap.get('id')!;
    this.collectionService.getCollectionById(collectionId).subscribe({
      next: (data) => {   
        this.collection = data;
        this.handleSubmittedItemsDetails(collectionId, 3)
        this.setCollectionProgress();
        this.itemPanel.expanded = true
      },
      error: (error) => {
        console.log(error);
        this.collectionNotFound = true;
        this.collectionNotFoundMessage = error.error.error;
      }
    })
  }

  handleSubmittedItemsDetails(id: string, numberOfSubmittedItems: number) {
    this.collectionService.getSubmittedItemsFromCollection(id).subscribe({
      next: (data) => {
        this.submittedItems = data;
        this.countPeopleThatSubmittedItems();
      }
    })
  }

  setCollectionProgress() {
    this.collectionService.getCollectionProgress(this.collection!.items).subscribe({
      next: (data) => {
        this.collectionProgress = data;
      }
    })
  }

  getImageFromBase64(dataURI: string) {
    let objectURL = 'data:image/jpeg;base64,' + dataURI;
    return this.sanitizer.bypassSecurityTrustUrl(objectURL);
  }

  getDonateString(submittedItem: SubmittedItem) {
    const foundItem = this.collection?.items.find((item) => {
      return item.id == submittedItem.collectionItemId.toString();
    })
    let category = this.translate.instant("category." + foundItem?.category.toLocaleLowerCase());
    let amount = submittedItem.amount == 0 ? '' : submittedItem.amount;
    let suffix = this.translate.instant("suffix." + foundItem?.type.toLocaleLowerCase());
    let result = category + " " + amount + suffix
    return result
  }

  countPeopleThatSubmittedItems() {
    let usernames : string[] = []
    for(let submittedItem of this.submittedItems) {
      if (!usernames.includes(submittedItem.username)) {
        usernames.push(submittedItem.username);
      }
    }
    this.numOfpeople = usernames.length;
  }

  getDaysToEnd(collection: Collection) {
    const msInDay = 24 * 60 * 60 * 1000;
    if (collection.endTime != null) {
      let now = new Date().getTime();
      let endTime = Date.parse(collection.endTime.toString());
      let diffTime = endTime - now;
      return Math.round((diffTime / msInDay));
    }
    return 0;
  }

  isUserLoggedIn() {
    return !this.authService.isUserLoggedIn();
  }

  isUserACollectionOwner() {
    if (this.authService.isUserLoggedIn()) {
      this.authService.getLoggedUser().subscribe({
        next: (data) => {
          this.isCollectionOwner = data.id == this.collection?.userId.toString()
        }
      })
    }
  }

  back() {
    this.location.back();
  }

}
