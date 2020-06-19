import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ca-certificates',
  templateUrl: './ca-certificates.component.html',
  styleUrls: ['./ca-certificates.component.css']
})
export class CACertificatesComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
  }

  getUrl(): string {
    return this.router.url.split('/')[3];
  }

}
