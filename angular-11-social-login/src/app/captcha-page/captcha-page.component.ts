import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from 'src/environments/environment';
import { catchError, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { TokenStorageService } from '../_services/token-storage.service';
@Component({
  selector: 'captcha-page',
  templateUrl: './captcha-page.component.html',
  styleUrls: ['./captcha-page.component.css']
})
export class CaptchaComponent implements OnInit {
  title = 'Captcha Generator';
  initialUrl =  environment.apiBaseUrl;
  display: any;
  status: any;
  randomStrings: any;
  private roles: string[];
  isLoggedIn = false;
  showAdminBoard = false;
  showModeratorBoard = false;
  username: string;
  public timerInterval: any;
  countmatch: any;
  constructor(private http: HttpClient, private snackBar: MatSnackBar,private router: Router,private tokenStorageService: TokenStorageService){
  }

  ngOnInit(): void {
    this.isLoggedIn = this.tokenStorageService.getUser();
    console.log("this.isLoggedIn"+this.isLoggedIn);
        if (this.isLoggedIn) {
          const user = this.tokenStorageService.getUser();
          this.roles = user.roles;
    
          this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
          this.showModeratorBoard = this.roles.includes('ROLE_MODERATOR');
    
          this.username = user.displayName;
          console.log("this.isLoggedIn"+user);
          this.countmatch = user.sValue;
        }
  }

  generateRandomString( parameters ): void {
    this.stop();
    this.http.post(`${this.initialUrl}api/auth/randomData`, parameters)
      .subscribe(data => {
          this.randomStrings = data;
          this.start();
          console.log(this.randomStrings)
        },
        err => {
          console.log(`Error occured ${err}`);
        }
      );
  }

  deleteCaptch( value ) {
    this.http.get(`${this.initialUrl}api/auth/deleteCaptcha/${value}`)
    .subscribe(data => {
      const randomData ={length: 1,
        size: 6,
        upperCase: true,
         lowerCase: true,
          digits: true,
           specialCharacters: false};
      this.generateRandomString(randomData);
    });
  }


  submit(value) {
    console.log(value)
    return this.http.get(`${this.initialUrl}api/auth/random/check/${value}/${this.username}`)
    .pipe(map(result => {
      console.log(result)
      return result;
        }));
  }


  submitCaptcha( parameters ) {
    this.submit(parameters).subscribe(data => {
      console.log(data)
      if(data!=null && data!=undefined && data[0]!=null){
          this.status = data[0].randomString;
          this.countmatch = data[0].time;
        if(this.status == parameters){
            this.status = "success";
            this.deleteCaptch(parameters);
        }else{
            this.status = "failure";  
        }
      }else{
        this.status = "failure";  
      }
          console.log(this.status)
        },
        err => {
          console.log(`Error occured ${err}`);
        }
      );
  }
  start() {
    this.timer(1);
  }
  stop() {
    clearInterval(this.timerInterval);
  }

  signOut(){
    this.router.navigate(['/login']);
  }

  timer(minute) {
    // let minute = 1;
    let seconds: number = minute * 30;
    let textSec: any = '0';
    let statSec: number = 30;

    const prefix = minute < 10 ? '0' : '';

    this.timerInterval = setInterval(() => {
      seconds--;
      if (statSec != 0) statSec--;
      else statSec = 29;

      if (statSec < 10) {
        textSec = '0' + statSec;
      } else textSec = statSec;

      this.display = `${prefix}${Math.floor(seconds / 30)}:${textSec}`;

      if (seconds == 0) {
       clearInterval(this.timerInterval);
        this.deleteCaptch(this.randomStrings[0].randomString);
      }
    }, 1000);
  }
  copyText(textToBeCopied) {
    let textarea = null;
    textarea = window.document.createElement("textarea");
    textarea.style.height = "0px";
    textarea.style.left = "-100px";
    textarea.style.opacity = "0";
    textarea.style.position = "fixed";
    textarea.style.top = "-100px";
    textarea.style.width = "0px";
    document.body.appendChild(textarea);
    textarea.value = textToBeCopied;
    textarea.select();
    let successful = document.execCommand("copy");
    if (successful) {
      this.snackBar.openFromComponent(CopiedComponent, {
        duration: 500,
      });
    }
    if (textarea && textarea.parentNode) {
      textarea.parentNode.removeChild(textarea);
    }
  }
}

@Component({
  selector: 'copied-snack',
  template: `
    <span class="copied-snack-bar">
        Copied
    </span>
  `,
  styles: [`
    .copied-snack-bar {
      color: hotpink;
    }
  `],
})
export class CopiedComponent {}
