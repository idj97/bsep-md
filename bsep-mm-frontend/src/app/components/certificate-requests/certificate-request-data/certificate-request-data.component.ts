import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CertificateSignRequest } from 'src/app/dtos/CertificateSignRequest.dto';
import { CertificateAuthority } from 'src/app/dtos/CertificateAuthority.dto';
import { ToasterService } from 'src/app/services/toaster.service';
import { CertificateRequestService } from 'src/app/services/certificate-request.service';

@Component({
  selector: 'app-certificate-request-data',
  templateUrl: './certificate-request-data.component.html',
  styleUrls: ['./certificate-request-data.component.css']
})
export class CertificateRequestDataComponent implements OnInit {

  @Input() certificateRequest: CertificateSignRequest;
  @Input() certificateRequestIndex: number;

  selectedCAType: string;
  issuerId: number;

  @Input() siemAgentCAs: Array<CertificateAuthority>;
  @Input() siemCenterCAs: Array<CertificateAuthority>;

  @Output() removeEvent: EventEmitter<number>;

  constructor(
    private certificateReqSvc: CertificateRequestService,
    private toasterService: ToasterService
  ) {
    this.siemAgentCAs = [];
    this.siemCenterCAs = [];
    this.removeEvent = new EventEmitter<number>();
  }

  ngOnInit() {
  }

  caTypeChanged() {
    this.issuerId = null;
  }
 
  approveCertificateRequest() {

    if(!window.confirm('Are you sure you want to approve this request?')) {
      return;
    }

    if(this.issuerId == null) {
      this.toasterService.showMessage('Empty field', 'Select an issuer to approve the request.');
      return;
    }

    this.certificateReqSvc.approveCertificateRequest(this.certificateRequest.id, this.issuerId).subscribe(
      data => {
        this.removeEvent.emit(this.certificateRequestIndex);
        this.toasterService.showMessage('Approved', 'Certificate successfully approved');
      },
      err => {
        this.toasterService.showErrorMessage(err);
      }
    );
  }

  declineCertificateRequest() {

    if(!window.confirm('Are you sure you want to decline this request?')) {
      return;
    }

    this.certificateReqSvc.declineCertificateRequest(this.certificateRequest.id).subscribe(
      data => {
        this.removeEvent.emit(this.certificateRequestIndex);
        this.toasterService.showMessage('Removed', 'Certificate successfully removed');
      },
      err => {
        this.toasterService.showErrorMessage(err);
        
      }
    );
  }

}
