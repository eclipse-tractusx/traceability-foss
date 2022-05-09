import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

export class ApiInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<string>, next: HttpHandler): Observable<HttpEvent<string>> {
    const requestUrl = req.url;
    req = req.clone({ url: requestUrl });
    return next.handle(req);
  }
}
