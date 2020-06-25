import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { LogDialogService } from 'src/app/services/log-dialog.service';

@Component({
  selector: 'div [app-log-dialog]',
  templateUrl: './log-dialog.component.html',
  styleUrls: ['./log-dialog.component.css']
})
export class LogDialogComponent implements OnInit {

  private subscription: Subscription;

  private activated: boolean;
  private item: any;

  constructor(private logDialogService: LogDialogService) { }

  ngOnInit() {
    this.subscription = this.logDialogService.receiveLog().subscribe(
      data => {
        this.activated = data.isOpened;
        this.item = data.item;
      }
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

}
