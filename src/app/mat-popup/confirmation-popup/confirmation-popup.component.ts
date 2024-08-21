import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ValidateFileService } from 'src/app/services/validateFile';
import { PopupService } from 'src/app/services/popup-service.service';
import { noWhitespaceValidator, validateAllFormFields } from 'src/app/shared/pipes/common-functions';
import { UserDetailsService } from 'src/app/services/user-details.service';

@Component({
  selector: 'app-confirmation-popup',
  templateUrl: './confirmation-popup.component.html',
  styleUrls: ['./confirmation-popup.component.scss']
})
export class ConfirmationPopupComponent implements OnInit {
  popupDetails: any;
  popupData: any;
  disputeForm: FormGroup;
  createEmailForm: FormGroup;
  allowedFileTypes = ['pdf'];
  selectedFile = [];
  fileList = [];
  isDuplicateFile: boolean = false;
  isFileSelected: boolean = false;
  isDuplicateEmail: boolean = false;
  actionWithMessage = [{
    action: 'Issued',
    message: 'Approved successfully.'
  },
  {
    action: 'Rejected',
    message: 'Rejected successfully.'
  },
  {
    action: 'Cancelled',
    message: 'Cancelled successfully.'
  },
  {
    action: 'Draft',
    message: 'Drafted successfully.'
  },
  {
    action: 'Closed',
    message: 'Closed successfully.'
  },
  {
    action: 'Final',
    message: 'Final successfully.'
  },
  {
    action: 'Pending',
    message: 'Submitted successfully.'
  },
  ]
  constructor(private dialog: MatDialogRef<ConfirmationPopupComponent>, private fb: FormBuilder, private fileValidation: ValidateFileService,
    private popupService: PopupService, private userDetails: UserDetailsService) { }

  ngOnInit(): void {
    //console.log(this.popupDetails)
    if (this.popupDetails && (this.popupDetails.action == 'dispute' || this.popupDetails.action == 'Rejected' || this.popupDetails.action == 'Cancelled' || this.popupDetails.action == 'Issued')) {
      this.disputeForm = this.fb.group({
        reason: ['', [Validators.required, noWhitespaceValidator]],
        file: ['']
      })
    }
    this.createEmailForm = this.fb.group({
      UserEmail1: [''],
      UserEmail2: [''],
      UserEmail3: [''],
      UserEmail4: [''],
      UserEmail5: [''],
    })
    if (this.popupDetails.userEmail != '' && this.popupDetails.userEmail != undefined) { this.setUserEmails(); }

  }

  closePopup(actionType) {
    this.dialog.close(actionType)
    //this.createEmailForm.reset();
  }
  setUserEmails() {
    const formValue = this.popupDetails.userEmail[0]
    this.createEmailForm.patchValue({
      UserEmail1: formValue.UserEmail1,
      UserEmail2: formValue.UserEmail2,
      UserEmail3: formValue.UserEmail3,
      UserEmail4: formValue.UserEmail4,
      UserEmail5: formValue.UserEmail5
    })
  }

  onSubmittingForm(actionType) {
    if (this.disputeForm.valid) {
      const fileInfo = {
        reason: this.disputeForm.value.reason,
        file: this.selectedFile
      }
      this.closePopup(fileInfo)
    }
  }
  removeItem(index: any) {
    this.fileList.splice(index, 1);
    this.selectedFile.splice(index, 1);
  }
  onSelectingFile(event) {
    //this.fileList = [];
    let newFile = [];
    let existingFile = [];
    this.isDuplicateFile = false;
    this.isFileSelected = true
    newFile.push(event.target.files);
    existingFile = this.popupDetails.attachmentsList
    if (this.fileList.length > 0 || existingFile.length > 0) {
      const filteredFile = newFile.filter((x, i) => this.fileList.some((y) => {
        x[i].name == y ? this.getFileUploadError('FileExceed', y) : '';

      }));
      const filteredExistingFile = existingFile.filter((x, i) => newFile.some((y, index) => {
        //console.log(x.fileName, y[index].name)
        const disputedFile = x.fileName.includes('_') ? x.fileName.split('_')[1] : x.fileName;
        disputedFile == y[index].name ? this.getFileUploadError('FileExceed', y[index].name) : '';

      }))
    }
    // Checking the file length & File Size
    for (let i = 0; i < event.target.files.length; i++) {
      if (this.fileValidation.checkIfFileIsValid(event.target.files[i], 'pdf') && !this.isDuplicateFile) {
        this.selectedFile.push(event.target.files[i]);
        this.fileList.push(event.target.files[i].name);
        //totalFileSize += event.target.files[i].size;
      }

    }
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
    else if (type == 'emailValidation') {
      this.isDuplicateEmail = true;
      popUpDetails = {
        width: '365px',
        action: 'emailValidation',
        message: fileName + " will not accept Manager/Originator Email Id's."
      }
    }


    this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
      // console.log(this.allFiles, this.selectedFile)
      if (response && type == 'FileSize') {

      }
    })
  }


  getMessage(action) {
    let message = action == '' ? 'Details Saved Successfully' : this.actionWithMessage.find(obj => obj.action == action).message
    //console.log(action)
    return message
    //return this.actionWithMessage.find(obj => obj.action == action).message
  }
  submitEmailForm(emailForm) {
    if (emailForm.valid) {
      this.isDuplicateEmail = false;
      let userEmails = [];
      for (var email in emailForm.value) {
        if (emailForm.value[email] != '' && (emailForm.value[email] == this.popupDetails.managerEmail.managerEmail || emailForm.value[email] == this.userDetails.userdata.value.emailId)) {
          userEmails.push("Stellantis " + email)
        }
      }

      userEmails.length > 0 ? this.getFileUploadError('emailValidation', userEmails) : ''
      //const validateManagerEmail = Object.values(emailForm.value).filter(x => x == this.popupDetails.managerEmail.managerEmail ? this.getFileUploadError('emailValidation') : '')

      !this.isDuplicateEmail ? this.closePopup(emailForm.value) : '';

    } else {
      validateAllFormFields(emailForm)
    }
  }

}
