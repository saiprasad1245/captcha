import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../_services/auth.service';
import { MatDialogRef } from '@angular/material/dialog';
import { TokenStorageService } from '../_services/token-storage.service';
@Component({
  selector: 'payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit {
  selectedFile: any;
  form: any = {};
  isSuccessful = false;
  isSignUpFailed = false;
  isUsing2FA = false;
  errorMessage = '';
  qrCodeImage = '';
  errorMesg: string;
  percentDone: number;
  uploadSuccess: boolean;
  private roles: string[];
  isLoggedIn = false;
  showAdminBoard = false;
  showModeratorBoard = false;
  username: string;
  amount: any;
  constructor(private authService: AuthService,private router: Router,private tokenStorageService: TokenStorageService) { }

  ngOnInit(): void {
    this.isLoggedIn = this.tokenStorageService.getUser();
    console.log("this.isLoggedIn"+this.isLoggedIn);
        if (this.isLoggedIn) {
          const user = this.tokenStorageService.getUser();
          this.roles = user.roles;
    
          this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
          this.showModeratorBoard = this.roles.includes('ROLE_MODERATOR');
    
          this.username = user.displayName;
		  this.amount = user.refundAmount;

          console.log("this.user.refundAmount"+user.refundAmount);
      
        }

    this.authService.withdraw(this.username).subscribe(
      data => {
        console.log(data);
        this.amount = data;
      },
      err => {
        this.errorMessage = err.error.message;
      }
    );
  }

  upload(files: File[]){
    //pick from one of the 4 styles of file uploads below
    this.uploadAndProgress(files);
  }

  uploadAndProgress(files: File[]){
    console.log(files[0])
    this.selectedFile = files[0];
    

  }

  onSubmit(): void {
    console.log("onsubmit")
    const sendData = {
      name: this.form.displayName.trim(),
      phone: this.form.phone.trim(),
      address: this.form.address.trim(),
      email: this.form.email.trim(),
    }

    const sendObjInFormData = new FormData()
   
    sendObjInFormData.append('claimFile', this.selectedFile ? this.selectedFile : new Blob())
    sendObjInFormData.append('supplr', JSON.stringify(sendData));
    this.authService.payment(sendObjInFormData).subscribe(
      data => {
        console.log(data);

	    this.isSuccessful = true;
      this.router.navigate(['/captcha-page']);
      },
      err => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    );
  }

}
