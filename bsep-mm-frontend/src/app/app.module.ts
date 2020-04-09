import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KeycloakService, KeycloakAngularModule } from 'keycloak-angular';
import { initializer } from './keycloak/app-init';
import { HomeComponent } from './components/home/home.component';
import { HttpClientModule } from '@angular/common/http';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CertificateRequestsComponent } from './components/certificate-requests/certificate-requests.component';
import { IssuedCertificatesComponent } from './components/issued-certificates/issued-certificates.component';
import { NewCertificateComponent } from './components/new-certificate/new-certificate.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'; 

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    CertificateRequestsComponent,
    IssuedCertificatesComponent,
    NewCertificateComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    KeycloakAngularModule,
    FontAwesomeModule,
    HttpClientModule,
    NgbModule,
  ],
  providers: [
    {
    provide: APP_INITIALIZER,
    useFactory: initializer,
    multi: true,
    deps: [KeycloakService] 
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
