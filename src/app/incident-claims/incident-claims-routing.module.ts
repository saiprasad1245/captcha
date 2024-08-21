import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IncidentClaimsComponent } from './incident-claims.component';

const routes: Routes = [
  {
    path: 'incident-claims',
    component: IncidentClaimsComponent
  },
  {
    path: '',
    redirectTo: 'incident-claims',
    pathMatch: 'full'
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IncidentClaimsRoutingModule { }
