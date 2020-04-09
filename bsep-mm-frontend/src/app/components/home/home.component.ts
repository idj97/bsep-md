import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { DummyService } from 'src/app/services/dummy.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  username: string;
  serverResp: string;
  numOfRequests: number = 0;

  constructor(private keyCloakSvc: KeycloakService, private ds: DummyService) { }

  ngOnInit() {
    this.username = this.keyCloakSvc.getUsername();
  }

  

}
