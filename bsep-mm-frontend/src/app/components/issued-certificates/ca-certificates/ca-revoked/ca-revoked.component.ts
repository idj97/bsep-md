import { Component, OnInit } from '@angular/core';
import { CertificateService } from 'src/app/services/certificate.service';

@Component({
  selector: 'app-ca-revoked',
  templateUrl: './ca-revoked.component.html',
  styleUrls: ['./ca-revoked.component.css']
})
export class CaRevokedComponent implements OnInit {

  private data: any[] = [];

  private params = {
    revoked: true,
    commonName: '',
    page: 0,
    pageSize: 20,
  }

  constructor(private certificateService: CertificateService) { }

  ngOnInit() {
    this.certificateService.postSearch(this.params).subscribe(
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
