import { ViewportScroller } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Item } from 'app/common/itemToAdd';
import { Location } from 'app/common/location';
import { SubmittedItem } from 'app/common/submittedItem';
import { CollectionService } from 'app/services/collection.service';

@Component({
  selector: 'app-profile-donates',
  templateUrl: './profile-donates.component.html',
  styleUrls: ['./profile-donates.component.css']
})
export class ProfileDonatesComponent implements OnInit {

  submittedItems: SubmittedItem[] = [];

  pageSize: number = 10;
  totalRecords: number = 10;
  page: number = 0;

  display: boolean = false;

  locations: Location[] = [];

  constructor(private collectionService: CollectionService, private router: Router, private scroller: ViewportScroller) { }

  ngOnInit(): void {
    this.getDonates();
  }

  getDonates() {
    let username = sessionStorage.getItem("username");
    this.collectionService.getSubmittedItemsForUser(username!, this.page, this.pageSize).subscribe({
      next: (data) => {
        this.submittedItems = data.content;
        this.pageSize = data.size;
        this.totalRecords = data.totalElements;
      },
      error: (error) => {

      }
    })
  }

  showLocations(item: SubmittedItem) {
    this.display = true;
    this.collectionService.getCollectionById(item.collectionId.toString()).subscribe({
      next: (data) => {
        this.locations = data.addresses;
      }
    })
  }

  changePage(event: any) {
    this.scroll();
    this.pageSize = event.rows;
    this.page = event.page;
    this.getDonates();
  }

  openCollection(id: number) {
    this.router.navigateByUrl("/collections/" + id);
  }

  scroll() {
    this.scroller.scrollToAnchor("itemList");
  }

}
