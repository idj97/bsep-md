import { Component, OnInit } from '@angular/core';
import { CertificateService } from 'src/app/services/certificate.service';
import { faArrowDown, faArrowUp } from '@fortawesome/free-solid-svg-icons';
import { RevokeDialogService } from 'src/app/services/revoke-dialog.service';

@Component({
  selector: 'app-ca-revoked',
  templateUrl: './ca-revoked.component.html',
  styleUrls: ['./ca-revoked.component.css']
})
export class CaRevokedComponent implements OnInit {

  faArrowDown = faArrowDown;
  faArrowUp = faArrowUp;

  private data: any[] = [];
  private elStatus: any[] = [];

  constructor(private certificateService: CertificateService,
              private revokeDialogService: RevokeDialogService) { }

  ngOnInit() {
    this.certificateService.getAllCA().subscribe(
      data => {
        let finalData = []
        let elStatus = []
        for (let i = 0; i < data.length; i++) {
          let root = data[i];
          let rootStatus = {
            isHovered: false,
            isSelected: false,
          }

          if (root.caIssuerId) {
            this.formCertificateData(root, rootStatus, data);
          }
          finalData.push(root);
          elStatus.push(rootStatus);
        }
        console.log(finalData);

        this.data = finalData;
        this.elStatus = elStatus;
      },

      error => {
        console.log(error);
      }
    );
  }

  initRevokeDialog(item: any): void {
    item.open = true;
    this.revokeDialogService.sendData(item);
  }

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

  mouseEnter(event, index): void {
    this.elStatus[index].isHovered = true;
  }

  mouseLeave(event, index): void {
    this.elStatus[index].isHovered = false;
  }

  moreOrLessInformation(event, index): void {
    this.elStatus[index].isSelected = !this.elStatus[index].isSelected;
  }

  moreOrLessIssuerInformation(event, issuerStatus): void {
    issuerStatus.isSelected = !issuerStatus.isSelected;
  }

}
