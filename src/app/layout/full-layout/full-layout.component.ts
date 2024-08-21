import { Component, OnInit } from '@angular/core';
import { UserDetailsService } from 'src/app/services/user-details.service';
import { SupplierService } from '../../services/supplier.service';
import { PopupService } from '../../services/popup-service.service';

@Component({
  selector: 'app-full-layout',
  templateUrl: './full-layout.component.html',
  styleUrls: ['./full-layout.component.scss']
})
export class FullLayoutComponent implements OnInit {
  userStatusForRoleIdOne = ["Originator"];
  userStatusForRoleIdTwo = ["Manager"];
  userStatusForRoleIdThree = ["Supplier"];
  userStatusForRoleIdFourth = ["Admin"];
  sideNavWidth = '10';
  isLoading = true
  userDetails: any
  userId: any
  userRole: any

  constructor(private userDetailsService: UserDetailsService, private supplierService: SupplierService, private PopupService: PopupService) { }

  ngOnInit(): void {

    this.supplierService.getUserDetails().subscribe(userData => {
      if (userData && userData.length && userData[0].role !== 'User Details Fetch failed') {
        if (this.userStatusForRoleIdOne.includes(userData[0].role)) {
          console.log(userData[0].role)
          // This condition is for User/originator
          this.userDetails = {
            userRoleId: 1,
            userRole: userData[0].role,
            userId: userData[0].modifiedBy,
            userName: userData[0].userName,
            emailId: userData[0].email
          }
        } else if (this.userStatusForRoleIdTwo.includes(userData[0].role)) {
          // This condition is for Manager/Approver
          this.userDetails = {
            userRoleId: 2,
            userRole: userData[0].role,
            userId: userData[0].modifiedBy,
            userName: userData[0].userName,
            emailId: userData[0].email
          }

        } else if (this.userStatusForRoleIdThree.includes(userData[0].role)) {
          // This condition is for Supplier Role
          this.userDetails = {
            userRoleId: 3,
            userRole: userData[0].role,
            userId: userData[0].modifiedBy,
            isShowNoDataFound: true,
            userName: userData[0].userName,
            emailId: userData[0].email
          }
        }
        else if (this.userStatusForRoleIdFourth.includes(userData[0].role)) {
          // This condition is for Admin
          this.userDetails = {
            userRoleId: 4,
            userRole: userData[0].role,
            userId: userData[0].modifiedBy,
            userName: userData[0].userName,
            emailId: userData[0].email
          }
        }
        else if (userData[0].supplierCode) {
          // This condition is for existing supplier
          this.userDetails = {
            userRoleId: 3,
            userRole: userData[0].role,
            userId: userData[0].modifiedBy,
            supplierDatas: userData,
            userName: userData[0].userName,
            emailId: userData[0].email
          }
        } else if (userData[0].status == 'User Details Fetch failed') {
          this.showErrorMessage();
        }
        // this.userDetails = {
        //   userRoleId: 1,
        //   userRole: 'Originator',
          //userId:'T0313TG' , // anthony
          //userId:'T3328SG',
          // userId:'S04394I',
          //userId:'S10097E',
          // userName:'Sai Prasad Goparaju',
          // emailId:'saiprasad.goparaju@external.stellantis.com'
          // userName: 'Nirocia',
          // emailId: 'nirocia.chandiran@external.stellantis.com'
        //}
        this.userDetailsService.setValue(this.userDetails)
        // sessionStorage.setItem('userDetails', JSON.stringify(this.userDetails));
        this.isLoading = false;
      } else {
        this.isLoading = false;
        this.showErrorMessage();
      }
    }, error => {
      this.isLoading = false;
      this.showErrorMessage();
    })
  }

  showErrorMessage() {
    const popUpDetails = {
      width: '420px',
      action: 'userDetailsError',
      message: 'Something Went wrong.Please refresh the page'
    }
    this.PopupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
      if (response) {
      }
    })
  }
  downloadSuprelGuide(){
    let link = document.createElement('a');
    link.setAttribute('type', 'hidden');
    link.href = 'assets/suprelGuide/SUPREL_Training.pdf';
    link.download = 'Supplier Relationship Incident Claim (SUPREL) Training';
    document.body.appendChild(link);
    link.click();
    link.remove();
  }
}
