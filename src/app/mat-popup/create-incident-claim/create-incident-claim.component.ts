import { Component, ElementRef, HostListener, OnChanges, OnInit, Optional } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import * as dropDownDetails from 'src/app/dropDownDetails';
import { noWhitespaceValidator, validateAllFormFields } from 'src/app/shared/pipes/common-functions';
import { PopupService } from 'src/app/services/popup-service.service';
import { SupplierService } from 'src/app/services/supplier.service';
import { UserDetailsService } from 'src/app/services/user-details.service';
import { ValidateFileService } from 'src/app/services/validateFile';
import { ChildActivationStart } from '@angular/router';
@Component({
  selector: 'app-create-incident-claim',
  templateUrl: './create-incident-claim.component.html',
  styleUrls: ['./create-incident-claim.component.scss']
})

export class CreateIncidentClaimComponent implements OnInit {

  popupData: any;
  createIncidentForm: FormGroup;
  dropDownDetails = dropDownDetails;
  secondaryNonConformityList = [];
  isAdding: boolean;
  isFileSelected: boolean = false;
  isDuplicateFile: boolean = false;
  selectedFile = [];
  allFiles = [];
  fileList = [];
  userEmails = [];
  allowedFileTypes = ['pdf'];
  isLoadingSupplierCodeDetails = false;
  // emailList = [];
  removable = true;
  // public separatorKeysCodes = [ENTER, COMMA];
  stateList = [];
  managerNameList = [];
  managerNames = [];
  technicalArea = dropDownDetails.technicalArea;
  fullStateList = [];
  isLoading = false;
  formControlsToCheckValues = ['supplierCode', 'supplierName', 'country', 'state', 'city', 'primaryNonConformity', 'secondaryNonConformity', 'details', 'file', 'email1', 'email2', 'email3', 'email4', 'email5']
  isInvalidSupplierCode: boolean;

  constructor(private fb: FormBuilder, private dialog: MatDialogRef<CreateIncidentClaimComponent>, private el: ElementRef,
    private supplierService: SupplierService, private popupService: PopupService, private userDetails: UserDetailsService, private fileValidation: ValidateFileService) { }

  ngOnInit(): void {
    this.supplierService.getManagerNamesList().subscribe(data => {
      data.forEach(element => {
        if (element.managerTid != this.userDetails.userdata.value.userId) {
          this.managerNames.push(element)
        }
      })
    })
    this.createForm();

  }

  createForm() {
    this.createIncidentForm = this.fb.group({
      supplierCode: ['', [Validators.required, noWhitespaceValidator, Validators.pattern('^[A-Za-z0-9 ]+$')]],
      supplierName: [''],
      country: '',
      searchTextForCountry: '',
      state: '',
      address: '',
      searchTextForState: '',
      city: '',
      zipCode: '',
      technicalArea: ['', Validators.required],
      managerName: ['', Validators.required],
      searchTextForTechArea: [''],
      searchTextForManagerName: [''],
      primaryNonConformity: ['', Validators.required],
      searchTextForPrimaryNonConformity: '',
      secondaryNonConformity: '',
      searchTextForSecondaryNonConformity: '',
      details: ['', [Validators.required, noWhitespaceValidator]],
      file: [''],
      // file: this.fb.array([]),
      email1: ['', [Validators.required, this.emailValidation]],
      email2: ['', this.emailValidation],
      email3: ['', this.emailValidation],
      email4: ['', this.emailValidation],
      email5: ['', this.emailValidation],
    })

  }

  onSelectingFile(event) {
    this.fileList = [];
    this.isFileSelected = true;
    this.isDuplicateFile = false;
    this.fileList.push(event.target.files);
    //Filtering Duplicate File Name 
    if (this.allFiles.length > 0) {
      const filteredFile = this.fileList.filter((x, i) => this.allFiles.some((y) => {
        x[i].name == y ? this.getFileUploadError('FileExceed', y) : '';

      }))
    }
    // Checking the file length & File Size
    for (let i = 0; i < event.target.files.length; i++) {
      if (this.fileValidation.checkIfFileIsValid(event.target.files[i], 'pdf') && !this.isDuplicateFile) {
        this.selectedFile.push(event.target.files[i]);
        this.allFiles.push(event.target.files[i].name);
        //totalFileSize += event.target.files[i].size;
      }

    }
  }
  removeItem(index: any) {
    this.allFiles.splice(index, 1);
    this.selectedFile.splice(index, 1);
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
      // console.log(this.allFiles, this.selectedFile)
      if (response && type == 'FileSize') {

      }
    })
  }

  private validateArrayNotEmpty(c: FormControl) {
    if (c.value && c.value.length === 0) {
      return {
        validateArrayNotEmpty: { valid: false }
      };
    }
    return null;
  }

  private validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

  closePopup() {
    this.dialog.close()
  }
  emailValidation(control: FormControl) {
    if (control.value) {
      return control.value.includes('stellantis.com') ? { 'incorrect': true } : ''
    }

  }
  onChangingPNC(event) {
    if (event.value == 'Cost Recovery') {
      this.createIncidentForm.addControl('amount', this.fb.control('', [Validators.required]));
      this.createIncidentForm.addControl('currency', this.fb.control('', [Validators.required]));
      this.createIncidentForm.addControl('searchTextForCurrency', this.fb.control(''));
    } else {
      this.createIncidentForm.removeControl('amount');
      this.createIncidentForm.removeControl('currency');
      this.createIncidentForm.removeControl('searchTextForCurrency');
    }
    this.createIncidentForm.patchValue({
      secondaryNonConformity: null
    })
    // console.log("testjghg", this.createIncidentForm.get('secondaryNonConformity'))
    this.secondaryNonConformityList = this.dropDownDetails.secondaryNonConformityList[event.value] ? this.dropDownDetails.secondaryNonConformityList[event.value] : [];
    this.createIncidentForm.get('secondaryNonConformity').setValidators([Validators.required])
    this.createIncidentForm.get('secondaryNonConformity').updateValueAndValidity()
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


  checkSupplierCode(event?: any) {
    if (this.createIncidentForm.value.supplierCode && this.createIncidentForm.controls['supplierCode'].valid) {
      this.createIncidentForm.patchValue({
        supplierCode: this.createIncidentForm.value.supplierCode.trim()
      })
      if (this.createIncidentForm.value.supplierCode.trim() && this.createIncidentForm.value.supplierCode.trim().length >= 5) {
        this.isLoadingSupplierCodeDetails = true;
        this.isInvalidSupplierCode = false;
        //console.log("test", this.createIncidentForm.value.supplierCode.trim().length);
        this.supplierService.getSupplierCodeDetails(this.createIncidentForm.value.supplierCode).subscribe(data => {
          // console.log("testfsfd", data)
          this.isLoadingSupplierCodeDetails = false;
          if (data) {
            this.isInvalidSupplierCode = false
            this.createIncidentForm.patchValue({
              supplierName: data.supplierName.trim(),
              country: data.country.trim(),
              state: data.state.trim(),
              city: data.city.trim(),
              //email1: data.ctEmail.trim(),
              zipCode: data.postalcode.trim(),
              address: data.supplierAddress.trim()

            })

            if (event == 'Draft' || event == 'Pending') {
              this.createIncident(event)
            }


          } else {
            this.ifSupplierCodeisInvalid();

          }
        }, error => {
          this.isLoadingSupplierCodeDetails = false;
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


    }

  }

  ifSupplierCodeisInvalid() {
    this.isInvalidSupplierCode = true;
    this.createIncidentForm.patchValue({
      supplierName: '',
      country: '',
      state: '',
      city: '',
      //email1: '',
      address: '',
      zipCode: '',
    })
    // this.createIncidentForm.controls['supplierCode'].setErrors()

  }

  onEnteringSupplierCode(event) {
    this.isInvalidSupplierCode = false;
    this.createIncidentForm.patchValue({
      supplierName: '',
      country: '',
      state: '',
      city: '',
      //email1: '',
      address: '',
      zipCode: '',

    })
    // if (this.createIncidentForm.controls['supplierCode'].valid) {

    // }
  }



  createIncident(event: any) {
    //event == 'Draft' ? this.createIncidentForm.patchValue({ file: 'file' }) : this.createIncidentForm.patchValue({ file: '' })
    if (event != 'Draft' && (this.createIncidentForm.value.primaryNonConformity == 'SQD Escalation' || this.createIncidentForm.value.primaryNonConformity == 'Cost Recovery') && this.allFiles.length == 0) {
      this.createIncidentForm.controls['file'].setErrors({ 'incorrect': true });
    }
    else {
      this.createIncidentForm.controls['file'].setErrors(null);
    }

    if (this.createIncidentForm.valid && !this.isInvalidSupplierCode) {
      const popUpDetails = {
        action: event == 'Draft' ? 'save' : 'submit'
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response && response != 'cancel') {
          this.addNewClaim(event);
        }
      })

    } else {
      validateAllFormFields(this.createIncidentForm);
      for (const key of Object.keys(this.createIncidentForm.controls)) {
        if (this.createIncidentForm.controls[key].invalid) {
          const invalidControl = this.el.nativeElement.querySelector('[formcontrolname="' + key + '"]');
          invalidControl.focus();
          break;
        }
      }


    }

  }

  addNewClaim(event: any) {
    this.isLoading = true;
    const createIncidentFormValue = this.createIncidentForm.value;
    const userEmails = this.userEmails[0] ? this.userEmails[0] : '';
    const sendData = {
      supplierCode: createIncidentFormValue.supplierCode.trim(),
      supplierName: createIncidentFormValue.supplierName.trim(),
      currencyType: createIncidentFormValue.currency,
      supplierAddress: createIncidentFormValue.address ? createIncidentFormValue.address.trim() : '',
      city: createIncidentFormValue.city,
      state: createIncidentFormValue.state,
      amount: createIncidentFormValue.amount,
      country: createIncidentFormValue.country,
      technicalArea: createIncidentFormValue.technicalArea,
      managerName: createIncidentFormValue.managerName,
      primaryNonConformity: createIncidentFormValue.primaryNonConformity,
      secondaryNonConformity: createIncidentFormValue.secondaryNonConformity,
      details: createIncidentFormValue.details,
      email1: createIncidentFormValue.email1,
      email2: createIncidentFormValue.email2,
      email3: createIncidentFormValue.email3,
      email4: createIncidentFormValue.email4,
      email5: createIncidentFormValue.email5,
      suprelStatus: event,
      createdOn: new Date().toISOString(),
      createdBy: this.userDetails.userdata.value.userId,
      modifiedOn: '',
      modifiedBy: '',
      originatorEmail: this.userDetails.userdata.value.emailId,
      originatorName: this.userDetails.userdata.value.userName,
      postalCode: createIncidentFormValue.zipCode,
      userEmail1: userEmails.UserEmail1 ? userEmails.UserEmail1 : '',
      userEmail2: userEmails.UserEmail2 ? userEmails.UserEmail2 : '',
      userEmail3: userEmails.UserEmail3 ? userEmails.UserEmail3 : '',
      userEmail4: userEmails.UserEmail4 ? userEmails.UserEmail4 : '',
      userEmail5: userEmails.UserEmail5 ? userEmails.UserEmail5 : '',
    }
    let arrofFiles: any = [];
    const sendObjInFormData = new FormData()
    sendObjInFormData.append('supplr', JSON.stringify(sendData));
    const claimFiles = Object.entries(this.selectedFile)
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
    this.supplierService.createNewIncidentClaim(sendObjInFormData).subscribe(data => {
      this.isLoading = false;
      const popUpDetails = {
        width: '262px',
        action: 'created',
      }
      this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
        if (response) {
          const sendObj = {
            action: 'add',
            incidentClaim: sendData
          }
          this.dialog.close(sendObj)
        }
      })


    },
      error => {
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
      }
    )
  }
  openConfirmationPopup(action) {
    const popUpDetails = {
      width: '375px',
      action: action,
    }
    if (action == 'exit') {
      popUpDetails.width = '458px'
    }
    this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
      if (response) {
        if (response == 'clear') {
          this.createIncidentForm.reset();
          this.userEmails = [];

        } else if (response == 'exit') {
          this.closePopup()
        }
      }
    })
  }

  checkFormDataToEnableClearButton() {
    let showClearButton = false
    this.formControlsToCheckValues.forEach(data => {
      if (this.createIncidentForm.value[data]) {
        showClearButton = true
      }
    })
    return showClearButton;
  }
  onClickEmail() {
    const managerEmail = this.managerNames.filter(x => this.createIncidentForm.value.managerName == x.managerName);
    const popUpDetails = {
      width: '600px',
      action: 'AddEmail',
      details: this.createIncidentForm.value,
      userEmail: this.userEmails.length > 0 ? this.userEmails : '',
      managerEmail: managerEmail[0] ? managerEmail[0] : ""
    }
    this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
      if (response) {
        this.userEmails = [];
        this.userEmails.push(response)
      }
    })
  }
}
