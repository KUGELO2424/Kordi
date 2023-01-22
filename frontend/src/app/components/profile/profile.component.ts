import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Collection } from 'app/common/collection';
import { SubmittedItem } from 'app/common/submittedItem';
import { UserData } from 'app/common/userData';
import { AuthService } from 'app/services/auth.service';
import { CollectionService } from 'app/services/collection.service';
import { Message, MessageService } from 'primeng/api';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user?: UserData;

  displayEditProfile: boolean;
  
  oldPassword: string = '';
  newPassword: string = '';
  newPasswordRepeat: string = '';

  errorMessages: Message[] = [];

  constructor(private authService: AuthService, private collectionService: CollectionService, private translate: TranslateService,
    private messageService: MessageService) { }

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
  }

  showEditProfileDialog() {
    this.displayEditProfile = true;
  }

  isUpdateDisabled() {
    if (this.oldPassword == '' || this.newPassword == '' || this.newPasswordRepeat == '') {
      return true;
    }
    return false;
  }

  changePassword() {
    if (this.newPassword !== this.newPasswordRepeat) {
      console.log("DDD");
      this.errorMessages = [{severity:'error', detail:this.translate.instant('profile.new_pass_not_match')}];
      return;
    }
    this.authService.updatePassword(this.oldPassword, this.newPassword).subscribe({
      next: (data) => {
        this.messageService.add({key: 'tl', severity:'success', detail: 'Hasło zostało zmienione'});
        this.displayEditProfile = false;
        this.reset();
      },
      error: (error) => {
        if (error.error.error === undefined) {
          this.errorMessages = [
            {severity:'error', detail: this.translate.instant("user.error")}
          ]
        }  else {
          this.errorMessages = [
            {severity:'error', detail: this.translate.instant(error.error.error)}
          ]
        }
      }
    })
  }

  reset() {
    this.newPassword = '';
    this.oldPassword = '';
    this.newPasswordRepeat = '';
  }

}
