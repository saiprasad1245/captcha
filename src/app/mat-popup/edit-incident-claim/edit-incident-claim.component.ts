import { ChangeDetectorRef, Component, ElementRef, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { UserDetailsService } from 'src/app/services/user-details.service';
import { noWhitespaceValidator, validateAllFormFields } from 'src/app/shared/pipes/common-functions';
import * as dropDownDetails from 'src/app/dropDownDetails';
import { PopupService } from 'src/app/services/popup-service.service';
import { SupplierService } from 'src/app/services/supplier.service';
import { MAT_MOMENT_DATE_FORMATS, MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { ValidateFileService } from 'src/app/services/validateFile';
import { ViewEncapsulation } from '@angular/compiler';
import { DatePipe } from '@angular/common';
@Component({
  selector: 'app-edit-incident-claim',
  templateUrl: './edit-incident-claim.component.html',
  styleUrls: ['./edit-incident-claim.component.scss'],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true } }
  ],
  //encapsulation: ViewEncapsulation.None
})
export class EditIncidentClaimComponent implements OnInit {
  popupData: any;
  editIncidentForm: FormGroup;
  dropDownDetails = dropDownDetails;
  secondaryNonConformityList = [];
  isAdding: boolean;
  isFileSelected: boolean = false;
  isDuplicateFile: boolean = false;
  selectedFile = [];
  selectedIncidentClaim: any;
  userDetails: any;
  allFiles = [];
  fileList = [];
  deletedFiles = [];
  isLoading: any;
  isViewmode = false;
  viewManagerMode = false;
  managerViewViaLink :boolean = false;
  fileUploadDisable = false;
  isDisputedStatus = false;
  isPendingStatus = false;
  userRoleId: any;
  userRole: any;
  attachmentsList: any;
  userId: any;
  userName: any;
  userEmailId: any;
  allowedFileTypes = ['pdf'];
  incidentDetails: any;
  isShowEditOptionsAndIssuedDate: boolean;
  managerNames: any;
  technicalArea = dropDownDetails.technicalArea;
  checkStatusToShowEditOptionsForRoleIdOne = ["Pending", "Issued", "Cancelled", "Closed", "Disputed", "Final"]
  checkStatusToShowEditOptionsForRoleIdTwo = ["Issued", "Cancelled", "Disputed", "Closed", "Final"]
  constructor(private fb: FormBuilder, private dialog: MatDialogRef<EditIncidentClaimComponent>, private userDetailsService: UserDetailsService, private el: ElementRef,
    private supplierService: SupplierService, private popupService: PopupService, private fileValidation: ValidateFileService,
    private datePipe: DatePipe) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.supplierService.getManagerNamesList().subscribe(data => {
      
      if (data) {
        this.isLoading = false;
        this.managerNames = data;
      }
    }),error =>{
      this.isLoading =false;
      console.log(error)
    }
    this.incidentDetails = this.popupData.details
    this.userDetailsService.getUserData.subscribe(data => {
      if (data) {
        // console.log(data)
        this.userDetails = data;
        this.userRoleId = data.userRoleId;
        this.userId = data.userId;
        this.userRole = data.userRole;
        this.userName = data.userName;
        this.userEmailId = data.emailId;
        this.createForm();
        this.checkOptions();
      }
    })
    //console.log("testuserrole", this.userRoleId == 4, "yytyty", this.userRoleId != 4)
    this.setFormValue();
  }

  setFormValue() {
    this.selectedIncidentClaim = this.popupData.details;
    this.attachmentsList = this.popupData.attachments;
    this.allFiles = this.attachmentsList.map(x => x.fileName);
    this.editIncidentForm.patchValue({
      supplierCode: this.selectedIncidentClaim.supplierCode,
      supplierName: this.selectedIncidentClaim.supplierName,
      suprelIncidentNumber: this.selectedIncidentClaim.suprelNo,
      incidentStatus: this.selectedIncidentClaim.suprelStatus,
      incidentStatusAdmin: this.selectedIncidentClaim.suprelStatus,
      primaryNonConformity: this.selectedIncidentClaim.primaryNonConformity,
      secondaryNonConformity: this.selectedIncidentClaim.secondaryNonConformity,
      amount: this.selectedIncidentClaim.amount != 'undefined' && this.selectedIncidentClaim.amount != null ? this.selectedIncidentClaim.amount : '',
      currency: this.selectedIncidentClaim.currencyType != 'undefined' && this.selectedIncidentClaim.currencyType != null ? this.selectedIncidentClaim.currencyType : '',
      details: this.selectedIncidentClaim.details,
      technicalArea: this.selectedIncidentClaim.technicalArea,
      managerName: this.selectedIncidentClaim.managerName,
      file: this.allFiles[0],
      issuedDate: this.selectedIncidentClaim.issuedDate && this.selectedIncidentClaim.issuedDate.split('T')[0] ? this.datePipe.transform(this.selectedIncidentClaim.issuedDate.split('T')[0], 'MM/dd/yyyy') : '',
      originator: this.selectedIncidentClaim.originatorName,
      supplierComments: this.selectedIncidentClaim.supplierComments != 'undefined' && this.selectedIncidentClaim.supplierComments != null ? this.selectedIncidentClaim.supplierComments : '',
      managerComments: this.selectedIncidentClaim.managerComments != 'undefined' && this.selectedIncidentClaim.managerComments != null ? this.selectedIncidentClaim.managerComments : '',
    })
    if (this.popupData.role == 1) {
      this.isViewmode = this.popupData.type == 'view' ? true : false;
    }

    if (this.popupData.role == 2) {
      this.managerViewViaLink = this.popupData.type == 'managerViewViaLink' ? true : false;
      this.viewManagerMode = (this.popupData.type == 'viewManagerMode' ) ? true : false;
      this.isPendingStatus = this.incidentDetails.suprelStatus == 'Pending' ? true : false;
      if (this.viewManagerMode == false) {
        this.isDisputedStatus = this.incidentDetails.suprelStatus == 'Disputed' ? true : false;
        this.isViewmode = (this.popupData.type == 'view' || this.popupData.type == 'Edit') ? true : false;
      }
    }

    if (this.popupData.role == 4) {
      this.isPendingStatus = this.incidentDetails.suprelStatus == 'Pending' ? true : false;
      this.fileUploadDisable = true;
    }
  }

  createForm() {
    this.editIncidentForm = this.fb.group({
      supplierCode: ['', [Validators.required, noWhitespaceValidator, Validators.pattern('^[A-Za-z0-9 ]+$')]],
      supplierName: ['', [Validators.required, noWhitespaceValidator]],
      suprelIncidentNumber: ['', Validators.required],
      incidentStatus: ['', Validators.required],
      incidentStatusAdmin: ['', Validators.required],
      searchTextForIncidentStatus: '',
      searchTextForIncidentStatusAdmin: '',
      primaryNonConformity: ['', Validators.required],
      technicalArea: [''],
      managerName: ['', Validators.required],
      searchTextForPrimaryNonConformity: '',
      secondaryNonConformity: ['', Validators.required],
      searchTextForTechArea: '',
      searchTextForManagerName: [''],
      searchTextForSecondaryNonConformity: '',
      details: ['', [Validators.required, noWhitespaceValidator]],
      file: [''],
      issuedDate: '',
      originator: '',
      supplierComments: '',
      managerComments: '',
      incidentStatusDropDown: ''

    })
    this.onChangingPNC(this.incidentDetails.primaryNonConformity);
  }

  checkOptions() {
    if (this.userRoleId == 1 && this.userId == this.incidentDetails.createdBy) {
      //console.log(this.incidentDetails)
      this.isShowEditOptionsAndIssuedDate = this.checkStatusToShowEditOptionsForRoleIdOne.includes(this.incidentDetails.suprelStatus)
    }
    else if (this.userRoleId == 2) {
      this.isShowEditOptionsAndIssuedDate = this.checkStatusToShowEditOptionsForRoleIdTwo.includes(this.incidentDetails.suprelStatus)
    }
    else {
      this.isShowEditOptionsAndIssuedDate = true;
    }
    if (this.isShowEditOptionsAndIssuedDate) {
      this.editIncidentForm.addControl('issuedDate', new FormControl(''));
    }
  }

  addSupplierCommentsField(status) {
    if (status == 'Disputed') {
      this.editIncidentForm.addControl('supplierComments', new FormControl(['', Validators.required]));
    }
  }
  onDeletingFile(index: any, editForm: any, file) {
    const popUpDetails = {
      width: '390px',
      action: 'deleteFileConfirmation'
    }
    this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
      if (response && response != 'cancel') {
        this.removeItem(index, editForm, file);
      }
    })
  }
  removeItem(index: any, editForm: any, file: any) {
    this.allFiles.splice(index, 1);
    const findIndexofFile = this.selectedFile.findIndex(x => x.name == file);
    findIndexofFile >= 0 ? this.selectedFile.splice(findIndexofFile, 1) : '';
    this.deletedFiles.push(file);
  }
  onSelectingFile(event) {
    this.fileList = [];
    let totalFileSize = 0
    this.isFileSelected = true;
    this.isDuplicateFile = false;
    this.fileList.push(event.target.files);
    //Filtering Duplicate File Name 
    if (this.allFiles.length > 0) {
      const filteredFile = this.fileList.filter((x, i) => this.allFiles.some((y) => {
        const disputedFile = y.includes('Disputed_') ? y.split('_')[1] : y;
        x[i].name == disputedFile ? this.getFileUploadError('FileExceed', disputedFile) : '';

      }))
    }
    // Checking the file length & File Size
    for (let i = 0; i < event.target.files.length; i++) {
      if (this.fileValidation.checkIfFileIsValid(event.target.files[i], 'pdf') && !this.isDuplicateFile) {
        this.selectedFile.push(event.target.files[i]);
        this.allFiles.push(event.target.files[i].name);
      }

    }
    //console.log(this.editIncidentForm.value)

  }
  getFileUploadError(type, fileName?) {
    let popUpDetails;
    if (type === 'FileExceed') {
      this.isDuplicateFile = true
      popUpDetails = {
        width: '365px',
        action: 'fileUploadInfo',
        message: fileName + ' is Already existing'
      }
    }


    this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
      if (response && type == 'FileSize') {
        //this.removeItem(fileName)
        //this.dialog.close();
      }
    })
  }
  closePopup() {
    this.dialog.close('Exit')
  }

  submitForm(event) {
    //event == 'Draft' || this.allFiles.length != 0 ? this.editIncidentForm.patchValue({ file: 'file' }) : this.editIncidentForm.patchValue({ file: '' })
    if (event != 'Draft' && (this.editIncidentForm.value.primaryNonConformity == 'SQD Escalation' || this.editIncidentForm.value.primaryNonConformity == 'Cost Recovery') && this.allFiles.length == 0) {
      this.editIncidentForm.controls['file'].setErrors({ 'incorrect': true });
    }
    else {
      this.editIncidentForm.controls['file'].setErrors(null);
    }
    if (this.editIncidentForm.valid) {

      const popUpDetails = {
        action: event == 'Draft' ? 'save' : 'submit'
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response && response != 'cancel') {
          this.editClaim(event);
        }
      })
    } else {

      validateAllFormFields(this.editIncidentForm);
      for (const key of Object.keys(this.editIncidentForm.controls)) {
        if (this.editIncidentForm.controls[key].invalid) {
          const invalidControl = this.el.nativeElement.querySelector('[formcontrolname="' + key + '"]');
          invalidControl.focus();
          break;
        }
      }
    }
  }

  onChangingPNC(pnc) {
    this.editIncidentForm.get('secondaryNonConformity').setValidators([Validators.required])
    if (pnc == 'Cost Recovery') {
      this.editIncidentForm.addControl('amount', this.fb.control('', [Validators.required]));
      this.editIncidentForm.addControl('currency', this.fb.control('', [Validators.required]));
      this.editIncidentForm.addControl('searchTextForCurrency', this.fb.control(''));
    } else {
      this.editIncidentForm.removeControl('amount');
      this.editIncidentForm.removeControl('currency');
      this.editIncidentForm.removeControl('searchTextForCurrency');
    }
    this.editIncidentForm.patchValue({
      secondaryNonConformity: null
    })
    this.secondaryNonConformityList = this.dropDownDetails.secondaryNonConformityList[pnc] ? this.dropDownDetails.secondaryNonConformityList[pnc] : [];
    this.editIncidentForm.get('secondaryNonConformity').setValidators([Validators.required])
    this.editIncidentForm.get('secondaryNonConformity').updateValueAndValidity()
  }
  onKeydown(event) {
    let regex: RegExp = new RegExp(/^\d*\.?\d{0,2}$/g);
    let specialKeys: Array<string> = ['Backspace', 'Tab', 'End', 'Home', 'ArrowLeft', 'ArrowRight', 'Del', 'Delete'];
    let value = event.target.value;
    if (specialKeys.indexOf(event.key) !== -1) {
      return;
    }
    let current: string = value;
    const position = event.target.selectionStart;
    const next: string = [current.slice(0, position), event.key == 'Decimal' ? '.' : event.key, current.slice(position)].join('');
    if (next && !String(next).match(regex)) {
      event.preventDefault();
    }
  }
  editClaim(event: any) {
    this.isLoading = true;
    const createIncidentFormValue = this.editIncidentForm.value
    const sendData = {
      supplierCode: createIncidentFormValue.supplierCode.trim(),
      supplierName: createIncidentFormValue.supplierName.trim(),
      currencyType: createIncidentFormValue.currency,
      supplierAddress: this.selectedIncidentClaim.supplierAddress ? this.selectedIncidentClaim.supplierAddress.trim() : '',
      amount: createIncidentFormValue.amount,
      suprelNo: createIncidentFormValue.suprelIncidentNumber,
      primaryNonConformity: createIncidentFormValue.primaryNonConformity,
      secondaryNonConformity: createIncidentFormValue.secondaryNonConformity,
      details: createIncidentFormValue.details.trim(),
      city: this.selectedIncidentClaim.city,
      state: this.selectedIncidentClaim.state,
      country: this.selectedIncidentClaim.country,
      technicalArea: createIncidentFormValue.technicalArea,
      managerName: createIncidentFormValue.managerName,
      suprelStatus: event,
      modifiedBy: this.userDetails.userId,
      modifiedOn: new Date().toISOString(),
      createdBy: this.selectedIncidentClaim.createdBy,
      createdOn: this.selectedIncidentClaim.createdOn,
      email1: this.selectedIncidentClaim.email1,
      email2: this.selectedIncidentClaim.email2,
      email3: this.selectedIncidentClaim.email3,
      email4: this.selectedIncidentClaim.email4,
      email5: this.selectedIncidentClaim.email5,
      supplierComments: this.selectedIncidentClaim.supplierComments,
      managerComments: this.selectedIncidentClaim.managerComments,
      originatorEmail: this.userDetails.emailId,
      originatorName: this.userDetails.userName,
      postalCode: this.selectedIncidentClaim.postalCode,
      userEmail1: this.selectedIncidentClaim.userEmail1 ? this.selectedIncidentClaim.userEmail1 : '',
      userEmail2: this.selectedIncidentClaim.userEmail2 ? this.selectedIncidentClaim.userEmail2 : '',
      userEmail3: this.selectedIncidentClaim.userEmail3 ? this.selectedIncidentClaim.userEmail3 : '',
      userEmail4: this.selectedIncidentClaim.userEmail4 ? this.selectedIncidentClaim.userEmail4 : '',
      userEmail5: this.selectedIncidentClaim.userEmail5 ? this.selectedIncidentClaim.userEmail5 : ''
    }
    const sendObjInFormData = new FormData();
    let arrofFiles: any = [];
    let deleteFile: any = [];
    this.deletedFiles.filter((x: any) => {
      deleteFile.push(x)
    })
    sendObjInFormData.append('supplr', JSON.stringify(sendData));
    const claimFiles = this.selectedFile ? Object.entries(this.selectedFile) : [];
    claimFiles.filter((x: any) => {
      arrofFiles.push(x[1] ? x[1] : new Blob())
    })
    if (arrofFiles.length > 0) {
      for (let file of arrofFiles) {
        sendObjInFormData.append('claimFile', file ? file : new Blob());
      }
    } else {
      sendObjInFormData.append('claimFile', new Blob());
    }
    sendObjInFormData.append('deleteFiles', deleteFile);
    this.supplierService.saveAndUpdateIncidentClaim(sendObjInFormData).subscribe(data => {
      this.isLoading = false;
      const popUpDetails = {
        width: '305px',
        action: 'update',
        // message: 'Saved Successfully',
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response) {
          const sendObj = {
            action: 'add',
            supplierData: sendData
          }
          this.dialog.close(sendObj)
        }
      })


    }, error => {
      this.isAdding = false;
      this.isLoading = false;
      const popUpDetails = {
        width: '365px',
        action: 'error',
        message: 'Something went wrong.please try again'
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {

        if (response) {
          //this.dialog.close();
        }
      })

    })
  }

  deleteIncidentClaim() {
    const popUpDetails = {
      width: '390px',
      action: 'deleteConfirmation'
    }
    this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
      if (response && response != 'cancel') {
        this.deleteClaim();
      }
    })
  }

  deleteClaim() {
    this.isLoading = true;
    const createIncidentFormValue = this.editIncidentForm.value
    this.supplierService.deleteIncidentClaim(createIncidentFormValue.suprelIncidentNumber.trim()).subscribe(data => {
      this.isLoading = false;
      const popUpDetails = {
        width: '262px',
        action: 'delete',
        message: 'deleted Successfully',
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {

        const sendData = {
          event: 'delete',
          claimDetails: this.selectedIncidentClaim
        }
        this.dialog.close(sendData)

      })


    }, error => {
      this.isAdding = false;
      this.isLoading = false;
      const popUpDetails = {
        width: '365px',
        action: 'error',
        message: 'Something went wrong.please try again'
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response) {
          //this.dialog.close()
        }
      })
    })
  }

  claimOperations(event: any) {
    const popUpDetails = {
      width: event == 'Submit' ? '335px' : '305px',
      action: this.userRoleId == 4 ? 'adminClaim' : event
    }
    if (this.editIncidentForm.valid) {

      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response) {
          this.userRoleId == 4 ? this.changeStatusclaimOperations(event, '') : this.changeStatusclaimOperations(event, response)
        }
      })
    } else {
      validateAllFormFields(this.editIncidentForm);
      for (const key of Object.keys(this.editIncidentForm.controls)) {
        if (this.editIncidentForm.controls[key].invalid) {
          const invalidControl = this.el.nativeElement.querySelector('[formcontrolname="' + key + '"]');
          invalidControl.focus();
          break;
        }
      }
    }

  }

  changeStatusclaimOperations(event: any, response) {
    let Event = event == "" ? this.editIncidentForm.value.incidentStatus : event;
    this.isLoading = {}
    let sendData = {}
    this.isLoading[event] = true;
    // console.log(this.isLoading)
    const createIncidentFormValue = this.editIncidentForm.value;
    let managerComments = event == 'Rejected' || event == 'Cancelled' || event == 'Issued' ? response.reason : createIncidentFormValue.managerComments;

    let managerName = createIncidentFormValue.managerName;

    // if (event == 'Pending') {
    sendData = {
      modifiedBy: this.userDetails.userId,
      modifiedOn: new Date().toISOString(),
      suprelNo: createIncidentFormValue.suprelIncidentNumber,
      suprelStatus: Event,
      primaryNonConformity: createIncidentFormValue.primaryNonConformity,
      secondaryNonConformity: createIncidentFormValue.secondaryNonConformity,
      amount: createIncidentFormValue.amount,
      currencyType: createIncidentFormValue.currency,
      details: createIncidentFormValue.details.trim(),
      technicalArea: createIncidentFormValue.technicalArea,
      comments: managerComments,
      managerName: managerName,
      supplierId: null,
      role: this.userRole
    }

    const sendObjInFormData = new FormData();
    sendObjInFormData.append('statusChange', JSON.stringify(sendData));
    this.supplierService.changeIncidentClaimStatus(sendObjInFormData).subscribe(data => {
      this.isLoading = undefined;
      const popUpDetails = {
        width: '225px',
        action: 'claimOperations',
        event: event
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {

        this.dialog.close()

      })


    }, error => {
      this.isAdding = false;
      this.isLoading = false;
      const popUpDetails = {
        width: '365px',
        action: 'error',
        message: 'Something went wrong.please try again'
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response) {
          //this.dialog.close()
        }
      })
    })

  }

  downLoadCertificate(editForm, files) {
    this.supplierService.downLoadCertificate(editForm.value.suprelIncidentNumber, editForm.value.supplierCode, files).subscribe((file: Blob) => {
      let fileData: any = new Blob([file], { type: file.type });
      var downloadURL = window.URL.createObjectURL(fileData);
      var link = document.createElement('a');
      link.href = downloadURL;
      link.download = files;
      link.click();
    }, err => {
      const popUpDetails = {
        width: '365px',
        action: 'error',
        message: 'Something Went wrong.Please try again'
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response) {
          //this.dialog.close();
        }
      })
    })
  }

}
