import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UtilityService {

  constructor() { }

  formCertificateData(root: any, rootStatus: any, data: any[]): any {
    let issuer = root;
    let issuerStatus = rootStatus;
    if (issuer.caIssuerId) {
      issuer.issuer = this.findIssuerById(issuer.caIssuerId, data);
      issuerStatus.issuerStatus = {
        isHovered: false,
        isSelected: false,
      }
      if (issuer.issuer.caIssuerId) {
        this.formCertificateData(issuer.issuer, issuerStatus.issuerStatus, data);
      }
    }

    return issuer;
  }

  findIssuerById(id: number, data: any[]): any {
    for (let i = 0; i < data.length; i++) {
      if (data[i].id == id) {
        return data[i];
      }
    }
    return null;
  }
}
