import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CaptchaComponent } from './captcha-page/captcha-page.component';
import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { OrderComponent } from './order/order.component';
import { PaymentComponent } from './payment/payment.component';
import { HistoryComponent } from './history/history.component';
import { DemoComponent } from './demo/demo.component';
import { HistoryWalletComponent } from './history-wallet/history-wallet.component';
import { ALLHistoryComponent } from './allhistory/allhistory.component';
import { AllHistoryWalletComponent } from './allhistory-wallet/allhistory-wallet.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'order', component: OrderComponent },
  { path: 'captcha-page', component: CaptchaComponent },
  { path: 'payment', component: PaymentComponent },
  { path: 'history', component: HistoryComponent },
  { path: 'demo', component: DemoComponent },
  { path: 'history-wallet', component: HistoryWalletComponent },
  { path: 'allhistory', component: ALLHistoryComponent },
  { path: 'allhistory-wallet', component: AllHistoryWalletComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
