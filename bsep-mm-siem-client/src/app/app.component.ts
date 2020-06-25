import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { faCoffee, faBell, faBook, faDesktop, faHome, faDoorOpen, faMailBulk, faChartBar } from '@fortawesome/free-solid-svg-icons';
import { KeycloakService } from 'keycloak-angular';
import { Subscription } from 'rxjs';
import { LogDialogService } from './services/log-dialog.service';
import { AlarmDialogService } from './services/alarm-dialog.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {

  private activated: boolean = false;
  private alarmDialogActivated: boolean = false;
  private logSubscription: Subscription;
  private alarmSubscription: Subscription;

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
              private logDialogService: LogDialogService,
              private alarmDialogService: AlarmDialogService) {
    titleService.setTitle(this.title);
  }

  ngOnInit() {
    this.logSubscription = this.logDialogService.receiveLog().subscribe(
      data => {
        this.activated = data.isOpened;
      }
    );

    this.alarmSubscription = this.alarmDialogService.receiveAlarm().subscribe(
      data => {
        this.alarmDialogActivated = data.isOpened;
      }
    );
  }

  ngOnDestroy() {
    this.logSubscription.unsubscribe();
    this.alarmSubscription.unsubscribe();
  }

  closeAlarmDialogs() {
    this.alarmDialogService.sendAlarm({
      open: false
    });
  }

  closeLogDialogs(): void {
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

  get getUserRoles(): Array<string> {
    return this.keyCloakSvc.getUserRoles();
  }
}
