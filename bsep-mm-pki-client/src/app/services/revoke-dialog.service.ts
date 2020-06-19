import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RevokeDialogService {

  private subject: Subject<any> = new Subject();

  constructor() { }

  sendData(data) {
    this.subject.next(data);
  }

  getData(): Observable<any> {
    return this.subject.asObservable();
  }

}
