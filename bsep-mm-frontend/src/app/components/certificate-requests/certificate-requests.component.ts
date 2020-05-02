import { Component, OnInit } from '@angular/core';
import { CertificateSignRequest } from 'src/app/dtos/CertificateSignRequest.dto';
import { CertificateRequestService } from 'src/app/services/certificate-request.service';
import { ToasterService } from 'src/app/services/toaster.service';

@Component({
  selector: 'app-certificate-requests',
  templateUrl: './certificate-requests.component.html',
  styleUrls: ['./certificate-requests.component.css']
})
export class CertificateRequestsComponent implements OnInit {

  certificateSignRequests: Array<CertificateSignRequest>;
  showSpinner: boolean;

  constructor(
    private certificateReqSvc: CertificateRequestService,
    private toasterService: ToasterService) {
    this.certificateSignRequests = [];
    this.showSpinner = true;
  }

  ngOnInit() {
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

  approveCertificateRequest(id: number, index: number) {

    if(!window.confirm('Are you sure you want to approve this request?')) {
      return;
    }

    this.certificateReqSvc.approveCertificateRequest(id).subscribe(
      data => {
        this.removeCertificateRequest(index);
        this.toasterService.showMessage('Approved', 'Certificate successfully approved');
      },
      err => {
        this.toasterService.showErrorMessage(err);
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
        this.toasterService.showErrorMessage(err);
        this.toasterService.showMessage('Removed', 'Certificate successfully removed');
      }
    );
  }

  removeCertificateRequest(index: number) {
    this.certificateSignRequests.splice(index, 1);
  }

}
