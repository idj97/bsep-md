import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { faCoffee, faBell, faBook, faDesktop, faHome, faDoorOpen, faMailBulk, faChartBar } from '@fortawesome/free-solid-svg-icons';
import { KeycloakService } from 'keycloak-angular';
import { Subscription } from 'rxjs';
import { LogDialogService } from './services/log-dialog.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {

  private activated: boolean = false;
  private logSubscription: Subscription;

  title = 'BSEP | SIEM';

  //ICONS
  faCoffee = faCoffee;
  faBell = faBell;
  faBook = faBook;
  faDesktop = faDesktop;
  faHome = faHome;
  faDoorOpen = faDoorOpen;
  faMailBulk = faMailBulk;
  faChartBar = faChartBar;

  constructor(private router: Router, private titleService: Title,
              private keyCloakSvc: KeycloakService,
              private logDialogService: LogDialogService) {
    titleService.setTitle(this.title);
  }

  ngOnInit() {
    this.logSubscription = this.logDialogService.receiveLog().subscribe(
      data => {
        this.activated = data.isOpened;
      }
    );
  }

  ngOnDestroy() {
    this.logSubscription.unsubscribe();
  }

  closeDialogs() {
    this.logDialogService.sendLog({
      open: false
    });
  }


  getUrl(): string {
    return this.router.url.split('/')[1];
  }

  logout() {
    this.keyCloakSvc.logout();
  }
}
