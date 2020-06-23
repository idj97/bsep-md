import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { faCoffee, faFile, faPlus, faKey, faHome, faDoorOpen, faMailBulk, faChartBar } from '@fortawesome/free-solid-svg-icons';
import { KeycloakService } from 'keycloak-angular';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {

  private activated: boolean = false;
  private subscription: Subscription;

  title = 'BSEP | SIEM';

  //ICONS
  faCoffee = faCoffee;
  faFile = faFile;
  faPlus = faPlus;
  faKey = faKey;
  faHome = faHome;
  faDoorOpen = faDoorOpen;
  faMailBulk = faMailBulk;
  faChartBar = faChartBar;

  constructor(private router: Router, private titleService: Title,
              private keyCloakSvc: KeycloakService) {
    titleService.setTitle(this.title);
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    
  }


  getUrl(): string {
    return this.router.url.split('/')[1];
  }

  logout() {
    this.keyCloakSvc.logout();
  }
}
