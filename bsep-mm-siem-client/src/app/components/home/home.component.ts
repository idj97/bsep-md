import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private keyCloakSvc: KeycloakService) { }

  ngOnInit() {
  }

  logout(): void {
    this.keyCloakSvc.logout();
  }

}
