import { ViewportScroller } from '@angular/common';
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

  constructor(private route: ActivatedRoute, private collectionService: CollectionService, private sanitizer: DomSanitizer,
    private translate: TranslateService, public router: Router, private messageService: MessageService, 
    private authService: AuthService) {
      const navigation = this.router.getCurrentNavigation();
      const state = navigation?.extras.state as {data: string};
      if (state !== undefined) {
        setTimeout(() => {
          this.messageService.add({severity:'success', detail: this.translate.instant("collection.item_submitted")});
        }, 300);
        
      }
    }

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handleCollectionDetails();
    })
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
      error: () => {

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
    let result = this.translate.instant("category." + foundItem?.category.toLocaleLowerCase()) + " " + submittedItem.amount + this.translate.instant("suffix." + foundItem?.type.toLocaleLowerCase())
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

}
