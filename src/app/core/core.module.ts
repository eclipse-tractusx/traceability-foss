import { NgModule } from '@angular/core';
import { ApiService } from './api/api.service';
import { AuthService } from './auth/auth.service';
import { CanDeactivateGuard } from './user/can-deactivate.guard';
import { UserService } from './user/user.service';

@NgModule({
  providers: [ApiService, AuthService, UserService, CanDeactivateGuard],
})
export class CoreModule {}
