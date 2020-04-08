import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-certificates',
  templateUrl: './user-certificates.component.html',
  styleUrls: ['./user-certificates.component.css']
})
export class UserCertificatesComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
  }

  getUrl(): string {
    return this.router.url.split('/')[3];
  }

}
