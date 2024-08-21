import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UrlService {
  initialUrl =  environment.apiKey;


  constructor() { }

  public getUserDetails = this.initialUrl + 'getUserDetails'

  public createNewIncidentClaim = this.initialUrl + 'createNewIncidentClaim';

  public getSearchClaim = this.initialUrl + 'getSearchClaim';

  public saveAndUpdateIncidentClaim = this.initialUrl + 'saveAndUpdateIncidentClaim';

  public deleteIncidentClaim = this.initialUrl + 'deleteIncidentClaim';

  public getUserIds = this.initialUrl + 'getUserIds';

  public changeIncidentStatus = this.initialUrl + 'incidentStatusChange';

  public downloadCertificate = this.initialUrl + 'downloadCertificate';

  public getSupplierCodeDetails = this.initialUrl + 'supplierCodeDetails';

  public getCertificatesWaitingForAcceptanceUrl = this.initialUrl + 'pendingCertification';
  
  public getAttachmentsList = this.initialUrl + 'getAttachmentsById';

  public getManagerNamesList = this.initialUrl + 'managerNamesList';

  public getAuditLogList = this.initialUrl + 'getAuditLogDetailsForSuprelNum';

  public getStateList = this.initialUrl + 'getStateList';

  public getCountryList = this.initialUrl + 'getCountryList';

  public bulkUploadData = this.initialUrl + 'saveBulkUploadData';

  public yearList = this.initialUrl + 'getYearsList';

  public managerBulkUpload = this.initialUrl + 'saveIncidentClaimsData';

  public managerViewData = this.initialUrl + 'claims-incident';
}
