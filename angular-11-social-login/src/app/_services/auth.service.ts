import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppConstants } from '../common/app.constants';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  login(credentials): Observable<any> {
    return this.http.post(AppConstants.AUTH_API + 'signin', {
      email: credentials.username,
      password: credentials.password
    }, httpOptions);
  }

  register(user): Observable<any> {
    return this.http.post(AppConstants.AUTH_API + 'signup', {
      displayName: user.displayName,
      email: user.email,
      password: user.password,
      matchingPassword: user.matchingPassword,
      socialProvider: 'LOCAL',
      using2FA: user.using2FA
    }, httpOptions);
  }
  
  payment(user): Observable<any> {
    return this.http.post(AppConstants.AUTH_API + 'createPayment', user);
  }

  history(username): Observable<any> {
    return this.http.post(AppConstants.AUTH_API + 'history'+'?username=' + username, '');
  }

  withdraw(username): Observable<any> {
    return this.http.post(AppConstants.AUTH_API + 'withdraw'+'?username=' + username, '');
  }

  allhistory(): Observable<any> {
    return this.http.post(AppConstants.AUTH_API + 'allhistory', httpOptions);
  }


  history_Wallet(username): Observable<any> {
    return this.http.post(AppConstants.AUTH_API + 'history-wallet'+'?username=' + username, '');
  }
  
  public downLoadCertificate(email, fileName) {
    return this.http.get(AppConstants.AUTH_API + 'downloadCertificate' + '?email=' + email + '&fileName=' + fileName, { responseType: 'blob' });
  }

  verify(credentials): Observable<any> {
    return this.http.post(AppConstants.AUTH_API + 'verify', credentials.code, {
    	  headers: new HttpHeaders({ 'Content-Type': 'text/plain' })
    });
  }
}
