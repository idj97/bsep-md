import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { initializer } from './keycloak/app-init';
import { HomeComponent } from './components/home/home.component';
import { HttpClientModule } from '@angular/common/http';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { LogEventsComponent } from './components/log-events/log-events.component';
import { AlarmEventsComponent } from './components/alarm-events/alarm-events.component';
import { ReportsComponent } from './components/reports/reports.component';
import { FormsModule } from '@angular/forms';
import { LogDialogComponent } from './components/log-dialog/log-dialog.component';
import { ChartsModule } from 'ng2-charts';
import { RuleModule } from './components/rule/rule.module';
import { AlarmDialogComponent } from './components/alarm-dialog/alarm-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LogEventsComponent,
    AlarmEventsComponent,
    ReportsComponent,
    LogDialogComponent,
    AlarmDialogComponent,
    ReportsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FontAwesomeModule,
    KeycloakAngularModule,
    FormsModule,
    ChartsModule,
    RuleModule
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
