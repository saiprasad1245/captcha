import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FullLayoutComponent } from './layout/full-layout/full-layout.component';

const routes: Routes = [


  //this will lead to url admin
  {
    path: '',
    component: FullLayoutComponent,
    // canActivate: [AuthGuard],
    // canActivateChild: [AuthGuard],
    children: [
      {
        path: '',
        loadChildren: () => import('./incident-claims/incident-claims.module').then(m => m.IncidentClaimsModule)
      },
    ]

  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
