import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { TestService } from 'src/app/services/test.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  unsecuredResponse: string;
  securedResponse: string;

  constructor(
    private keyCloakSvc: KeycloakService,
    private testService: TestService
    ) { }

  ngOnInit() {
  }

  logout(): void {
    this.keyCloakSvc.logout();
  }

  securedRequest() {
    this.testService.securedTest().subscribe(
      data => {
        console.log(data);
        this.securedResponse = data;
      },
      err => {
        console.log(err.error);
      }
    );
  }

  unsecuredRequest() {
    this.testService.unsecuredTest().subscribe(
      data => {
        console.log(data);
        this.unsecuredResponse = data;
      },
      err => {
        console.log(err.error);
      }
    );
  }

}
