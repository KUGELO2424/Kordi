import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.css']
})
export class VerificationComponent implements OnInit {

  code = "";
  verificationType = "EMAIL";
  email = "test@mail.pl";
  phone = "******123";
  error: string | undefined;
  message: string | undefined;

  sendAgainTimeInSeconds: number = 60;

  sendCodeAgainTime: Date
  sendEmailAgainTime: Date

  constructor() {
    var now = new Date();
    now.setDate(now.getDate() - 1);
    this.sendCodeAgainTime = now;
    this.sendEmailAgainTime = now;
  }

  ngOnInit(): void {
  }

  verify() {
    this.error = "Nie udało się zweryfikować konta.";
  }
  
  sendEmailAgain() {
    var now = new Date();
    const diff = (now.getTime() - this.sendEmailAgainTime.getTime()) / 1000
    if (diff < this.sendAgainTimeInSeconds) {
      this.error = "Musisz poczekać " + Math.round(this.sendAgainTimeInSeconds - diff) + " sekund, by wysłać wiadomość email ponownie";
      return;
    }
    this.message = "Wiadomość email została wysłana ponownie.";
    this.sendEmailAgainTime = new Date();
  }

  sendCodeAgain() {
    const now = new Date();
    const diff = (now.getTime() - this.sendCodeAgainTime.getTime()) / 1000
    if (diff < this.sendAgainTimeInSeconds) {
      this.error = "Musisz poczekać " + Math.round(this.sendAgainTimeInSeconds - diff) + " sekund, by wysłać kod aktywacyjny ponownie";
      return;
    }
    this.message = "Kod aktywacyjny został wysłany ponownie.";
    this.sendCodeAgainTime = new Date();
  }

  clearError() {
    this.error = undefined;
  }

  clearMessage() {
    this.message = undefined;
  }

}
