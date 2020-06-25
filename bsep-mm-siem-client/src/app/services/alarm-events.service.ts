import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlarmEventsService {

  constructor(private http: HttpClient) { }

  postSearchAlarms(dto: any): Observable<any> {
    return this.http.post('/api/alarms/search', dto);
  }
}
