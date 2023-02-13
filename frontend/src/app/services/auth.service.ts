import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserData } from 'app/common/userData';
import { environment } from 'environments/environment';
import { BehaviorSubject, map, Subject } from 'rxjs';
import { UserToRegister } from '../common/userToRegister';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private loginUrl = environment.baseUrl + '/login';
  private registerUrl = environment.baseUrl + '/register';
  private sendTokenUrl = environment.baseUrl + '/sendToken';
  private verifyUrl = environment.baseUrl + '/verify';
  private validateUrl = environment.baseUrl + '/validate';
  private getUserUrl = environment.baseUrl + '/users';
  
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

  register(user: UserToRegister) {
    return this.httpClient.post<UserToRegister>(this.registerUrl, user)
  }

  verifyByPhone(phone: string, token: string) {
    return this.httpClient.get<any>(`${this.verifyUrl}?phone=${phone}&token=${token}`)
  }

  verifyByEmail(token: string) {
    return this.httpClient.get<any>(`${this.verifyUrl}?token=${token}`)
  }

  sendVerificationToken(username: string) {
    return this.httpClient.post<any>(`${this.sendTokenUrl}?username=${username}`, "");
  }

  getUserByUsername(username: string) {
    return this.httpClient.get<UserData>(`${this.getUserUrl}/${username}`);
  }

  getLoggedUser() {
    return this.httpClient.get<UserData>(`${this.getUserUrl}/me`);
  }

  validate(username: string, token: string) {
    return this.httpClient.post<any>(`${this.validateUrl}?username=${username}&token=${token}`, "");
  }

  updatePassword(oldPassword: string, newPassword: string) {
    return this.httpClient.put<any>(`${this.getUserUrl}/updatePassword?oldPassword=${oldPassword}&password=${newPassword}`, "");
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
