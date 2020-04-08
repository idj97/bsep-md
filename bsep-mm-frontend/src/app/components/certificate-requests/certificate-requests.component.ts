import { Component, OnInit } from '@angular/core';
import { CertificateSignRequest } from 'src/app/dtos/CertificateSignRequest.dto';

@Component({
  selector: 'app-certificate-requests',
  templateUrl: './certificate-requests.component.html',
  styleUrls: ['./certificate-requests.component.css']
})
export class CertificateRequestsComponent implements OnInit {

  certificateSignRequests: Array<CertificateSignRequest>;

  constructor() {
    this.certificateSignRequests = [];
    
    /*
    let csr = new CertificateSignRequest();
    csr.city = 'Lawrenceville';
    csr.commonName = 'Some common name';
    csr.country = 'USA, Georgia';
    csr.email = 'somecsr@someemail.com';
    csr.organisation = 'Some organisation';
    csr.organisationUnit = 'Some organisation unit ';

    this.certificateSignRequests.push(csr);
    this.certificateSignRequests.push(csr);
    this.certificateSignRequests.push(csr);*/

  }

  ngOnInit() {
  }

  getCertificateRequests() {

  }

  approveCertificateRequest() {

  }

  declineCertificateRequest() {

  }

}
