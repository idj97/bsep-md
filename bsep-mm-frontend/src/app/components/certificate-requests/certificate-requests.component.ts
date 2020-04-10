import { Component, OnInit } from '@angular/core';
import { CertificateSignRequest } from 'src/app/dtos/CertificateSignRequest.dto';
import { CertificateRequestService } from 'src/app/services/certificate-request.service';

@Component({
  selector: 'app-certificate-requests',
  templateUrl: './certificate-requests.component.html',
  styleUrls: ['./certificate-requests.component.css']
})
export class CertificateRequestsComponent implements OnInit {

  certificateSignRequests: Array<CertificateSignRequest>;
  showSpinner: boolean;

  constructor(private certificateReqSvc: CertificateRequestService) {
    this.certificateSignRequests = [];
    this.showSpinner = true;
  }

  ngOnInit() {
    this.certificateReqSvc.getPendingSignedRequests().subscribe(
      data => {
        this.certificateSignRequests = data;
      },
      err => {
        console.log(err.error);
      }
    ).add(() => {
      this.showSpinner = false;
    });
  }

  getCertificateRequests() {

  }

  approveCertificateRequest(id: number, index: number) {

    if(!window.confirm('Are you sure you want to approve this request?')) {
      return;
    }

    this.certificateReqSvc.approveCertificateRequest(id).subscribe(
      data => {
        this.removeCertificateRequest(index);
      },
      err => {
        console.log(err.error);
      }
    );
  }

  declineCertificateRequest(id: number, index: number) {

    if(!window.confirm('Are you sure you want to decline this request?')) {
      return;
    }

    this.certificateReqSvc.declineCertificateRequest(id).subscribe(
      data => {
        this.removeCertificateRequest(index);
      },
      err => {
        console.log(err.error);
      }
    );
  }

  removeCertificateRequest(index: number) {
    this.certificateSignRequests.splice(index, 1);
  }

}
