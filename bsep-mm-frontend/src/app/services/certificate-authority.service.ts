import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { CertificateAuthority } from '../dtos/CertificateAuthority.dto';

@Injectable({
  providedIn: 'root'
})
export class CertificateAuthorityService {

  constructor(private http: HttpClient) { }

  createCA(ca: CertificateAuthority): Observable<any> {
    return this.http.post('api/ca', ca);
  }

  getCAByType(type: string): Observable<any> {
    return this.http.get<any>(`api/ca/${type}`);
  }
}
