import { Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { faArrowUp, faArrowDown } from '@fortawesome/free-solid-svg-icons';
import { UtilityService } from 'src/app/services/utility.service';
import { SearchLogs } from 'src/app/dtos/search-logs.dto';
import { LogEventsService } from 'src/app/services/log-events.service';
import { LogDialogService } from 'src/app/services/log-dialog.service';

@Component({
  selector: 'app-log-events',
  templateUrl: './log-events.component.html',
  styleUrls: ['./log-events.component.css']
})
export class LogEventsComponent implements OnInit {

  //Icons
  faArrowUp = faArrowUp;
  faArrowDown = faArrowDown;

  private logSearchDTO: SearchLogs = new SearchLogs();
  private activeInput = -1;
  private showSearchFields: boolean = false;
  private data: any = {
    items: []
  };
  private pageIncrementing: boolean = false;

  private timeout = null;

  @ViewChild("ncf", {static: false}) logSearchForm: any;

  constructor(private utilityService: UtilityService,
              private logEventsService: LogEventsService,
              private logDialogService: LogDialogService) { }

  ngOnInit() {
    this.updateLogs();
  }

  selectLog(item: any): void {
    this.logDialogService.sendLog(
      {
        isOpened: true,
        item: item
      }
    );
  }

  updateLogs() : void {
    this.logEventsService.postSearchLogs(this.logSearchDTO).subscribe(
      data => {
        console.log(data);
        this.data = data;
      },
      error => {
        console.log(error);
      }
    );
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

  textFieldsChanged() {
    clearTimeout(this.timeout);
    console.log(this.logSearchDTO.timezone);
    this.timeout = setTimeout(() => {
      this.logSearchDTO.pageNum = 0;
      this.updateLogs();
    }, 700);
  }

  @HostListener('window:mousewheel', ['$events'])
  onScroll(event) {
    if (this.pageIncrementing) return;
    let element = document.getElementById('app-main-content');
    let perc = (element.scrollTop + element.offsetHeight) / element.scrollHeight;
    console.log(perc);
    if (perc > 0.90 && this.logSearchDTO.pageNum < this.data.totalPages) {
      this.pageIncrementing = true;
      this.logSearchDTO.pageNum++
      this.logEventsService.postSearchLogs(this.logSearchDTO).subscribe(
        data => {
          data.items.forEach(element => {
            this.data.items.push(element);
          });
          
        },
        error => {
          console.log(error);
        }
      ).add(() => {
        this.pageIncrementing = false;
      });;
    }
    
  }

}
