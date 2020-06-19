import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { CertificateService } from 'src/app/services/certificate.service';

@Component({
  selector: 'app-user-all',
  templateUrl: './user-all.component.html',
  styleUrls: ['./user-all.component.css']
})
export class UserAllComponent implements OnInit {

  private data: any[] = [];
  private elStatus: any[] = [];
  private subscription: Subscription;

  private params = {
    revoked: false,
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
