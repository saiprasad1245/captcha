import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncidentClaimsRoutingModule } from './incident-claims-routing.module';
import { IncidentClaimsComponent } from './incident-claims.component';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import{MatCardModule} from '@angular/material/card';
import {MatFormFieldModule } from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import { FlexLayoutModule } from '@angular/flex-layout';
import {MatSelectModule} from '@angular/material/select';
import {MatDatepickerModule } from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatButtonModule} from '@angular/material/button';
import {MatDividerModule} from '@angular/material/divider';
import { MatRadioModule } from '@angular/material/radio';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import {MatSortModule} from '@angular/material/sort';
import { HttpClientModule } from '@angular/common/http';
import { MatTooltipModule } from '@angular/material/tooltip';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { TableVirtualScrollModule } from 'ng-table-virtual-scroll';
import {ScrollingModule} from '@angular/cdk/scrolling';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatMenuModule} from '@angular/material/menu';
import { SearchPipeModule } from '../shared/pipes/searchPipe';
import { MatChipsModule } from '@angular/material/chips';

@NgModule({
  declarations: [    IncidentClaimsComponent
  ],
  imports: [
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    TableVirtualScrollModule,
    ScrollingModule,
    MatInputModule,
    FlexLayoutModule,
    MatSelectModule,
    SearchPipeModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatDividerModule,
    MatTableModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatIconModule,
    MatRadioModule,
    HttpClientModule,
    MatSortModule,
    MatChipsModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    NgxMatSelectSearchModule,
    CommonModule,
    IncidentClaimsRoutingModule,
    MatButtonToggleModule,
    MatMenuModule
  ]
})
export class IncidentClaimsModule { }
