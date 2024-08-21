import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { PopupService } from 'src/app/services/popup-service.service';
import { SupplierService } from 'src/app/services/supplier.service';

@Component({
  selector: 'app-audit-log-popop',
  templateUrl: './audit-log-popup.component.html',
  styleUrls: ['./audit-log-popup.component.scss']
})
export class AuditLogPopupComponent implements OnInit {
  popupDetails: any;
  isShowError: boolean = false;
  isLoading: boolean = true;
  auditLogList = [];
  constructor(private supplierService: SupplierService, private dialog: MatDialogRef<AuditLogPopupComponent>,) { }

  ngOnInit(): void {
    //console.log(this.popupDetails)
    this.supplierService.getAuditLogList(this.popupDetails.suprelNo).subscribe(data => {
      //console.log(data)
      let filteredData = [];
      let activityLogList = [];
      this.isLoading = false;
      this.isShowError = false;
      if (data.length > 0) {
        data.forEach((list, i) => {
          const currentLog = {
            //userTid: list.userTid,
            primaryNonConformity: list.primaryNonConformity != null ? list.primaryNonConformity : '',
            secondaryNonConformity: list.secondaryNonConformity != null ? list.secondaryNonConformity : '',
            amount: list.amount != null ? list.amount : '',
            currency: list.currency != null ? list.currency : '',
            technicalArea: list.technicalArea != null ? list.technicalArea : '',
            details: list.details != null ? list.details : '',
            originatorName: list.originatorName != null ? list.originatorName : '',
            managerName: list.managerName != null ? list.managerName : '',
            managerComments: list.managerComments != null ? list.managerComments : '',
            supplierComments: list.supplierComments != null ? list.supplierComments : '',
            supplierManufCode: list.supplierManufCode != null ? list.supplierManufCode : '',
            suprelNo: list.suprelNo != null ? list.suprelNo : '',
            suprelStatus: list.suprelStatus != null ? list.suprelStatus : '',
            supplierEmail1: list.supplierEmail1 != null ? list.supplierEmail1 : '',
            supplierEmail2: list.supplierEmail2 != null ? list.supplierEmail2 : '',
            supplierEmail3: list.supplierEmail3 != null ? list.supplierEmail3 : '',
            supplierEmail4: list.supplierEmail4 != null ? list.supplierEmail4 : '',
            supplierEmail5: list.supplierEmail5 != null ? list.supplierEmail5 : '',
            attachmentNames: list.attachmentNames != null ? list.attachmentNames : '',
            userEmail1: list.userEmail1 != null ? list.userEmail1 : '',
            userEmail2: list.userEmail2 != null ? list.userEmail2 : '',
            userEmail3: list.userEmail3 != null ? list.userEmail3 : '',
            userEmail4: list.userEmail4 != null ? list.userEmail4 : '',
            userEmail5: list.userEmail5 != null ? list.userEmail5 : '',

          }
          activityLogList.push(currentLog)
          //console.log(currentLog, activityLogList[i - 1])
          if (i != 0) {
            //const filteredList = activityLogList[i-1].filter(x => JSON.stringify(currentLog) !== JSON.stringify(x))
            if (JSON.stringify(currentLog) !== JSON.stringify(activityLogList[i - 1])) {
              if (list.suprelStatus == 'Disputed') {
                let attachmentFilteredName = activityLogList[i - 1].attachmentNames.split(', ')
                let currentLogAttachmentNAme = list.attachmentNames.split(', ');
                let uniqueFile = currentLogAttachmentNAme.filter((x) => attachmentFilteredName.indexOf(x) === -1);
                list['fileName'] = uniqueFile;
              }
              filteredData.push(list);

            }
          } else if (i == 0) {
            filteredData.push(list)
          }
          this.auditLogList = filteredData;
        })
        //console.log(this.auditLogList)
      }
    }, error => {
      console.log(error)
      this.isLoading = false;
      this.isShowError = true;
    }
    )
  }
  closePopup() {
    this.dialog.close()
  }
}
