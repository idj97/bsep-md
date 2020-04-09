import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { NewCertificateComponent } from './components/new-certificate/new-certificate.component';
import { IssuedCertificatesComponent } from './components/issued-certificates/issued-certificates.component';
import { CertificateRequestsComponent } from './components/certificate-requests/certificate-requests.component';


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
    component: IssuedCertificatesComponent
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
