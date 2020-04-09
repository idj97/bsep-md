import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { faCoffee, faFile, faPlus, faKey, faHome, faDoorOpen, faMailBulk } from '@fortawesome/free-solid-svg-icons';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'BSEP | PKI';

  //ICONS
  faCoffee = faCoffee;
  faFile = faFile;
  faPlus = faPlus;
  faKey = faKey;
  faHome = faHome;
  faDoorOpen = faDoorOpen;
  faMailBulk = faMailBulk;

  constructor(private router: Router, private titleService: Title,
              private keyCloakSvc: KeycloakService) {
    titleService.setTitle(this.title);
  }

  getUrl(): string {
    return this.router.url.split('/')[1];
  }

  logout() {
    this.keyCloakSvc.logout();
  }

}
