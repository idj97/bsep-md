import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TestService {

  constructor(private http: HttpClient) { }


  unsecuredTest(): Observable<any> {
    return this.http.get('unsecured/hello' , { responseType: 'text' });
  }

  securedTest(): Observable<any> {
    return this.http.get('https://localhost:8441/api/test/hello', { responseType: 'text' });
  }
}
