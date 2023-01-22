import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SubmittedItem } from 'app/common/submittedItem';
import { CollectionService } from 'app/services/collection.service';

@Component({
  selector: 'app-profile-donates',
  templateUrl: './profile-donates.component.html',
  styleUrls: ['./profile-donates.component.css']
})
export class ProfileDonatesComponent implements OnInit {

  submittedItems: SubmittedItem[] = [];

  constructor(private collectionService: CollectionService, private router: Router) { }

  ngOnInit(): void {
    this.getDonates();
  }

  getDonates() {
    let username = sessionStorage.getItem("username");
    this.collectionService.getSubmittedItemsForUser(username!).subscribe({
      next: (data) => {
        this.submittedItems = data.content;
      },
      error: (error) => {

      }
    })
  }

  openCollection(id: number) {
    this.router.navigateByUrl("/collections/" + id);
  }

}
