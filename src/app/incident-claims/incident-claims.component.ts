import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild, ElementRef, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { PopupService } from '../services/popup-service.service';
import { TableVirtualScrollDataSource } from 'ng-table-virtual-scroll';
import { UserDetailsService } from '../services/user-details.service';
import * as dropDownDetails from 'src/app/dropDownDetails';
import { SupplierService } from 'src/app/services/supplier.service';
import { MatDialogRef } from '@angular/material/dialog';
import * as moment from 'moment';
import { ValidateFileService } from '../services/validateFile';

@Component({
  selector: 'app-incident-claims',
  templateUrl: './incident-claims.component.html',
  styleUrls: ['./incident-claims.component.scss'],
  //encapsulation: ViewEncapsulation.None
})
export class IncidentClaimsComponent implements OnInit {
  @ViewChild('cusMatTable', { read: ElementRef }) public cusMatTable: ElementRef<any>;
  dropDownDetails = dropDownDetails;
  managerNames: any;
  mName: any
  itemSize = 48
  userRoleId: any
  userRole: any
  userId: any
  displayedColumnss = ['id', 'name'];
  pageLength: any;
  dataSource: any
  paginator: any
  searchForm: any
  userDetails: any
  minDate = new Date('January 1, 1000 00:00:00');
  maxDate = '';
  minDateFrom = new Date('January 1, 1000 00:00:00');
  incidentClaimList: any;
  selectedIncidentClaims: any;
  isLoadingData = false;
  isLoadingBtn: boolean = false;
  attachmentsList: any;
  selectedRows = new SelectionModel<any>(false);
  statusToShowEditOptionForRoleIdOne = ["Draft"];
  technicalArea = dropDownDetails.technicalArea;
  supplierStatus = ["Issued", "Closed", "Cancelled", "Disputed", "Final"];
  greenBgForDate = ["Issued", "Closed", "Final"];
  grayStatus = ["Cancelled , 'Closed'"];
  viewMode = false;
  viewManagerMode = false;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSourceAttributes();
  }
  pageSize = new FormControl();
  isAccepting: boolean
  isRejecting: boolean;
  isManagerView: boolean = false;
  isShowErrorMessage: boolean;
  isShowingAcceptanceData: boolean = false;
  tableData: any
  incidentData = [];
  secondaryNonConformityList = [];
  message = '';
  isLoading = false;
  userIds = []
  stateList = []
  yearList = []
  countryList = []

  setDataSourceAttributes() {
    // this.dataSource.paginator = this.paginator;
    // this.dataSource.sort = this.sort
  }

  paginationPageSize = 20
  displayedColumns = [
    'selectRow',
    'supplierCode',
    'supplierName',
    'address',
    'city',
    'state',
    'country',
    'suprelNo',
    'technicalArea',
    'originatorName',
    'managerName',
    'primaryNonConformity',
    'secondaryNonConformity',
    'suprelStatus',
    'issuedDate',
  ];
  rowData = [];
  statusList = [];

  constructor(private fb: FormBuilder, private popupService: PopupService, private userDetailsService: UserDetailsService,
    private supplierService: SupplierService, private fileValidation: ValidateFileService) { }

  ngOnInit(): void {

    this.userDetailsService.getUserData.subscribe(data => {
      if (data) {
        this.userRoleId = data.userRoleId;
        this.userId = data.userId;
        this.userRole = data.userRole;
      }
    })

    if (this.userRoleId == 2) {
      this.supplierService.getManagerViewData(this.userId).subscribe(data => {
        if (data !=null && data.status != 500) {
          this.isManagerView = true;
          let managerViewData = data[0].suprelStatus == 'Pending' || data[0].suprelStatus == 'Disputed'  ? 'managerViewViaLink' : 'viewModeViaLink';
          this.viewOrEditIncidentClaimList(managerViewData, data[0]);
        }
      }), error => {
        this.isManagerView = false;
        console.log(error)
      }
    }

    this.supplierService.getManagerNamesList().subscribe(data => {
      if (data) {
        this.managerNames = data;
      }
    })

    this.supplierService.getYearList().subscribe(data => {
      if (data) {
        this.yearList = data;
      }
    })


    this.supplierService.getStateList().subscribe(data => {
      if (data) {
        this.stateList = data;
      }
    })

    this.supplierService.getCountryList().subscribe(data => {
      if (data) {
        this.countryList = data;
      }
    })

    if (this.userRoleId == 3) {
      this.statusList = this.dropDownDetails.supplierStatusList
    }
    else {
      //const managerStatus = this.dropDownDetails.statusList.filter(x => { if (x != 'Draft') { this.statusList.push(x) } });
      //} else {
      this.statusList = this.dropDownDetails.statusList
    }
    this.selectedRows.changed.subscribe((change) => {
      this.onSelectingRow(this.selectedRows.selected);
    });
    this.supplierService.getUserIds().subscribe(data => {
      this.userIds = data;
    });

    this.searchForm = this.fb.group({
      supplierCode: '',
      supplierName: '',
      address: '',
      state: null,
      city: '',
      searchTextForCountry: '',
      searchTextForState: '',
      country: null,
      managerName: null,
      incidentOriginator: null,
      technicalArea: null,
      searchTextForTechArea: '',
      searchTextForManagerName: '',
      searchTextForOriginator: '',
      searchTextForIncidentCategory: '',
      searchForSecondaryNonConfirm: '',
      suprel: '',
      primaryNonConformity: null,
      secondaryNonConformity: null,
      status: null,
      searchTextForStatus: '',
      issueFromDate: '',
      issueToDate: '',
      searchTextForYear: '',
      year: null
    });


  }
  onChangingPNC(event) {
    this.searchForm.patchValue({
      secondaryNonConformity: null
    })

    // console.log("testjghg", this.createIncidentForm.get('secondaryNonConformity'))
    this.secondaryNonConformityList = this.dropDownDetails.secondaryNonConformityList[event.value] ? this.dropDownDetails.secondaryNonConformityList[event.value] : [];
  }

  onChangingRole(event) {
    this.isLoading = true;
    this.supplierService.getUserDetails().subscribe(userData => {
      if (userData && userData.length && userData[0].role !== 'User Details Fetch failed') {
        this.isLoading = true;
        if ('Originator' == event.value) {
          console.log(userData[0].role)
          // This condition is for User/originator
          this.userDetails = {
            userRoleId: 1,
            userRole: 'Originator',
            userId: userData[0].modifiedBy,
            userName: userData[0].userName,
            emailId: userData[0].email
          }
        } else if ('Manager' == event.value) {
          // This condition is for Manager/Approver
          this.userDetails = {
            userRoleId: 2,
            userRole: 'Manager',
            userId: userData[0].modifiedBy,
            userName: userData[0].userName,
            emailId: userData[0].email
          }

        } else if (userData[0].status == 'User Details Fetch failed') {
          //this.showErrorMessage();
        }
        this.userDetailsService.setValue(this.userDetails)
        // sessionStorage.setItem('userDetails', JSON.stringify(this.userDetails));
        this.isLoading = false;
      } else {
        this.isLoading = false;
        //this.showErrorMessage();
      }
    }, error => {
      this.isLoading = false;
      //this.showErrorMessage();
    })
    this.resetData();
    this.ngOnInit();

  }
  searchIncidentClaims() {

    if (this.searchForm.valid) {
      this.isLoading = true;
      this.isLoadingData = true;
      //this.viewMode = false;
      this.isShowErrorMessage = false;
      this.isShowingAcceptanceData = false;
      const searchFormValue = this.searchForm.value
      const sendObj = {
        "supplierCode": searchFormValue.supplierCode ? searchFormValue.supplierCode.trim() : '',
        "supplierName": searchFormValue.supplierName ? searchFormValue.supplierName.trim() : '',
        "supplierAddress": searchFormValue.address ? searchFormValue.address.trim() : '',
        "incidentOriginator": searchFormValue.incidentOriginator,
        "city": searchFormValue.city ? searchFormValue.city.trim() : '',
        "state": searchFormValue.state,
        "country": searchFormValue.country,
        "suprelNo": searchFormValue.suprel,
        "managerName": searchFormValue.managerName ? searchFormValue.managerName.trim() : '',
        "technicalArea": searchFormValue.technicalArea,
        "primaryNonConformity": searchFormValue.primaryNonConformity,
        'secondaryNonConformity': searchFormValue.secondaryNonConformity,
        "suprelStatus": searchFormValue.status,
        "issueFromDate": searchFormValue.issueFromDate ? moment(searchFormValue.issueFromDate).format(
          'YYYY-MM-DD'
        ) : '',
        "issueToDate": searchFormValue.issueToDate ? moment(searchFormValue.issueToDate).format(
          'YYYY-MM-DD'
        ) : '',
        "role": this.userRole,
        "userId": this.userId,
        "year": searchFormValue.year
      }
      this.supplierService.getSearchClaim(sendObj).subscribe(data => {
        this.isLoading = false;
        this.isLoadingData = false;
        this.incidentData = [];
        this.selectedIncidentClaims = '';
        if (data && data.length) {
          if (this.userRoleId == 3) {
            data.forEach(element => {
              if (!(element.suprelStatus == 'Pending' || element.suprelStatus == 'Draft')) {
                this.incidentData.push(element);
              }
            });
          } else if (this.userRoleId == 2) {
            data.forEach(element => {
              if (element.suprelStatus != 'Draft') {
                this.incidentData.push(element);
              }
            });
          } else {
            this.incidentData = data;
          }
          this.incidentClaimList = this.incidentData;
          this.tableData = this.incidentClaimList.length > 0 ? new TableVirtualScrollDataSource(this.incidentClaimList.slice(0, this.getPageOptions()[0])) : []
          this.pageLength = this.incidentClaimList.length;
        } else {
          this.incidentClaimList = [];
          this.tableData = []
          this.isShowErrorMessage = false
          this.isLoadingData = false;
        }
      }, error => {
        this.isShowErrorMessage = true
        this.isLoadingData = false;
        this.incidentClaimList = [];
        this.tableData = []
      })
    }
  }

  reportData() {
    this.popupService.openReportGenerateModal(this.incidentClaimList);
  }
  onSelectingFromDate(event) {
    if (new Date(event.value) > new Date('January 1, 1000 00:00:00')) {
      this.minDate = event.value;
    } else {
      this.minDateFrom = new Date('January 1, 1000 00:00:00');
    }
  }

  onSelectingToDate(event) {
    this.maxDate = event.value
  }
  onSelectingRow(selectedRows) {
    this.selectedIncidentClaims =
      selectedRows && selectedRows.length == 0 ? '' : selectedRows;
    if (this.selectedIncidentClaims != '') {
      if (this.userRoleId == 1) {
        if (this.statusToShowEditOptionForRoleIdOne.includes(this.selectedIncidentClaims[0].suprelStatus) &&
          this.userId == this.selectedIncidentClaims[0].createdBy) {
          this.viewMode = false;
        } else {
          this.viewMode = true;
        }
      }
      if (this.userRoleId == 2) {
        this.managerNames.forEach((value) => {
          if (value.managerTid == this.userId) {
            this.mName = value.managerName
          }
        })
        if (this.selectedIncidentClaims[0].suprelStatus != 'Draft' && this.selectedIncidentClaims[0].managerName == this.mName) {
          this.viewMode = true;
          this.viewManagerMode = false;
        } else {
          this.viewMode = false;
          this.viewManagerMode = true;
        }
      }

      if (this.userRoleId == 4) {
        this.viewMode = true;
      }

      if (this.userRoleId == 3) {
        if (this.supplierStatus.includes(this.selectedIncidentClaims[0].suprelStatus)) {
          this.viewMode = true;
        } else {
          this.viewMode = false;
        }
      }
    }
  }


  resetData() {
    this.incidentClaimList = undefined;
    this.searchForm.reset();
    this.isShowingAcceptanceData = false;
    this.onChangingPNC('');
    this.selectedRows = new SelectionModel<any>(false);
    this.selectedIncidentClaims = undefined
  }

  toggleSelection(radioBtn, incidentClaim) {
    if (this.selectedRows.isSelected(incidentClaim)) {
      radioBtn.checked = false;
      this.selectedIncidentClaims = '';
    } else {
      this.selectedRows.toggle(incidentClaim)
      this.onSelectingRow(this.selectedRows.selected)
    }
  }



  getPageOptions() {
    const defaultPageOptions = [5, 25, 50, 100, 500]
    let requiredPageOptions = []
    if (this.incidentClaimList.length) {
      defaultPageOptions.forEach(option => {
        if (option <= this.incidentClaimList.length) {
          requiredPageOptions.push(option)
        }
      })
      !requiredPageOptions.includes(this.incidentClaimList.length) && this.incidentClaimList.length < 500 ? requiredPageOptions.push(this.incidentClaimList.length) : '';
      return defaultPageOptions
    }
  }

  onChangePage(event) {
    this.cusMatTable.nativeElement.scrollTop = 0;
    this.tableData = new TableVirtualScrollDataSource(this.incidentClaimList.slice(event.pageIndex * event.pageSize, event.pageSize * (event.pageIndex + 1)));
  }

  isAllSelected() {
    const numSelected = this.selectedRows.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected() ?
      this.selectedRows.clear() :
      this.dataSource.data.forEach(row => this.selectedRows.select(row));
  }

  viewOrEditIncidentClaimList(type, datas?) {
    this.isLoadingBtn = true;
    let selectedIncidentClaim = type == 'managerViewViaLink' || type == 'viewModeViaLink' ? datas : this.selectedIncidentClaims[0];
    this.supplierService.getAttachmentsList(selectedIncidentClaim.suprelNo).subscribe(data => {
      this.attachmentsList = data;
      this.isLoadingBtn = false;
      const popupData = {
        type: type == 'viewModeViaLink' ? 'viewManagerMode' : type,
        details: selectedIncidentClaim,
        //details: '',
        attachments: this.attachmentsList,
        role: this.userRoleId
      }
      if (this.userRoleId == 3) {
        this.popupService.openIncidentDataModalForSupplierRole(popupData).subscribe(response => {
          if (response != 'Exit') {
            this.searchIncidentClaims();
          }
          else {
            this.selectedRows.clear();
          }
          this.selectedIncidentClaims = '';
        })
      } else {
        this.popupService.openEditIncidentModal(popupData).subscribe(response => {

          if (response != 'Exit') {
            if (response && response.event == 'delete') {
              this.removeClaimFromTable(response.claimDetails)
            }
            this.isShowingAcceptanceData == true && this.userRoleId == 2 ? this.getCertificatesWaitingForAcceptance() : this.searchIncidentClaims();

          }
          this.userRoleId == 2 && this.isManagerView == true ? this.isManagerView = false : '';
          this.selectedRows.clear();
          this.selectedIncidentClaims = '';
        })
      }
    }, error => {
      const popUpDetails = {
        width: '365px',
        action: 'error',
        message: 'Something went wrong.please try again'
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response) {

        }
      })
    })


  }

  removeClaimFromTable(claimDetails) {
    const selectedIndex = this.incidentClaimList.findIndex(obj => obj.suprelNo == claimDetails.suprelNo);
    this.incidentClaimList.splice(selectedIndex, 1);
    this.pageLength = this.incidentClaimList.length;
    this.tableData = new TableVirtualScrollDataSource(this.incidentClaimList.slice(this.paginator.pageIndex * this.paginator.pageSize, this.paginator.pageSize * (this.paginator.pageIndex + 1)))
    this.selectedIncidentClaims = '';
    this.selectedRows = new SelectionModel<any>(false);
    this.selectedRows.changed.subscribe((change) => {
      this.onSelectingRow(this.selectedRows.selected);
    });
  }

  getPageSizeOptions() {

    const defaultPageOptions = [5, 25, 50, 100, 500]
    let requiredPageOptions = []
    if (this.dataSource.data.length) {
      defaultPageOptions.forEach(option => {
        if (option <= this.dataSource.data.length) {
          requiredPageOptions.push(option)
        }
      })
      !requiredPageOptions.includes(this.dataSource.data.length) && this.dataSource.data.length > 500 ? requiredPageOptions.push(this.dataSource.data.length) : '';
      return requiredPageOptions
    }

  }

  getHeightClass() {
    let className;
    if (this.dataSource.data.length) {
      if (this.dataSource.data.length <= 5) {
        className = 'cus-table-min-height-' + this.dataSource.data.length
      } else {
        className = 'cus-table-max-height'
      }
    }
    return className
  }

  sortEvent(event) {
    console.log(event)
    if (event.direction == 'asc') {
      this.incidentClaimList.sort((a, b) => (a[event.active] > b[event.active]) ? 1 : -1)
      // {
      //   (a[event.active] > b[event.active]) ? 1 : -1
      // console.log(a[event.active],b[event.active])})
      console.log(this.incidentClaimList)
      //(a[event.active] > b[event.active]) ? 1 : -1)
    } else {
      this.incidentClaimList.sort((a, b) => (a[event.active] < b[event.active]) ? 1 : -1)
      console.log(this.incidentClaimList)
    }
    this.paginator.pageIndex = 0;
    this.tableData = new TableVirtualScrollDataSource(this.incidentClaimList.slice(0, this.paginator.pageSize))
  }

  createIncidentClaim() {
    const popupData = {}
    this.popupService.openCreateIncidentModal(popupData).subscribe(response => {
      if (response && response.action && response.action == 'add') {
        if (this.incidentClaimList && this.incidentClaimList.length) {
          this.incidentClaimList.unshift(response.incidentClaim);
          this.pageLength = this.incidentClaimList.length;
          this.tableData = new TableVirtualScrollDataSource(this.incidentClaimList.length <= 5 ? this.incidentClaimList : this.incidentClaimList.slice(this.paginator.pageIndex * this.paginator.pageSize, this.paginator.pageSize * (this.paginator.pageIndex + 1)))
        } else {
          this.incidentClaimList = [];
          this.incidentClaimList.push(response.incidentClaim);
          this.pageLength = this.incidentClaimList.length;
          this.tableData = new TableVirtualScrollDataSource(this.incidentClaimList.slice(0, this.getPageOptions()[0]));
        }
        this.searchIncidentClaims();
      }
    })
  }

  exportData(event: any) {
    let claimList = this.selectedIncidentClaims ? this.selectedIncidentClaims : this.incidentClaimList;
    //console.log(claimList) 
    this.supplierService.exportData(event, claimList);
    this.selectedRows.clear();
  }

  getCertificatesWaitingForAcceptance() {
    this.isShowingAcceptanceData = true
    this.managerNames.forEach((value) => {
      if (value.managerTid == this.userId) {
        this.mName = value.managerName
      }
    })
    this.isShowErrorMessage = false;
    this.isLoadingData = true;
    this.supplierService.getCertificatesWaitingForAcceptance(this.mName).subscribe(data => {
      this.isLoadingData = false;
      if (data && data.length) {
        this.incidentData = data;
        this.incidentClaimList = this.incidentData;
        this.incidentClaimList = data;
        this.tableData = new TableVirtualScrollDataSource(this.incidentClaimList.slice(0, this.getPageOptions()[0]))
        this.pageLength = this.incidentClaimList.length;
        this.selectedIncidentClaims = '';
      } else {
        this.incidentClaimList = [];
        this.tableData = [];
        if (!data) {
          this.isShowErrorMessage = true;

        }
      }
    }, error => {
      this.isShowErrorMessage = true;
      this.isLoadingData = false;
      this.incidentClaimList = [];
      this.tableData = []
    })
  }
  openActivityLog(supplierDetails) {
    this.popupService.openAuditLogPopupModel(supplierDetails).subscribe(response => {
      if (response) {

      }
    })
  }

  onSelectingFile(event, type) {
    const sendObjInFormData = new FormData();
    const selectedFile = event.target.files[0];
    let fileType = ['xlsx', 'xls']
    if (this.fileValidation.checkIfFileIsValid(selectedFile, fileType)) {
      this.isLoadingBtn = true;
      sendObjInFormData.append('selectedFile', selectedFile ? selectedFile : new Blob());
      if (type == 'ManagerListUpload') {
        this.supplierService.saveBulkUploadData(sendObjInFormData).subscribe(data => {
          this.isLoadingBtn = false;
          const popUpDetails = {
            width: '262px',
            action: 'BulkUpload',
          }
          this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
            if (response) {
              this.searchIncidentClaims();

            }
          })

        }, error => {
          this.isLoadingBtn = false;
          console.log(error, error.error.message)
          const popUpDetails = {
            width: '365px',
            action: 'error',
            message: error.error.message
          }
          this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
            if (response) {

            }
          })
        })
      }
      if (type == 'BulkUpload') {
        this.supplierService.saveManagerBulkUpload(sendObjInFormData).subscribe(data => {
          this.isLoadingBtn = false;
          const popUpDetails = {
            width: '262px',
            action: 'BulkUpload',
          }
          this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
            if (response) {
              this.searchIncidentClaims();

            }
          })

        }, error => {
          this.isLoadingBtn = false;
          console.log(error, error.error.message)
          const popUpDetails = {
            width: '365px',
            action: 'error',
            message: error.error.message
          }
          this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
            if (response) {

            }
          })
        })
      }
    }
  }

  downloadSampleFile(type) {
    let link = document.createElement('a');
    link.setAttribute('type', 'hidden');
    const downloadLink = type == 'ManagerListUpload' ? 'assets/sampleFile/ManagerList.xlsx' : 'assets/sampleFile/SuprelBulkUpload.xlsx'
    link.href = downloadLink;
    link.download = type == 'ManagerListUpload' ? 'ManagerList' : 'BulkUpload';
    document.body.appendChild(link);
    link.click();
    link.remove();
  }
}
