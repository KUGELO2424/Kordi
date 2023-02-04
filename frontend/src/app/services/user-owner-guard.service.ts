import { Injectable } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';
import { CollectionService } from './collection.service';

@Injectable({
  providedIn: 'root'
})
export class UserOwnerGuardService {

  constructor(private router: Router, private loginService: AuthService, private collectionService: CollectionService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    let username = sessionStorage.getItem("username");
    let collectionId = route?.params["id"];
    if (username != null) {
      this.loginService.getUserByUsername(username).subscribe({
        next: (user) => {
          this.collectionService.getCollectionById(collectionId).subscribe({
            next: (collection) => {
              if (collection.userId.toString() == user.id) {
                return true;
              }
              this.navigateToHomePage();
              return false;
            },
            error: () => {
              this.navigateToHomePage();
              return false;
            }
          })
        },
        error: () => {
          this.navigateToHomePage();
          return false;
        }
      })
    } else {
      this.navigateToHomePage();
      return false;
    }
  }

  navigateToHomePage() {
    this.router.navigateByUrl("/home");
  }
}
