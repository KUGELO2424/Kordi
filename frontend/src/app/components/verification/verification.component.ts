import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { UserData } from 'app/common/userData';
import { AuthService } from 'app/services/auth.service';
import { Message } from 'primeng/api';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.css']
})
export class VerificationComponent implements OnInit {

  code = "";
  verificationType = "PHONE";
  user: UserData | undefined;

  phone = "*********";
  messages: Message[] = [];

  sendAgainTimeInSeconds: number = 60;
  sendCodeAgainTime: Date
  sendEmailAgainTime: Date

  constructor(private router: Router, private authService: AuthService, private translate: TranslateService) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as {data: string};
    if (state === undefined) {
      this.router.navigateByUrl("/");
    }
    const username = state.data;
    
    authService.getUserByUsername(username).subscribe(data => {
      this.user = data;
      this.phone = "******" + this.user.phone.substring(6, 10);
    })

    this.setTime();
  }

  ngOnInit(): void {
  }

  verify() {
    if (this.code == "" || this.code.length != 6) {
      this.messages = [
        {severity:'error', detail: this.translate.instant('phone.verification.code.wrong')}
      ]
      return;
    }

    this.authService.verifyByPhone(this.user!.phone, this.code).subscribe({
      next: response => {
        if (response.status == 'VERIFIED') {
          const navigationExtras = {state: {data: this.translate.instant('login.verification_success')}};
          this.router.navigateByUrl("/login", navigationExtras);
        }
      },
      error: error => {
        console.log(error);
        this.messages = [
          {severity:'error', detail: this.translate.instant(error.error.error)}
        ]
      }
    })
  }

  sendCodeAgain() {
    if (!this.checkIfRequestCanBeSend()) {
      return;
    }
    this.authService.sendVerificationToken(this.user!.username).subscribe({
      next: response => {
        if (response.status == 'PENDING') {
          this.messages = [
            {severity:'success', detail: this.translate.instant("verify.activation_code_send")}
          ]
          this.sendCodeAgainTime = new Date();
        }
      },
      error: error => {
        console.log(error);
        this.messages = [
          {severity:'error', detail: this.translate.instant(error.error.error)}
        ]
      }
    })
  }

  setTime() {
    var now = new Date();
    now.setDate(now.getDate() - 1);
    this.sendCodeAgainTime = now;
    this.sendEmailAgainTime = now;  
  }

  checkIfRequestCanBeSend() {
    const now = new Date();
    const diff = (now.getTime() - this.sendCodeAgainTime.getTime()) / 1000;
    if (diff < this.sendAgainTimeInSeconds) {
      const time = Math.round(this.sendAgainTimeInSeconds - diff);
      this.messages = [
        {severity:'error', detail: this.translate.instant('phone.verification.code.send.wait', time)}
      ]
      return false;
    }
    return true;
  }

}
