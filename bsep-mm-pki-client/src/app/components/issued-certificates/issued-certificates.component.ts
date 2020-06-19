import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { RevokeDialogService } from 'src/app/services/revoke-dialog.service';
import { Subscription } from 'rxjs';

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
