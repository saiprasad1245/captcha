import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as FileSaver from 'file-saver';
import { UrlService } from './url.service';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import 'jspdf-autotable';
import { Observable } from 'rxjs/internal/Observable';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { DatePipe } from '@angular/common';
const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
const EXCEL_EXTENSION = '.xlsx';
const PDF_EXTENSION = '.pdf';
@Injectable({
  providedIn: 'root'
})
export class SupplierService {

  head: any;
  data: any;
  selectedincidentDatas: any;

  constructor(private http: HttpClient, private urlService: UrlService, private datePipe: DatePipe) {
  }

  public getUserDetails(): Observable<any> {
    return this.http.get<any>(this.urlService.getUserDetails)
  }

  public getUserIds(): Observable<any> {
    return this.http.get<any>(this.urlService.getUserIds)
  }

  public getStateList(): Observable<any> {
    return this.http.get<any>(this.urlService.getStateList)
  }

  public getCountryList(): Observable<any> {
    return this.http.get<any>(this.urlService.getCountryList)
  }

  public getYearList(): Observable<any> {
    return this.http.get<any>(this.urlService.yearList)
  }

  public getSearchClaim(searchDetails): Observable<any> {
    return this.http.post<any>(this.urlService.getSearchClaim, searchDetails)
  }

  public createNewIncidentClaim(supplierDetails): Observable<any> {
    return this.http.post<any>(this.urlService.createNewIncidentClaim, supplierDetails)
  }

  public getSupplierCodeDetails(supplierCode): Observable<any> {
    return this.http.post<any>(this.urlService.getSupplierCodeDetails + '?supplierCode=' + supplierCode, '')
  }

  public saveAndUpdateIncidentClaim(supplierDetails): Observable<any> {
    return this.http.post<any>(this.urlService.saveAndUpdateIncidentClaim, supplierDetails)
  }

  public deleteIncidentClaim(suprelNo): Observable<any> {
    return this.http.post<any>(this.urlService.deleteIncidentClaim + '?suprelNo=' + suprelNo, '')
  }

  public changeIncidentClaimStatus(statusChangeDetails): Observable<any> {
    return this.http.post<any>(this.urlService.changeIncidentStatus, statusChangeDetails)
  }
  public downLoadCertificate(suprelIncidentNumber, supplierCode, fileName) {
    return this.http.get(this.urlService.downloadCertificate + '?suprelCode=' + suprelIncidentNumber + '&supplierCode=' + supplierCode + '&fileName=' + fileName, { responseType: 'blob' });
  }

  public getAttachmentsList(suprelNo): Observable<any> {
    return this.http.post<any>(this.urlService.getAttachmentsList + '?suprelNo=' + suprelNo, '')
  }

  public getCertificatesWaitingForAcceptance(managerName): Observable<any> {
    return this.http.get<any>(this.urlService.getCertificatesWaitingForAcceptanceUrl + '?managerName=' + managerName)
  }

  public getManagerNamesList(): Observable<any> {
    return this.http.get<any>(this.urlService.getManagerNamesList)
  }

  public getAuditLogList(suprelNo): Observable<any> {
    return this.http.post<any>(this.urlService.getAuditLogList + '?suprelNo=' + suprelNo, '')
  }

  public saveBulkUploadData(file): Observable<any> {
    return this.http.post<any>(this.urlService.bulkUploadData, file)
  }

  public saveManagerBulkUpload(file): Observable<any> {
    return this.http.post<any>(this.urlService.managerBulkUpload, file)
  }

  public getManagerViewData(tId): Observable<any> {
    return this.http.get<any>(this.urlService.managerViewData + '?tId=' + tId)
  }

  public exportData(event: any, incidentList: any) {
    let incidentListDat = this.selectedincidentDatas ? [...this.selectedincidentDatas] : [...incidentList]
    let reqData = [];
    incidentListDat.forEach(incidentData => {
      const data = {
        'Supplier Code': incidentData.supplierCode,
        'Supplier Name': incidentData.supplierName,
        'Address': incidentData.supplierAddress,
        'City': incidentData.city,
        'State': incidentData.state,
        'Country': incidentData.country,
        'Suprel #': incidentData.suprelNo,
        'Technical Area': incidentData.technicalArea,
        'Originator Name': incidentData.originatorName,
        'Manager Name': incidentData.managerName,
        'Primary Non-Conformity': incidentData.primaryNonConformity,
        'Secondary Non-Conformity': incidentData.secondaryNonConformity,
        'Incident Status': incidentData.suprelStatus,
        'Issued Date': this.datePipe.transform(incidentData.issuedDate, 'MM-dd-yyyy')
      }
      reqData.push(data)
    })

    if (event == 'xlsx') {
      this.exportAsExcelFile(reqData, 'Incident Claim List')
    }
    else if (event == 'pdf') {
      this.exportAsPdfFile(reqData, 'Incident Claim List')
    }
    else {
      this.exportAsExcelFile(reqData, event)
    }

  }

  public exportAsPdfFile(json: any[], pdfFileName: string): void {

    this.head = [
      ['Supplier Code', 'Supplier Name', 'Address', 'City', 'State', 'Country', 'Suprel #', 'Technical Area', 'Originator Name', 'Manager Name', 'Primary Non-Conformity', 'Secondary Non-Conformity', 'Incident Status', 'Issued Date']
    ]

    this.data = json;
    var prepare = [];
    this.data.forEach(e => {
      var tempObj = [];
      tempObj.push(e['Supplier Code']);
      tempObj.push(e['Supplier Name']);
      tempObj.push(e['Address']);
      tempObj.push(e['City']);
      tempObj.push(e['State']);
      tempObj.push(e['Country']);
      tempObj.push(e['Suprel #']);
      tempObj.push(e['Technical Area']);
      tempObj.push(e['Originator Name'])
      tempObj.push(e['Manager Name'])
      tempObj.push(e['Primary Non-Conformity']);
      tempObj.push(e['Secondary Non-Conformity']);
      tempObj.push(e['Incident Status']);
      tempObj.push(e['Issued Date']);
      prepare.push(tempObj);
    });
    var doc = new jsPDF('landscape');
    // var header = function (data) {
    //   doc.text( 'Supplier Data', 150, 20, {align: 'center'} );
    // };
    (doc as any).autoTable({
      head: this.head,
      body: prepare,
      theme: 'plain',
      headStyles: {
        halign: "left",
        valign: "middle",
        lineWidth: 0.25,
        lineColor: 200
      },
      bodyStyles: {
        halign: "center",
        lineWidth: 0.25,
        lineColor: 200
      },
      margin: {
        top: 30
      },
      didDrawPage: (dataArg) => {
        doc.text('Supplier Data', 150, 20, { align: 'center' });
      }
      // beforePageContent: header
      // beforePageContent: header

    })
    doc.save(pdfFileName + '.pdf');
  }

  public exportAsExcelFile(json: any[], excelFileName: string): void {
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json);
    worksheet['A1'].a = {
      fill: {
        type: 'pattern',
        pattern: "solid",
        bgColor: { rgb: "FF1c4587" }
      },
      font: {
        name: 'Times New Roman',
        sz: 16,
        color: { rgb: "#FF000000" },
        bold: true,
        italic: false,
        underline: false
      },
      border: {
        top: { style: "thin", color: { auto: 1 } },
        right: { style: "thin", color: { auto: 1 } },
        bottom: { style: "thin", color: { auto: 1 } },
        left: { style: "thin", color: { auto: 1 } },
      }
    }
    const workbook: XLSX.WorkBook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    this.saveAsExcelFile(excelBuffer, excelFileName);
  }

  private saveAsExcelFile(buffer: any, fileName: string): void {
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE
    });
    FileSaver.saveAs(data, fileName + '_export_' + new Date().getTime() + EXCEL_EXTENSION);
  }
}
