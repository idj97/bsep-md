import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlarmDialogService {

  private subject: Subject<any> = new Subject<any>();

  constructor() { }

  sendAlarm(alarm: any): void {
    this.subject.next(alarm);
  }

  receiveAlarm(): Observable<any> {
    return this.subject.asObservable();
  }
}
