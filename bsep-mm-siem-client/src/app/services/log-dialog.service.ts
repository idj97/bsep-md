import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LogDialogService {

  private subject: Subject<any> = new Subject<any>();

  constructor() { }

  sendLog(log: any): void {
    this.subject.next(log);
  }

  receiveLog(): Observable<any> {
    return this.subject.asObservable();
  }

}
