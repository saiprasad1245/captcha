import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import{MatCardModule} from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {
  MatCheckboxModule
} from '@angular/material/checkbox';
import {
  MatGridListModule
} from '@angular/material/grid-list';
import {
  MatSlideToggleModule
} from '@angular/material/slide-toggle';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { OrderComponent } from './order/order.component';
import { CaptchaComponent } from './captcha-page/captcha-page.component';
import { MatRadioModule } from '@angular/material/radio';
import { authInterceptorProviders } from './_helpers/auth.interceptor';
import { PaymentComponent } from './payment/payment.component';
import { HistoryComponent } from './history/history.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { DemoComponent } from './demo/demo.component';
import { HistoryWalletComponent } from './history-wallet/history-wallet.component';
import { ALLHistoryComponent } from './allhistory/allhistory.component';
import { AllHistoryWalletComponent } from './allhistory-wallet/allhistory-wallet.component';
import { BlockCopyPasteDirective } from './captcha-page/captcha-disable';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    ProfileComponent,
    OrderComponent,
    CaptchaComponent,
    PaymentComponent,
    HistoryComponent,
    DemoComponent,
    HistoryWalletComponent,
    ALLHistoryComponent,
    AllHistoryWalletComponent,
    BlockCopyPasteDirective
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatRadioModule,
    MatListModule,
    MatToolbarModule,
    MatPaginatorModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatCardModule,
    MatSnackBarModule,
    MatTableModule
  ],
  providers: [authInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
