import { Component, OnInit } from '@angular/core';
import { dateInputsHaveChanged } from '@angular/material/datepicker/datepicker-input-base';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { Collection } from 'app/common/collection';
import { CollectionService } from 'app/services/collection.service';

@Component({
  selector: 'app-collection',
  templateUrl: './collection-info.component.html',
  styleUrls: ['./collection-info.component.css']
})
export class CollectionInfoComponent implements OnInit {

  collection: Collection;

  constructor(private route: ActivatedRoute, private collectionService: CollectionService, private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handleCollectionDetails();
    })
  }

  handleCollectionDetails() {
    const connId: string = this.route.snapshot.paramMap.get('id')!;
    this.collectionService.getCollectionById(connId).subscribe({
      next: (data) => {   
        this.collection = data;
      },
      error: () => {

      }
    })
  }

  getImageFromBase64(dataURI: string) {
    let objectURL = 'data:image/jpeg;base64,' + dataURI;
    return this.sanitizer.bypassSecurityTrustUrl(objectURL);
  }

}
