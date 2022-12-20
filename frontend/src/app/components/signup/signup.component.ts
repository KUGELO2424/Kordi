import { Component, OnInit } from '@angular/core';
import { FormBuilder, UntypedFormControl, UntypedFormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { NavigationBehaviorOptions, NavigationExtras, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { UserToRegister } from 'app/common/userToRegister';
import { AuthService } from 'app/services/auth.service';
import { Message } from 'primeng/api';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  hide = true;
  errorMessage: Message[] = [];

  form: UntypedFormGroup = new UntypedFormGroup({
    username: new UntypedFormControl('', [Validators.required, Validators.minLength(3)]),
    password: new UntypedFormControl('', [Validators.required, Validators.minLength(6)]),
    firstname: new UntypedFormControl('', [Validators.required, Validators.minLength(2)]),
    lastname: new UntypedFormControl('', [Validators.required, Validators.minLength(2)]),
    email: new UntypedFormControl('', [Validators.required, Validators.email]),
    phone: new UntypedFormControl('', [Validators.required, Validators.pattern("[0-9]{9}")]),
    verificationType: new UntypedFormControl('EMAIL')
  });

  constructor(private router: Router, private translate: TranslateService, private authService: AuthService) { }

  ngOnInit(): void {
  }

  checkValidation(input: string){
    const validation = this.form.get(input)?.invalid && (this.form.get(input)?.dirty || this.form.get(input)?.touched)
    return validation;
  }

  submit(formData: UntypedFormGroup, formDirective: FormGroupDirective): void {
    let user = new UserToRegister();
    user.username = formData.value.username;
    user.password = formData.value.password;
    user.firstName = formData.value.firstname;
    user.lastName = formData.value.lastname;
    user.email = formData.value.email;
    user.phone = formData.value.phone;
    user.verificationType = formData.value.verificationType;

    this.authService.register(user).subscribe({
      next: response => {
        let navigationExtras;
        if (user.verificationType == 'EMAIL') {
          navigationExtras = {state: {data: this.translate.instant('login.signup_success_email')}};
        } else {
          navigationExtras = {state: {data: this.translate.instant('login.signup_success_phone')}};
        }
        this.router.navigateByUrl("/login", navigationExtras);
        formDirective.resetForm();
        this.form.reset();
      },
      error: error => {
        console.log(error);
        if (error.error.error === undefined) {
          this.errorMessage = [
            {severity:'error', detail: this.translate.instant("user.error")}
          ]
        } else {
          this.errorMessage = [
            {severity:'error', detail: this.translate.instant(error.error.error)}
          ]
        }
      }
    })
  } 

}
