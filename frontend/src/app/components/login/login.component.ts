import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'app/services/auth.service';
import { Message } from 'primeng/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  errorMessage: Message[] = [];
  error: string | null | undefined;
  message: string | undefined;

  form: UntypedFormGroup = new UntypedFormGroup({
    username: new UntypedFormControl(''),
    password: new UntypedFormControl(''),
  });
  invalidLogin = false

  constructor(private router: Router, private loginService: AuthService, private translate: TranslateService) {
      const navigation = this.router.getCurrentNavigation();
      const state = navigation?.extras.state as {data: string};
      if (state !== undefined) {
        this.message = state.data;
      }
  }

  ngOnInit(): void {
  }

  submit() {
    this.message = "";
    const username = this.form.controls['username'].value
    const password = this.form.controls['password'].value
    if (username == '' || password == '') {
      this.errorMessage = [
        {severity:'error', detail: this.translate.instant("user.cannotbeempty")}
      ]
      return
    }
    this.loginService.authenticate(username, password).subscribe({
      next: (data) => {
        this.router.navigateByUrl("/")
        this.invalidLogin = false
      },
      error: (error) => {
        this.invalidLogin = true
        this.errorMessage = [
          {severity:'error', detail: this.translate.instant(error.error.error)}
        ]
      }
    })

  }

}
