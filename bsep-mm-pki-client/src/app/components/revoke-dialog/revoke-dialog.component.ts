import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { RevokeDialogService } from 'src/app/services/revoke-dialog.service';
import { CertificateService } from 'src/app/services/certificate.service';

@Component({
  selector: 'div [app-revoke-dialog]',
  templateUrl: './revoke-dialog.component.html',
  styleUrls: ['./revoke-dialog.component.css']
})
export class RevokeDialogComponent implements OnInit {

  private activated: boolean = false;
  private subscription: Subscription;

  private data: any = null;
  private selectedValue = 0;

  private revoked: boolean = false;
  private revoking: boolean = false;

  constructor(private revokeDialogService: RevokeDialogService,
              private certificateService: CertificateService) { }

  ngOnInit() {
    this.subscription = this.revokeDialogService.getData().subscribe(
      data => {
        this.activated = data.open;
        if (data.open) {
          this.data = data;
          console.log(this.data);
          if (data.revoking && data.revoked) {
            this.revoked = true;
            this.revoking = true;
          }
          else {
            this.revoked = false;
            this.revoking = false;
          }
          
        }
      }
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  revoke(data): void {
    this.revoking = true;
    let dl = {
      certificateId: data.id,
      serialNumber: data.serialNumber,
      revokeReason: this.selectedValue,
    }

    this.certificateService.postRevoke(dl).subscribe(
      data => {
        let d = this.data;
        d.open = true;
        d.revoking = true;
        d.revoked = true;
        this.revokeDialogService.sendData(d);
        this.selectedValue = 0;
      },
      error => {
        console.log(error);
      }
    );
  }

}
