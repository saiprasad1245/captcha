import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';

@Injectable({
  providedIn: 'root'
})
export class UserDetailsService {
  public userdata = new BehaviorSubject<any>('')
  public getUserData = this.userdata.asObservable()
  constructor() { }

  setValue(data) {
    this.userdata.next(data);
  }
}
