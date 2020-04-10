import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { RevokeDialogService } from 'src/app/services/revoke-dialog.service';

@Component({
  selector: 'div [app-revoke-dialog]',
  templateUrl: './revoke-dialog.component.html',
  styleUrls: ['./revoke-dialog.component.css']
})
export class RevokeDialogComponent implements OnInit {

  private activated: boolean = false;
  private subscription: Subscription;
  private data: any = null;
  private revoked: boolean = false;
  private revoking: boolean = false;

  constructor(private revokeDialogService: RevokeDialogService) { }

  ngOnInit() {
    this.subscription = this.revokeDialogService.getData().subscribe(
      data => {
        this.activated = data.open;
        if (data.open) {
          this.data = data;
          this.revoked = false;
          this.revoking = false;
        }
      }
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  revoke(): void {
    this.revoking = true;
  }

}
