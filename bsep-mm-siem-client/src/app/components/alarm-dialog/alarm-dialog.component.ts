import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AlarmDialogService } from 'src/app/services/alarm-dialog.service';
import { LogDialogService } from 'src/app/services/log-dialog.service';

@Component({
  selector: 'div [app-alarm-dialog]',
  templateUrl: './alarm-dialog.component.html',
  styleUrls: ['./alarm-dialog.component.css']
})
export class AlarmDialogComponent implements OnInit {

  private subscription: Subscription;

  private activated: boolean;
  private item: any;

  constructor(private alarmDialogService: AlarmDialogService,
              private logDialogService: LogDialogService) { }

  ngOnInit() {
    this.subscription = this.alarmDialogService.receiveAlarm().subscribe(
      data => {
        console.log(data);
        this.activated = data.isOpened;
        this.item = data.item;
      }
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  selectLog(item: any): void {
    this.logDialogService.sendLog(
      {
        isOpened: true,
        item: item
      }
    );
  }

}
