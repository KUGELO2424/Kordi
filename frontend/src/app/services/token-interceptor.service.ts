import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptorService implements HttpInterceptor{

  constructor() { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (sessionStorage.getItem("username") && sessionStorage.getItem("token")) {
      req = req.clone({
        setHeaders: {
          Authorization: sessionStorage.getItem("token")!
        }
      })
    }
    return next.handle(req);
  }
}
