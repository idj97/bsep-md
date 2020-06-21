import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { faCoffee, faFile, faPlus, faKey, faHome, faDoorOpen, faMailBulk } from '@fortawesome/free-solid-svg-icons';
import { KeycloakService } from 'keycloak-angular';
import { RevokeDialogService } from './services/revoke-dialog.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private activated: boolean = false;
  private subscription: Subscription;

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
              private keyCloakSvc: KeycloakService,
              private revokeDialogService: RevokeDialogService) {
    titleService.setTitle(this.title);
  }

  ngOnInit() {
    this.subscription = this.revokeDialogService.getData().subscribe(
      data => {
        this.activated = data.open;
      }
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  closeDialog() {
    this.revokeDialogService.sendData({
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
