import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { BehaviorSubject, map, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private loginUrl = environment.baseUrl + '/login';
  private registerUrl = environment.baseUrl + '/register';
  private sendTokenUrl = environment.baseUrl + '/sendToken';
  private verifyUrl = environment.baseUrl + '/verify';
  
  username: Subject<string> = new BehaviorSubject<string>("");

  constructor(private httpClient: HttpClient) {
    this.username.next(sessionStorage.getItem("username")!);
  }

  authenticate(username: string, password: string) {

    const httpParams = new HttpParams()
      .set('username', username)
      .set('password', password);

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded'
      })
    }

    return this.httpClient.post<any>(this.loginUrl, httpParams, httpOptions)
    .pipe(
      map(userData => {
        sessionStorage.setItem("username", username);
        this.username.next(username);
        let tokenStr = "Bearer " + userData.access_token;
        sessionStorage.setItem("token", tokenStr);
        return userData;
      })
    );
  }

  isUserLoggedIn() {
    let user = sessionStorage.getItem("username");
    let token = sessionStorage.getItem("token");
    return !(user === null) && !(token === null);
  }

  logOut() {
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("token");
    this.username.next("");
  }

}