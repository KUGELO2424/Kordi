import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Collection } from 'app/common/collection';
import { CollectionService } from 'app/services/collection.service';

@Component({
  selector: 'app-profile-collections',
  templateUrl: './profile-collections.component.html',
  styleUrls: ['./profile-collections.component.css']
})
export class ProfileCollectionsComponent implements OnInit {

  collections: Collection[] = [];

  constructor(private collectionService: CollectionService, private router: Router) { }


  ngOnInit(): void {
    this.getCollections();
  }

  getCollections() {
    this.collectionService.getCollectionForLoggedUser().subscribe({
      next: (data) => {
        this.collections = data
        this.getCollectionsProgress();
      },
      error: (error) => {
        console.log(error);
      }
    })
  }

  editCollection(id: number) {
    this.router.navigateByUrl("/collections/" + id + "/panel")
  }

  getCollectionsProgress() {
    for(let collection of this.collections) {
      this.collectionService.getCollectionProgress(collection.items).subscribe({
        next: (data) => {
          collection.progress = data;
        }
      })
    }
  }

  isActive(collection: Collection) {
    if (collection.progress == 100) {
      return false;
    } else if (collection.endTime !== undefined && this.getDaysToEnd(collection) < 0) {
      return false;
    }
    return true;
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

  getProgress(collection: Collection) {
    return Math.floor(collection.progress);
  }

}
