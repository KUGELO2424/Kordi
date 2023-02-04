import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService {

  constructor(private router: Router, private loginService: AuthService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (this.loginService.isUserLoggedIn()) {
      return this.loginService.validate(sessionStorage.getItem("username")!, sessionStorage.getItem("token")!).subscribe({
        next: () => {
          return true;
        },
        error: (error) => {
          this.router.navigateByUrl("/login");
          this.loginService.logOut();
          return false;
        }
      })
    } else {
      this.router.navigateByUrl("/login");
      return false;
    }
  }
}
