import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  username: string = "";

  constructor(private loginService: AuthService, private router: Router) {
  }

  isAuthenticated() {
    return this.loginService.isUserLoggedIn();
  }

  searchCollections(category: string) {
    let navigationExtras = {state: {data: category}};
    this.router.navigateByUrl("collections", navigationExtras);
  }
}
