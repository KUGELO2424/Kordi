import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'app/services/auth.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-verification-email',
  templateUrl: './verification-email.component.html',
  styleUrls: ['./verification-email.component.css']
})
export class VerificationEmailComponent implements OnInit {

  errorMessage: string = "";
  username: string = "";

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService, 
    private translate: TranslateService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.verifyUser();
    })
  }

  verifyUser() {
    const token: string = this.route.snapshot.paramMap.get('token')!;
    const username: string = this.route.snapshot.paramMap.get('username')!;
    this.username = username;
    this.authService.verifyByEmail(token).subscribe({
      next: (data) => {
      },
      error: (error) => {
        console.log(error);
        this.errorMessage = error.error.error;
      }
    })
  }

  sendAgain() {
    this.authService.sendVerificationToken(this.username).subscribe({
      next: (data) => {
        this.messageService.add({severity:'success', detail: this.translate.instant('verify.sent')});
      },
      error: (error) => {
        console.log(error);
        this.messageService.add({severity:'error', detail: this.translate.instant('verify.not_send')});
      }
    })
  }

}
