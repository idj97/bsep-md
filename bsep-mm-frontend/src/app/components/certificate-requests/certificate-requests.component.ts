import { Component, OnInit } from '@angular/core';
import { CertificateSignRequest } from 'src/app/dtos/CertificateSignRequest.dto';
import { CertificateRequestService } from 'src/app/services/certificate-request.service';
import { ToasterService } from 'src/app/services/toaster.service';
import { CertificateAuthority } from 'src/app/dtos/CertificateAuthority.dto';
import { CertificateAuthorityService } from 'src/app/services/certificate-authority.service';

@Component({
  selector: 'app-certificate-requests',
  templateUrl: './certificate-requests.component.html',
  styleUrls: ['./certificate-requests.component.css']
})
export class CertificateRequestsComponent implements OnInit {

  certificateSignRequests: Array<CertificateSignRequest>;
  showSpinner: boolean;

  siemAgentAuthorities: Array<CertificateAuthority>;
  siemCenterAuthorities: Array<CertificateAuthority>;

  constructor(
    private certificateReqSvc: CertificateRequestService,
    private caService: CertificateAuthorityService,
    private toasterService: ToasterService
  ) {

    this.certificateSignRequests = [];
    this.showSpinner = true;
  }

  ngOnInit() {
    this.getCAByType('SIEM_AGENT_ISSUER');
    this.getCAByType('SIEM_CENTER_ISSUER');
    this.getCertificateRequests();
  }

  getCertificateRequests() {

    this.certificateReqSvc.getPendingSignedRequests().subscribe(
      data => {
        this.certificateSignRequests = data;
      },
      err => {
        this.toasterService.showErrorMessage(err);
      }
    ).add(() => {
      this.showSpinner = false;
    });

  }

  getCAByType(caType: string) {

    this.caService.getCAByType(caType).subscribe(
      data => {
        caType == 'SIEM_AGENT_ISSUER' ? this.siemAgentAuthorities = data : this.siemCenterAuthorities = data;
      },
      err => {
        this.toasterService.showErrorMessage(err);
      }
    );

  }


  removeCertificateRequest($index) {
    this.certificateSignRequests.splice($index, 1);
  }

}
