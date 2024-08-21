import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCommonModule, MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { HttpClientModule } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { CreateIncidentClaimComponent } from './create-incident-claim/create-incident-claim.component';
import { MatChipsModule } from '@angular/material/chips';
import { EditIncidentClaimComponent } from './edit-incident-claim/edit-incident-claim.component';
import { EditIncidentForSupplierRoleComponent } from './edit-incident-for-supplier-role/edit-incident-for-supplier-role.component';
import { ReportGenerateComponent } from './report-generate-popup/report-generate.component';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatMenuModule} from '@angular/material/menu';
import { SearchPipeModule } from '../shared/pipes/searchPipe';
import { ConfirmationPopupComponent } from './confirmation-popup/confirmation-popup.component';
import { LoadingComponent } from '../loading';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuditLogPopupComponent } from './audit-log-popup/audit-log-popup.component';
import { ScrollingModule } from '@angular/cdk/scrolling';


@NgModule({
  declarations: [CreateIncidentClaimComponent, EditIncidentClaimComponent,LoadingComponent,ConfirmationPopupComponent, EditIncidentForSupplierRoleComponent,ReportGenerateComponent, AuditLogPopupComponent],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    FlexLayoutModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatChipsModule,
    CommonModule,
    HttpClientModule,
    SearchPipeModule,
    MatIconModule,
    MatTooltipModule,
    NgxMatSelectSearchModule,
    MatIconModule,
    MatButtonToggleModule,
    MatMenuModule,
    ScrollingModule
  ]
})
export class MatPopupModule { }
