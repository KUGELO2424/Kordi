import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { finalize, Observable } from 'rxjs';
import { LoaderService } from './loader.service';

@Injectable({
  providedIn: 'root'
})
export class LoadingInterceptorService implements HttpInterceptor{

  private totalRequests = 0;

  constructor(private loadingService: LoaderService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('=========== caught ===========')
    this.totalRequests++;
    this.loadingService.setLoading(true);
    return next.handle(req).pipe(
      finalize(() => {
        this.totalRequests--;
        if (this.totalRequests == 0) {
          this.loadingService.setLoading(false);
        }
      })
    );
  }
}
