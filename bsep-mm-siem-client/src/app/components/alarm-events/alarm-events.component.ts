import { Component, OnInit, ViewChild } from '@angular/core';
import { faArrowUp, faArrowDown } from '@fortawesome/free-solid-svg-icons';
import { SearchAlarms } from 'src/app/dtos/search-alarms.dto';
import { UtilityService } from 'src/app/services/utility.service';
import { AlarmDialogService } from 'src/app/services/alarm-dialog.service';

@Component({
  selector: 'app-alarm-events',
  templateUrl: './alarm-events.component.html',
  styleUrls: ['./alarm-events.component.css']
})
export class AlarmEventsComponent implements OnInit {

  //Icons
  faArrowUp = faArrowUp;
  faArrowDown = faArrowDown;

  private showSearchFields: boolean = false;
  private alarmSearchDTO: SearchAlarms = new SearchAlarms();
  private activeInput = -1;
  private pageIncrementing: boolean = false;
  private data: any = {
    items: [
      {
        name: 'dsadasd',
        timestamp: 'dsadasd',
        alarmType: 'dsadasd',
        machineOS: 'dsadasd',
        machineIP: 'dsadasd',
        logs: [
          {
            eventType: 'dsad',
            eventName:'dsad',
            eventId:'dsad',
            machineOS:'dsad',
          }
        ]
      }
    ]
  };

  private timeout = null;


  @ViewChild("ncf", {static: false}) alarmSearchForm: any;

  constructor(private utilityService: UtilityService,
              private alarmDialogService: AlarmDialogService) { }

  ngOnInit() {
  }

  selectAlarm(item: any): void {
    this.alarmDialogService.sendAlarm(
      {
        isOpened: true,
        item: item
      }
    );
  }

  toggleSearchFields(): void {
    this.showSearchFields = !this.showSearchFields;
  }

  focusInput(event: FocusEvent, index) {
    this.activeInput = index;
  }

  blurInput(event: FocusEvent, index) {
    if (event.relatedTarget) {
      
      let relTarget = <HTMLElement>event.relatedTarget;
      if (relTarget.classList.contains('dl-abdtp-date-button') ||
      relTarget.classList.contains('dl-abdtp-right-button') ||
      relTarget.classList.contains('dl-abdtp-left-button')) {
        (<HTMLInputElement>event.target).focus();
        return;
      } 
    }
    
    if (this.activeInput == index || this.isEmpty(index)) {
      this.activeInput = -1;
    }
  }

  isEmpty(index: any): boolean {
    return this.utilityService.isEmpty(index);
  }

  textFieldsChanged() {
    clearTimeout(this.timeout);
    console.log(this.alarmSearchDTO);
    this.timeout = setTimeout(() => {
      //this.logSearchDTO.pageNum = 0;
      //this.updateLogs();
    }, 700);
  }

}
