import { Injectable } from '@angular/core';
import { PopupService } from 'src/app/services/popup-service.service';

@Injectable({
    providedIn: 'root'
})
export class ValidateFileService {
    constructor(private popupService: PopupService) { }

    public checkIfFileIsValid(file, allowedFileTypes) {
        const fileName = file.name.split('.');
        if (!allowedFileTypes.includes(fileName[fileName.length - 1].toLowerCase())) {
            let fileTypeText = "";
            allowedFileTypes != 'pdf' ?
                allowedFileTypes.forEach((obj, i) => {
                    fileTypeText = fileTypeText + obj + (allowedFileTypes[i + 1] ? ', ' : '.')
                }) : fileTypeText = allowedFileTypes + '.';
            const popUpDetails = {
                width: '413px',
                action: 'fileUploadInfo',
                message: file.name + ' cannot be uploaded.Accepted file type extensions are ' + fileTypeText
            }
            this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
            })

            return false;
        } else if (allowedFileTypes == 'pdf' && !(file && file.size < 2097152)) {
            const popUpDetails = {
                width: '290px',
                action: 'fileUploadInfo',
                message: file.name + ' exceeds max file size limit 2MB.',
            }
            this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
            })

            return false;
        } else if (!(/^[A-Za-z0-9 ]+$/.test(fileName[0]) && fileName.length == 2)) {

            const popUpDetails = {
                width: '374px',
                action: 'fileUploadInfo',
                message: file.name + ' cannot be uploaded,special characters not allowed in file name.'
            }
            this.popupService.openConfirmationPopupModal(popUpDetails).subscribe(response => {
            })

            return false;
        } else {

            return true;
        }

    }


}
