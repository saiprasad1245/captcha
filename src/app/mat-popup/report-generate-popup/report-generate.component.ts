import { Component, OnInit } from "@angular/core";
import { PopupService } from 'src/app/services/popup-service.service';
import { MatDialogRef } from '@angular/material/dialog';
import { SupplierService } from 'src/app/services/supplier.service';
import * as dropDownDetails from 'src/app/dropDownDetails';
import html2canvas from 'html2canvas';

import { Chart } from "chart.js";
import { ViewEncapsulation } from "@angular/compiler";
import { FormBuilder, FormGroup } from "@angular/forms";
import jsPDF from "jspdf";

@Component({
  selector: "report-generate",
  templateUrl: "./report-generate.component.html",
  styleUrls: ["./report-generate.component.scss"],
  //encapsulation: ViewEncapsulation.None
})
export class ReportGenerateComponent implements OnInit {
  public chart: Chart;
  incidentList: any;
  categoryForm: FormGroup;
  dataList: any = [];
  labelsList: any = [];
  yearList = [];
  reportHeader: any = 'Non-Conformity Selection Report';
  Categories = ['Primary Non-Conformity', 'Suprel Status', 'Technical Area'];
  labels: any = dropDownDetails.primaryNonConformityList;
  technicalArea = dropDownDetails.technicalArea;
  statusList = dropDownDetails.statusList;
  element: any;
  count: any = 0;
  colors = [
    // "rgb(102, 0, 102)",
    // "rgb(255, 128, 0)",
    // "rgb(255, 0, 0)",
    // "rgb(255, 255, 0)",
    // "rgb(102, 204, 0)",
    // "rgb(204, 102, 0)",
    // "rgb(104, 0, 59)"

    "rgb(102, 0, 102)",
    "rgb(255, 128, 0)",
    "rgb(255, 0, 0)",
    "rgb(255, 255, 0)",
    "rgb(102, 204, 0)",
    "rgb(204, 102, 0)",
    "rgb(104, 0, 59)"
  ];
  incidentStatusColors = [
    "rgb(128,128,128)", //draft
    "rgb(111, 170, 228)", //pending
    "rgb(255,255,0)",//issued
    "rgb(255, 165,0)", //dispute
    "rgb(128,128,128)",//cancelled
    "rgb(128,128,128)", //closed
    "rgb(0,128,0)", //final
  ]
  sameList: any = [];
  showdropdown: boolean = false;


  constructor(
    private popupService: PopupService,
    private supplierService: SupplierService,
    private dialogRef: MatDialogRef<ReportGenerateComponent>, private fb: FormBuilder,
  ) {
    dialogRef.disableClose = true;
    this.supplierService.getYearList().subscribe(data => {
      if (data) {
        this.yearList = data;
      }
    })
  }

  closePopup() {
    this.dialogRef.close();
  }

  exportData() {
    this.supplierService.exportData(this.reportHeader, this.incidentList)
  }

  ExtractData() {
    this.showdropdown = true;
  }

  ngOnInit() {

    this.categoryForm = this.fb.group({
      category: [this.Categories[0]],
      year: null
    })
    this.getChartData();

  }
  getChartData() {
    this.labels = [];
    let data = [];
    this.labelsList = [];
    this.dataList = [];
    let filteredData = [];
    if (this.chart) {
      this.chart.destroy();
    }
    if (this.categoryForm.value.year) {
      filteredData = this.incidentList.filter(x => {
        let yearofClaim = x.createdOn.split('-')[0];
        if (this.categoryForm.value.year != null && yearofClaim == this.categoryForm.value.year) {
          return x;
        }
      })
    } else { filteredData = this.incidentList }
    filteredData.forEach(element => {
      if (this.categoryForm.value.category == 'Suprel Status') {
        this.labelsList.push(element.suprelStatus);
        this.labels = this.statusList;
        this.reportHeader = 'Suprel Status Selection Report'
      }
      else if (this.categoryForm.value.category == 'Technical Area') {
        this.labelsList.push(element.technicalArea)
        this.labels = this.technicalArea;
        this.reportHeader = 'Technical Area Selection Report'
      }
      else {
        this.labelsList.push(element.primaryNonConformity);
        this.labels = dropDownDetails.primaryNonConformityList;
        this.reportHeader = 'Non-Conformity Selection Report'
      }

    });
    this.labels.forEach(element => {
      this.count = 0;
      this.labelsList.forEach(ele => {
        if (ele == element) {
          this.count = this.count + 1;
        }
      });
      this.dataList.push(this.count);
    });
    data.push(this.dataList)
    var ctx = document.getElementById('canvas');
    this.chart = new Chart(ctx, {
      type: "bar",
      data: {
        labels: this.labels,
        datasets: [
          {
            //label: "Total " + this.labelsList.length + " Records",
            data: this.dataList,
            backgroundColor: //[
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",


              //],
              this.categoryForm.value.category == 'Suprel Status' ? this.incidentStatusColors : this.colors,
            borderColor:// [
              //"rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              // "rgb(63, 81, 181)",
              //],
              this.categoryForm.value.category == 'Suprel Status' ? this.incidentStatusColors : this.colors,
            borderWidth: 1,
          }
        ]
      },
      options: {
        hover: {
          animationDuration: 0,
        },
        tooltips: {
          enabled: false
        },
        title: {
          display: true,
          text: "Total " + this.labelsList.length + " Records",
          fontSize: 16,
          fontStyle: "bold",
        },
        "animation": {
          "duration": 1,
          "onComplete": function () {
            var chartInstance = this.chart,
              ctx = chartInstance.ctx;
            ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, 'bold', Chart.defaults.global.defaultFontFamily);
            ctx.textAlign = 'center';
            ctx.textBaseline = 'bottom';
            data.forEach((dataSet, i) => {
              var meta = chartInstance.controller.getDatasetMeta(i);
              meta.data.forEach(function (bar, index) {
                var datas = dataSet[index];
                if (datas > 0) {
                  ctx.fillText(datas, bar._model.x, bar._model.y - 5);
                }

              });
            });
          }
        },
        legend: {
          display: false,
          // onClick: (e, legendItem, legend) => {
          //   e.stopPropagation();
          // },
          //position: 'chartArea'
        },
        responsive: true,
        scales: {
          yAxes: [
            {
              scaleLabel: {
                display: true,
                labelString: 'Selection Values',
                fontColor: "rgb(63, 81, 181)",
                fontSize: 16,
                fontStyle: "bold",

              },
              ticks: {
                beginAtZero: true,
                userCallback: function (label, index, labels) {
                  // when the floored value is the same as the value we have a whole number
                  if (Math.floor(label) === label) {
                    return label;
                  }


                },
                max: Math.max(...this.dataList) + 5,
                fontStyle: "bold",
              }
            }
          ],
          xAxes: [
            {
              scaleLabel: {
                display: true,
                labelString: this.categoryForm.value.category == 'Suprel Status' ? 'Incident Status Category' : this.categoryForm.value.category == 'Technical Area' ? 'Technical Area Category' : 'Primary Non-Conformity Category',
                fontColor: "rgb(63, 81, 181)",
                fontSize: 16,
                fontStyle: "bold",
              },
              ticks: {
                beginAtZero: true,
                fontStyle: "bold",
                //fontColor: this.colors,
                //fontColor: "rgb(63, 81, 181)", 
              }
            }
          ]
        }
      }
    });
  }
  saveAsPDF() {
    html2canvas(document.getElementById("report-chart")).then((canvas) => {
      const imgData = canvas.toDataURL("image/png");
      const pdf = new jsPDF('landscape');
      (pdf as any).autoTable({
        didDrawPage: (dataArg) => {
          pdf.text(this.reportHeader, 160, 40, { align: 'center' });
        }
      })
      pdf.addImage(imgData, "JPEG", 50, 50, 200, 150);
      pdf.save(this.reportHeader + ".pdf");
    })
  }
}
