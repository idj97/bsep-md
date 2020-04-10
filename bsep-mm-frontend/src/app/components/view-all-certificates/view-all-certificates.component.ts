import { Component, OnInit, Input } from '@angular/core';
import { RevokeDialogService } from 'src/app/services/revoke-dialog.service';

@Component({
  selector: 'app-view-all-certificates',
  templateUrl: './view-all-certificates.component.html',
  styleUrls: ['./view-all-certificates.component.css']
})
export class ViewAllCertificatesComponent implements OnInit {

  private data: any[] = [];
  private elStatus: any[] = [];

  @Input() isRevoked: boolean;

  @Input() set parentData(parentData) {
    let items = parentData.items;
    let finalData = []
    let elStatus = []
    for (let i = 0; i < items.length; i++) {
      let root = items[i];
      if (!this.isRevoked && root.certificateDto.revocation) continue;
      let rootStatus = {
        isHovered: false,
        isSelected: false,
      }

      if (root.caIssuerId) {
        this.formCertificateData(root, rootStatus, items);
      }
      finalData.push(root);
      elStatus.push(rootStatus);
    }

    this.data = finalData;
    this.elStatus = elStatus;
  }

  

  constructor(private revokeDialogService: RevokeDialogService) { }

  ngOnInit() {
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
    if (!this.elStatus[index].isSelected) {
      this.elStatus[index].isHovered = false;
    }
  }

  moreOrLessIssuerInformation(event, issuerStatus): void {
    issuerStatus.isSelected = !issuerStatus.isSelected;
  }

}
