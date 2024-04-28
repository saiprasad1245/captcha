import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import * as moment from 'moment';

import { TableVirtualScrollDataSource } from 'ng-table-virtual-scroll';
import { AuthService } from '../_services/auth.service';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'history-wallet',
  templateUrl: './history-wallet.component.html',
  styleUrls: ['./history-wallet.component.css'],
})
export class HistoryWalletComponent implements OnInit {
  @ViewChild('cusMatTable', { read: ElementRef }) public cusMatTable: ElementRef<any>;
  @ViewChild('fromDateInput') fromDateInput: ElementRef;
  @ViewChild('toDateInput') toDateInput: ElementRef;
  @ViewChild('fileInput') fileInput: ElementRef;

  searchForm: FormGroup;
  isShowAddButtons = false;
  selectedSupplierDatas: any;


  selectedRows = new SelectionModel<any>(false);
  pageLength: any;
  isLoadingData = false;

  private paginator: MatPaginator;
  selectedFile: any;
  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
   }

  supplierDataSource = new MatTableDataSource<any>();
  
  isShowErrorMessage: boolean;
  stateList = []
  fullStateList = []
  // 

  isAccepting: boolean;
  isRejecting: boolean;
  selectedSupplierCodesForDownloading = [];
  tableData: any;
  displayedColumns = [
    'id',
    'razorpay_order_id',
    'razorpay_payment_id',
    'user_id',
    'type',
    'amount'
  ];
  

  filtersModel = [];
  filterKeys = {};
  pageSize = 5;
  supplierData: any;
  isShowingAcceptanceData: boolean;
  loggedInUserDetails: any
  userRoleId: any
  statusList = []
  private roles: string[];
  isLoggedIn = false;
  showAdminBoard = false;
  showModeratorBoard = false;
  username: string;

  constructor(
    private fb: FormBuilder,private authService: AuthService,private tokenStorageService: TokenStorageService

  ) {


  }

  ngOnInit() {
    this.isLoggedIn = this.tokenStorageService.getUser();
    console.log("this.isLoggedIn"+this.isLoggedIn);
        if (this.isLoggedIn) {
          const user = this.tokenStorageService.getUser();
          this.roles = user.roles;
    
          this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
          this.showModeratorBoard = this.roles.includes('ROLE_MODERATOR');
    
          this.username = user.displayName;
          console.log("this.isLoggedIn"+user);
      
        }

    this.authService.history_Wallet(this.username).subscribe(
      data => {
        console.log(data);
        this.supplierData = data;
        this.updateTableData(data);
      },
      err => {
       
      }
    );
  }
 
  /*  Scrolling the table data div to top on moving to next page*/

  sortEvent(event) {
    if (event.direction == 'asc') {
      this.supplierData.sort((a, b) => (a[event.active] > b[event.active]) ? 1 : -1)
    } else {
      this.supplierData.sort((a, b) => (a[event.active] < b[event.active]) ? 1 : -1)
    }
    this.paginator.pageIndex = 0;
    this.tableData = new MatTableDataSource(this.supplierData.slice(0, this.paginator.pageSize))
  }


  onChangePage(event) {
    this.cusMatTable.nativeElement.scrollTop = 0;
    this.tableData = new MatTableDataSource(this.supplierData.slice(event.pageIndex * event.pageSize, event.pageSize * (event.pageIndex + 1)))
  }

  /* Setting selected rows into selected supplier datas */

  onSelectingRow(selectedRows) {
    this.selectedSupplierDatas =
      selectedRows && selectedRows.length == 0 ? '' : selectedRows;
  }


  /* Funtion to open popup model */


  updateTableData(response) {
    this.supplierData = response;
     console.log("updateTableData",this.supplierData)
     console.log("updateTableData",this.supplierData.length)
     this.pageLength = this.supplierData.length;
     console.log(response.slice(0, this.pageLength))
      this.tableData = new MatTableDataSource(this.supplierData.slice(0, this.pageLength))
    
    //this.getPageOptions();
  }



  getPageOptions() {
    const defaultPageOptions = [5, 25, 50, 100, 500]
    let requiredPageOptions = []
    if (this.supplierData.length) {
      defaultPageOptions.forEach(option => {
        if (option <= this.supplierData.length) {
          requiredPageOptions.push(option)
        }
      })
      !requiredPageOptions.includes(this.supplierData.length) && this.supplierData.length < 500 ? requiredPageOptions.push(this.supplierData.length) : ''
      return requiredPageOptions
    }
  }

  toggleSelection(radioBtn, supplierData) {
    if (this.selectedRows.isSelected(supplierData)) {
      radioBtn.checked = false
    } else {
      this.selectedRows.toggle(supplierData)
    }
  }

  downLoadCertificate(supplierDetails) {
console.log(supplierDetails)
    this.authService.downLoadCertificate(supplierDetails.email, supplierDetails.fileName).subscribe((file: Blob) => {
      let fileData: any = new Blob([file], { type: file.type });
      var downloadURL = window.URL.createObjectURL(fileData);
      // window.open(url);
      var link = document.createElement('a');
      link.href = downloadURL;
      link.download = supplierDetails.fileName;
      link.click();
     // this.selectedSupplierCodesForDownloading.splice(this.selectedSupplierCodesForDownloading.indexOf(supplierDetails.supplierCode), 1)
     
    }, err => {
      //this.selectedSupplierCodesForDownloading.splice(this.selectedSupplierCodesForDownloading.indexOf(supplierDetails.supplierCode), 1)
    
      const popUpDetails = {
        width: '365px',
        action: 'error',
        message: 'Something Went wrong.Please try again'
      }

    })
  }






  getDropDownDataList(dropdownData, formControlName) {
    const searchText = this.searchForm.value[formControlName]
    if (searchText && searchText.trim().length) {
      return dropdownData.filter(obj => obj.toLowerCase().includes(searchText.toLowerCase()))
    } else {
      return dropdownData
    }
  }

  masterToggle() {
    this.isAllSelected() ?
      this.selectedRows.clear() :
      this.supplierData.forEach(row => this.selectedRows.select(row));
  }

  isAllSelected() {
    const numSelected = this.selectedRows.selected.length;
    const numRows = this.supplierData.length;
    return numSelected === numRows;
  }




  getHeightClass() {
    let className;
    if (this.tableData.data.length) {
      if (this.tableData.data.length <= 5) {
        className = 'cus-table-min-height-' + this.tableData.data.length
      } else {
        className = 'cus-table-max-height'
      }
    }
    return className
  }


  downloadSampleFile() {
    let link = document.createElement('a');
    link.setAttribute('type', 'hidden');
    link.href = 'assets/samplefile/SupplierBulkUpload.xlsx';
    link.download = 'supplierBulkUpload';
    document.body.appendChild(link);
    link.click();
    link.remove();
  }
}
