import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LogEventsService {

  constructor(private http: HttpClient) { }

  postSearchLogs(dto: any): Observable<any> {
    return this.http.post('/api/logs/search', dto);
  }
}
