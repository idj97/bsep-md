import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KeycloakService, KeycloakAngularModule } from 'keycloak-angular';
import { initializer } from './keycloak/app-init';
import { HomeComponent } from './components/home/home.component';
import { HttpClientModule } from '@angular/common/http';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { IssuedCertificatesComponent } from './components/issued-certificates/issued-certificates.component';
import { NewCertificateComponent } from './components/new-certificate/new-certificate.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { UserCertificatesComponent } from './components/issued-certificates/user-certificates/user-certificates.component';
import { CACertificatesComponent } from './components/issued-certificates/ca-certificates/ca-certificates.component';
import { CaAllComponent } from './components/issued-certificates/ca-certificates/ca-all/ca-all.component';
import { CaRevokedComponent } from './components/issued-certificates/ca-certificates/ca-revoked/ca-revoked.component';
import { UserRevokedComponent } from './components/issued-certificates/user-certificates/user-revoked/user-revoked.component';
import { UserAllComponent } from './components/issued-certificates/user-certificates/user-all/user-all.component';
import { RevokeDialogComponent } from './components/revoke-dialog/revoke-dialog.component';
import { FormsModule } from '@angular/forms';
import { DlDateTimeDateModule, DlDateTimePickerModule } from 'angular-bootstrap-datetimepicker';
import { ViewAllCertificatesComponent } from './components/view-all-certificates/view-all-certificates.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { KeySizeDirective } from './validators/key-size.directive';
import { ValidForDirective } from './validators/valid-for.directive';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    IssuedCertificatesComponent,
    NewCertificateComponent,
    UserCertificatesComponent,
    CACertificatesComponent,
    CaAllComponent,
    CaRevokedComponent,
    UserRevokedComponent,
    UserAllComponent,
    RevokeDialogComponent,
    ViewAllCertificatesComponent,
    KeySizeDirective,
    ValidForDirective,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    KeycloakAngularModule,
    FontAwesomeModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    DlDateTimeDateModule,
    DlDateTimePickerModule,
    NgSelectModule,
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
