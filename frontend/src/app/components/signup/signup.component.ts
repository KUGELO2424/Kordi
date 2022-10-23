import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { NavigationExtras, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  hide = true;
  error: string | undefined;
  authError: string | undefined;

  form: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(3)]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    firstname: new FormControl('', [Validators.required, Validators.minLength(2)]),
    lastname: new FormControl('', [Validators.required, Validators.minLength(2)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    phone: new FormControl('', [Validators.required, Validators.pattern("[0-9]{9}")]),
    verificationType: new FormControl('EMAIL')
  });

  constructor(private router: Router, private translate: TranslateService) { }

  ngOnInit(): void {
  }

  checkValidation(input: string){
    const validation = this.form.get(input)?.invalid && (this.form.get(input)?.dirty || this.form.get(input)?.touched)
    return validation;
  }

  submit(formData: FormGroup, formDirective: FormGroupDirective): void {
    const username = formData.value.username;
    const password = formData.value.password;
    const firstname = formData.value.firstname;
    const lastname = formData.value.lastname;
    const email = formData.value.email;
    const phone = formData.value.phone;
    const verificationType = formData.value.verificationType;
    console.log("REGISTER USER with " + verificationType);
    if (username == "Test") {
      this.authError = this.translate.instant("login.error_occured");
    } else {
      const navigationExtras: NavigationExtras = {state: {data: this.translate.instant('login.signup_success')}};
      this.router.navigateByUrl("/login", navigationExtras);
      formDirective.resetForm();
      this.form.reset();
    }
  } 

  clearError() {
    this.error = undefined;
    this.authError = undefined;
  }

}
