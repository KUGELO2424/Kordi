import { Component, OnInit } from '@angular/core';
import { dateInputsHaveChanged } from '@angular/material/datepicker/datepicker-input-base';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Collection } from 'app/common/collection';
import { SubmittedItem } from 'app/common/submittedItem';
import { CollectionService } from 'app/services/collection.service';

@Component({
  selector: 'app-collection',
  templateUrl: './collection-info.component.html',
  styleUrls: ['./collection-info.component.css']
})
export class CollectionInfoComponent implements OnInit {

  collection: Collection | undefined;
  submittedItems: SubmittedItem[] = [];
  numOfpeople: number = 0;

  constructor(private route: ActivatedRoute, private collectionService: CollectionService, private sanitizer: DomSanitizer,
    private translate: TranslateService) { }

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

}
