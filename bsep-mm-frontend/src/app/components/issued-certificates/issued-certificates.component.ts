import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-issued-certificates',
  templateUrl: './issued-certificates.component.html',
  styleUrls: ['./issued-certificates.component.css']
})
export class IssuedCertificatesComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
  }


  getUrl(): string {
    return this.router.url.split('/')[2];
  }

}
