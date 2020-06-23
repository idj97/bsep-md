import { Component, OnInit, ViewChild } from '@angular/core';
import { UtilityService } from 'src/app/services/utility.service';
import { faArrowUp, faArrowDown } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-log-events',
  templateUrl: './log-events.component.html',
  styleUrls: ['./log-events.component.css']
})
export class LogEventsComponent implements OnInit {

  //Icons
  faArrowUp = faArrowUp;
  faArrowDown = faArrowDown;

  private logSearchDTO: any = {};
  private activeInput = -1;
  private showSearchFields: boolean = true;

  private timeout = null;

  @ViewChild("ncf", {static: false}) logSearchForm: any;

  constructor(private utilityService: UtilityService) { }

  ngOnInit() {
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

  toggleSearchFields(): void {
    this.showSearchFields = !this.showSearchFields;
  }

  typed() {
    clearTimeout(this.timeout);
    this.timeout = setTimeout(() => {
      console.log("dsadadsa");
    }, 1000);
  }

}
