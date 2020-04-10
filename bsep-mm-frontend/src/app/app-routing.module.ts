import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { NewCertificateComponent } from './components/new-certificate/new-certificate.component';
import { IssuedCertificatesComponent } from './components/issued-certificates/issued-certificates.component';
import { CertificateRequestsComponent } from './components/certificate-requests/certificate-requests.component';
import { UserCertificatesComponent } from './components/issued-certificates/user-certificates/user-certificates.component';
import { CACertificatesComponent } from './components/issued-certificates/ca-certificates/ca-certificates.component';
import { UserAllComponent } from './components/issued-certificates/user-certificates/user-all/user-all.component';
import { UserRevokedComponent } from './components/issued-certificates/user-certificates/user-revoked/user-revoked.component';
import { CaAllComponent } from './components/issued-certificates/ca-certificates/ca-all/ca-all.component';
import { CaRevokedComponent } from './components/issued-certificates/ca-certificates/ca-revoked/ca-revoked.component';


const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'new-certificate',
    component: NewCertificateComponent
  },
  {
    path: 'issued-certificates',
    component: IssuedCertificatesComponent,
    children:[
      {
        path : 'user-certificates',
        component: UserCertificatesComponent,
        children:[
          {
            path : 'all',
            component: UserAllComponent
          },
          {
            path : 'revoked',
            component: UserRevokedComponent
          },
        ]
      },
      {
        path : 'CA-certificates',
        component: CACertificatesComponent,
        children:[
          {
            path : 'all',
            component: CaAllComponent
          },
          {
            path : 'revoked',
            component: CaRevokedComponent
          },
        ]
      },
    ]
  },
  {
    path: 'certificate-requests',
    component: CertificateRequestsComponent
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
