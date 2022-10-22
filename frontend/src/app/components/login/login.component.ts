import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  error: string | null | undefined;
  message: string | undefined;

  form: FormGroup = new FormGroup({
    username: new FormControl(''),
    password: new FormControl(''),
  });
  invalidLogin = false

  constructor(private router: Router) {
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
    console.log(username + ' ' + password);
    if (username == '' || password == '') {
      this.error = "error"
    }
  }

  clearError() {
    this.error = undefined;
  }

}
