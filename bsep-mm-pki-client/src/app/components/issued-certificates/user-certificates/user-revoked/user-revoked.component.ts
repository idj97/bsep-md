import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { CertificateService } from 'src/app/services/certificate.service';

@Component({
  selector: 'app-user-revoked',
  templateUrl: './user-revoked.component.html',
  styleUrls: ['./user-revoked.component.css']
})
export class UserRevokedComponent implements OnInit {

  private data: any[] = [];
  private elStatus: any[] = [];
  private subscription: Subscription;

  private params = {
    revoked: true,
    isCa: false,
    page: 0,
    pageSize: 20,
  }

  constructor(private certificateService: CertificateService) { }

  ngOnInit() {
    this.certificateService.postSimpleSearchCertificate(this.params).subscribe(
      data => {
        console.log(data);
        this.data = data.items;
      },

      error => {
        console.log(error);
      }
    );
  }

}
