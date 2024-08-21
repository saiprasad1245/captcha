import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { PopupService } from 'src/app/services/popup-service.service';
import { UserDetailsService } from 'src/app/services/user-details.service';
import { noWhitespaceValidator } from 'src/app/shared/pipes/common-functions';
import * as dropDownDetails from 'src/app/dropDownDetails';
import { SupplierService } from 'src/app/services/supplier.service';
import { ValidateFileService } from 'src/app/services/validateFile';
import { DatePipe } from '@angular/common';
@Component({
  selector: 'app-edit-incident-for-supplier-role',
  templateUrl: './edit-incident-for-supplier-role.component.html',
  styleUrls: ['./edit-incident-for-supplier-role.component.scss']
})
export class EditIncidentForSupplierRoleComponent implements OnInit {

  popupData: any;
  userDetails: any;
  userRoleId: any;
  userRole: any;
  incidentDetails: any;
  secondaryNonConformityList = [];
  allFiles = [];
  fileList = [];
  deletedFiles = [];
  isAccepting: boolean = false;
  isDisputing: boolean;
  isDuplicateFile: boolean = false;
  dropDownDetails = dropDownDetails;
  isDownloading: boolean;
  isLoading: boolean = false;
  selectedSupplierData: any;
  selectedFile: any;
  allowedFileTypes = ['pdf'];
  attachmentsList: any;
  checkStatusToShowEditOptionsForRoleId = ["Cancelled", "Disputed", "Closed", "Final"]
  isViewMode = false;
  constructor(private fb: FormBuilder, private popupService: PopupService, private dialog: MatDialogRef<EditIncidentForSupplierRoleComponent>, private userDetailsService: UserDetailsService,
    private supplierService: SupplierService, private fileValidation: ValidateFileService, private datePipe: DatePipe) { }
  incidentForm: any
  ngOnInit(): void {
    this.incidentDetails = this.popupData.details
    this.userDetailsService.getUserData.subscribe(data => {
      if (data) {
        this.userDetails = data;
        this.userRoleId = data.userRoleId;
        this.userRole = data.userRole
        this.createForm();
      }
    })
    if (this.checkStatusToShowEditOptionsForRoleId.includes(this.incidentDetails.suprelStatus)) {
      this.isViewMode = true;
    }
  }

  createForm() {
    this.incidentForm = this.fb.group({
      incidentNumber: ['', Validators.required],
      supplierCode: ['', [Validators.required, noWhitespaceValidator, Validators.pattern('^[A-Za-z0-9 ]+$')]],
      supplierName: ['', [Validators.required, noWhitespaceValidator]],
      incidentStatus: ['', Validators.required],
      technicalArea: '',
      managerName: '',
      primaryNonConformity: ['', Validators.required],
      secondaryNonConformity: ['', Validators.required],
      details: ['', Validators.required],
      file: ['', Validators.required],
      issuedDate: '',
      originator: '',
      amount: '',
      currency: '',
      managerComments: '',
      supplierComments: ''
    })
    if (this.popupData) {
      this.setFormValue()
    }
  }

  setFormValue() {
    this.selectedSupplierData = this.popupData.details;
    //console.log(this.popupData)
    this.attachmentsList = this.popupData.attachments;
    this.allFiles = this.attachmentsList.map(x => x.fileName);
    this.incidentForm.patchValue({
      incidentNumber: this.selectedSupplierData.suprelNo,
      supplierCode: this.selectedSupplierData.supplierCode,
      supplierName: this.selectedSupplierData.supplierName,
      primaryNonConformity: this.selectedSupplierData.primaryNonConformity,
      secondaryNonConformity: this.selectedSupplierData.secondaryNonConformity,
      details: this.selectedSupplierData.details,
      technicalArea: this.selectedSupplierData.technicalArea,
      managerName: this.selectedSupplierData.managerName,
      incidentStatus: this.selectedSupplierData.suprelStatus,
      supplierComments: this.selectedSupplierData.supplierComments != undefined && this.selectedSupplierData.supplierComments != null ? this.selectedSupplierData.supplierComments : '',
      amount: this.selectedSupplierData.amount != undefined && this.selectedSupplierData.amount != null ? this.selectedSupplierData.amount : '',
      currency: this.selectedSupplierData.currencyType != undefined && this.selectedSupplierData.currencyType != null ? this.selectedSupplierData.currencyType : '',
      file: this.allFiles[0],
      issuedDate: this.selectedSupplierData.issuedDate && this.selectedSupplierData.issuedDate.split('T')[0] ? this.datePipe.transform(this.selectedSupplierData.issuedDate.split('T')[0], 'MM/dd/yyyy') : '',
      originator: this.selectedSupplierData.originatorName,
      managerComments: this.selectedSupplierData.managerComments != 'undefined' && this.selectedSupplierData.managerComments != null ? this.selectedSupplierData.managerComments : '',
    })

  }

  disputeClaim() {
    const popUpDetails = {
      action: 'dispute',
      width: '400px',
      attachmentsList: this.popupData.attachments
    }
    //console.log(popUpDetails)
    this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {

      if (response && response != 'cancel') {
        this.disputeClaimwithFile('Disputed', response);
        //this.claimOperations('Dispute', response);

      }
    })
  }

  closePopup() {
    this.dialog.close('Exit')
  }

  disputeClaimwithFile(event: any, response?) {
    //const sendData = this.popupData.details
    const sendData = {
      supplierCode: this.popupData.details.supplierCode.trim(),
      supplierName: this.popupData.details.supplierName.trim(),
      currencyType: this.popupData.details.currencyType,
      supplierAddress: this.popupData.details.supplierAddress ? this.popupData.details.supplierAddress.trim() : '',
      amount: this.popupData.details.amount,
      suprelNo: this.popupData.details.suprelNo,
      primaryNonConformity: this.popupData.details.primaryNonConformity,
      secondaryNonConformity: this.popupData.details.secondaryNonConformity,
      details: this.popupData.details.details,
      city: this.popupData.details.city,
      state: this.popupData.details.state,
      country: this.popupData.details.country,
      technicalArea: this.popupData.details.technicalArea,
      managerName: this.popupData.details.managerName,
      suprelStatus: event,
      issuedDate: this.popupData.details.issuedDate,
      modifiedBy: this.userDetails.userId,
      modifiedOn: new Date().toISOString(),
      createdBy: this.popupData.details.createdBy,
      createdOn: this.popupData.details.createdOn,
      email1: this.popupData.details.email1,
      email2: this.popupData.details.email2,
      email3: this.popupData.details.email3,
      email4: this.popupData.details.email4,
      email5: this.popupData.details.email5,
      supplierComments: response.reason,
      managerComments: this.popupData.details.managerComments,
      originatorEmail: this.popupData.details.originatorEmail,
      originatorName: this.popupData.details.originatorName,
      postalCode: this.popupData.details.postalCode,
      userEmail1:  this.popupData.details.userEmail1 ? this.popupData.details.userEmail1 :'' ,
      userEmail2:  this.popupData.details.userEmail2 ? this.popupData.details.userEmail2 :'' ,
      userEmail3:  this.popupData.details.userEmail3 ? this.popupData.details.userEmail3 :'' ,
      userEmail4:  this.popupData.details.userEmail4 ? this.popupData.details.userEmail4 :'' ,
      userEmail5:  this.popupData.details.userEmail5 ? this.popupData.details.userEmail5 :'' 
    }
    //console.log(sendData)
    this.selectedFile = response.file;
    this.isLoading = true;
    this.isDisputing = true;
    const sendObjInFormData = new FormData();
    let arrofFiles: any = [];
    sendObjInFormData.append('supplr', JSON.stringify(sendData));
    const claimFiles = this.selectedFile ? Object.entries(this.selectedFile) : [];
    claimFiles.filter((x: any) => {
      arrofFiles.push(x[1] ? x[1] : new Blob())
    })
    let deleteFile: any = [];
    this.deletedFiles.filter((x: any) => {
      deleteFile.push(x)
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
      this.claimOperations(event, response);
      // this.isLoading = false;
      this.isDisputing = false;
    }, error => {
      console.log(error)
      this.isLoading = false;
      this.isDisputing = false;
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

  downLoadCertificate(editForm, files) {
    this.isDownloading = true;
    this.supplierService.downLoadCertificate(editForm.value.incidentNumber, editForm.value.supplierCode, files).subscribe((file: Blob) => {
      let fileData: any = new Blob([file], { type: file.type });
      var downloadURL = window.URL.createObjectURL(fileData);
      var link = document.createElement('a');
      link.href = downloadURL;
      link.download = files;
      link.click();
      this.isDownloading = false;

    }, err => {
      this.isDownloading = false;
      const popUpDetails = {
        width: '365px',
        action: 'error',
        message: 'Something Went wrong.Please try again'
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response) {

        }
      })
    })
  }

  acceptClaimOperation(event: any) {

    const popUpDetails = {
      action: 'acceptFile',
      width: '400px',
    }
    this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
      if (response && response != 'cancel') {
        this.disputeClaimwithFile('Final', response);
      }
    })
  }

  claimOperations(event: any, response?) {
    //console.log(response,event)
    let reason = event == 'Disputed' ? response.reason : '';
    this.isAccepting = true;
    this.isLoading = true;
    this.isDisputing = event == 'Disputed' ? true : false;
    const createIncidentFormValue = this.incidentForm.value;
    let managerName = createIncidentFormValue.managerName;
     const sendData = {
        modifiedBy: this.userDetails.userId,
        modifiedOn: new Date().toISOString(),
        suprelNo: createIncidentFormValue.incidentNumber,
        suprelStatus: event,
        primaryNonConformity: createIncidentFormValue.primaryNonConformity,
        secondaryNonConformity: createIncidentFormValue.secondaryNonConformity,
        details: createIncidentFormValue.details.trim(),
        technicalArea: createIncidentFormValue.technicalArea,
        comments: reason,
        managerName: managerName,
        supplierId: this.userDetails.userId,
        role: this.userRole
      }

    const sendObjInFormData = new FormData();
    sendObjInFormData.append('statusChange', JSON.stringify(sendData));
    this.supplierService.changeIncidentClaimStatus(sendObjInFormData).subscribe(data => {
      //this.isAccepting = false
      const popUpDetails = {
        width: '270px',
        action: event,
        // message: 'issued Successfully',
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        //this.isLoading = false;
        //this.isDisputing = false;
        this.dialog.close()

      })
    }, error => {
      this.isAccepting = false
      this.isDisputing = false;
      this.isLoading = false;
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
    this.deletedFiles.push(file);
  }

}
