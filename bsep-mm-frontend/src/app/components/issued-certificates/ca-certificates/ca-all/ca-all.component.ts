import { Component, OnInit, OnDestroy } from '@angular/core';
import { CertificateService } from 'src/app/services/certificate.service';
import { RevokeDialogService } from 'src/app/services/revoke-dialog.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-ca-all',
  templateUrl: './ca-all.component.html',
  styleUrls: ['./ca-all.component.css']
})
export class CaAllComponent implements OnInit {

  private data: any[] = [];

  private params = {
    revoked: false,
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
