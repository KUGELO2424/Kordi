import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Collection } from 'app/common/collection';
import { UserData } from 'app/common/userData';
import { AuthService } from 'app/services/auth.service';
import { CollectionService } from 'app/services/collection.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user?: UserData;
  collections: Collection[] = [];

  constructor(private authService: AuthService, private collectionService: CollectionService, private translate: TranslateService) { }

  ngOnInit(): void {
    this.getUserData();
  }

  getUserData() {
    this.authService.getLoggedUser().subscribe({
      next: (data) => {
        this.user = data;
      },
      error: (error) => {
        console.log(error);
      }
    })
    this.collectionService.getCollectionForLoggedUser().subscribe({
      next: (data) => {
        this.collections = data
      },
      error: (error) => {
        console.log(error);
      }
    })
  }

}
