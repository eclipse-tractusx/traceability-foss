import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
import { NotificationService } from 'src/app/shared/components/notifications/notification.service';

/**
 *
 *
 * @export
 * @class HttpErrorInterceptor
 * @implements {HttpInterceptor}
 */
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private notificationService: NotificationService) {}

  intercept(
    request: HttpRequest<Record<string, unknown>>,
    next: HttpHandler,
  ): Observable<HttpEvent<Record<string, unknown>>> {
    return next.handle(request).pipe(
      retry(1),
      catchError((error: HttpErrorResponse) => {
        let errorMessage: string;
        if (Array.isArray(error.error.error)) {
          errorMessage = `An error occurred: ${error.error.message}`;
          error.error.error.forEach(message => (errorMessage = message.message));
        } else if (error.message) {
          errorMessage = error.message;
        } else {
          errorMessage = `Backend returned code ${error.status}: ${error.message}`;
        }
        this.notificationService.error(errorMessage);
        return throwError(errorMessage);
      }),
    );
  }
}
