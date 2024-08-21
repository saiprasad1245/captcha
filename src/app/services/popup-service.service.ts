import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { CreateIncidentClaimComponent } from '../mat-popup/create-incident-claim/create-incident-claim.component';
import { EditIncidentClaimComponent } from '../mat-popup/edit-incident-claim/edit-incident-claim.component';
import { EditIncidentForSupplierRoleComponent } from '../mat-popup/edit-incident-for-supplier-role/edit-incident-for-supplier-role.component';
import {ReportGenerateComponent} from '../mat-popup/report-generate-popup/report-generate.component';
import { ConfirmationPopupComponent } from '../mat-popup/confirmation-popup/confirmation-popup.component';
import { AuditLogPopupComponent } from '../mat-popup/audit-log-popup/audit-log-popup.component';

@Injectable({
  providedIn: 'root'
})
export class PopupService {

  constructor(private matDialog: MatDialog) { }

  openIncidentDataModalForSupplierRole(popupData) {
    let dialogRef: MatDialogRef<EditIncidentForSupplierRoleComponent>;
    dialogRef = this.matDialog.open(EditIncidentForSupplierRoleComponent, { width: '750px',disableClose: true });
    dialogRef.componentInstance.popupData = popupData;
    return dialogRef.afterClosed()
  }

  openCreateIncidentModal(popupData) {
    let dialogRef: MatDialogRef<CreateIncidentClaimComponent>;
    dialogRef = this.matDialog.open(CreateIncidentClaimComponent, { width: '750px',disableClose: true });
    dialogRef.componentInstance.popupData = popupData;
    return dialogRef.afterClosed()
  }

  openEditIncidentModal(popupData) {
    let dialogRef: MatDialogRef<EditIncidentClaimComponent>;
    dialogRef = this.matDialog.open(EditIncidentClaimComponent, { width: '750px',disableClose: true });
    dialogRef.componentInstance.popupData = popupData;
    return dialogRef.afterClosed()
  }
  openReportGenerateModal(incidentList){
    let dialogRef: MatDialogRef<ReportGenerateComponent>;
    dialogRef = this.matDialog.open(ReportGenerateComponent, {width: '700px' ,disableClose: false});
    dialogRef.componentInstance.incidentList = incidentList;
    return dialogRef.afterClosed()
  }

  openConfirmationPopupModal(popupDetails) {
    // const width = popupDetails.action && popupDetails.action == 'success' || popupDetails.action == 'error' ? '400px' : '500px';
    const width = popupDetails.width  ? popupDetails.width : '350px';
    // const width =  '350px'
    let dialogRef: MatDialogRef<ConfirmationPopupComponent>;
    dialogRef = this.matDialog.open(ConfirmationPopupComponent, { width: width ,disableClose: true});
    dialogRef.componentInstance.popupDetails = popupDetails;
    return dialogRef.afterClosed()
  }

  openAuditLogPopupModel(popUpDetails){
    let dialogRef: MatDialogRef<AuditLogPopupComponent>;
    dialogRef = this.matDialog.open(AuditLogPopupComponent, {width: '700px' ,disableClose: true});
    dialogRef.componentInstance.popupDetails = popUpDetails;
    return dialogRef.afterClosed()
  }

}
