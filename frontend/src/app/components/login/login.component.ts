import { Component, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'app/services/auth.service';
import { Message } from 'primeng/api';
import { Location } from '@angular/common'

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  errorMessage: Message[] = [];
  successMessage: Message[] = [];

  form: UntypedFormGroup = new UntypedFormGroup({
    username: new UntypedFormControl(''),
    password: new UntypedFormControl(''),
  });

  constructor(private router: Router, private loginService: AuthService, private translate: TranslateService, private location: Location) {
      if (loginService.isUserLoggedIn()) {
        this.router.navigateByUrl("/");
      }
      const navigation = this.router.getCurrentNavigation();
      const state = navigation?.extras.state as {data: string};
      if (state !== undefined) {
        this.successMessage = [
          {severity:'success', detail: this.translate.instant(state.data)}
        ]
      }
  }

  ngOnInit(): void {
  }

  submit() {
    this.successMessage = [];
    const username = this.form.controls['username'].value
    const password = this.form.controls['password'].value
    if (username == '' || password == '') {
      this.errorMessage = [
        {severity:'error', detail: this.translate.instant("user.cannot.be.empty")}
      ]
      return
    }
    this.loginService.authenticate(username, password).subscribe({
      next: () => {
        this.location.back();
        // this.router.navigateByUrl("/")
      },
      error: (error) => {
        if (error.error.error === undefined) {
          this.errorMessage = [
            {severity:'error', detail: this.translate.instant("user.error")}
          ]
        } else if (error.error.error === 'user.not.verified.phone') {
          const navigationExtras = {state: {data: username}};
          this.router.navigateByUrl("/verify", navigationExtras);
        } else {
          this.errorMessage = [
            {severity:'error', detail: this.translate.instant(error.error.error)}
          ]
        }
      }
    })

  }

}
