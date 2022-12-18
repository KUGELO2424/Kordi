import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'app/services/auth.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  constructor(
    private loginService: AuthService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.loginService.logOut();
    this.router.navigateByUrl("/");
  }

}
