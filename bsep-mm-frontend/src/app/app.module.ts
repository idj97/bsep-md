import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KeycloakService, KeycloakAngularModule } from 'keycloak-angular';
import { initializer } from './keycloak/app-init';
import { HomeComponent } from './components/home/home.component';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    KeycloakAngularModule,
    HttpClientModule
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
