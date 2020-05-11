import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateCertificate } from '../dtos/create-certificate.dto';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  constructor(private http: HttpClient) { }

  getAllCA(): Observable<any> {
    return this.http.get<any>('api/ca');
  }

  postSearch(certificateSearchDto: any): Observable<any> {
    return this.http.post<any>('api/ca/search', certificateSearchDto);
  }

  postRevoke(revocationDto: any): Observable<any> {
    return this.http.post<any>('api/certificates/revoke', revocationDto);
  }

  postSearchUserCertificate(certificateSearchDto: any) {
    return this.http.post<any>('api/certificates/search', certificateSearchDto);
  }

  postSimpleSearchCertificate(certificateSearchDto: any) {
    return this.http.post<any>('api/certificates/simple-search', certificateSearchDto);
  }

  getSignWithCertificates(): Observable<any> {
    return this.http.get<any>('api/certificates/authorities');
  }

  getSerialNumber(): Observable<any> {
    return this.http.get<any>('api/certificates/serial-number');
  }

  getSignatureAlghoritms(algorithm: string): Observable<any> {
    return this.http.get<any>(`api/algorithms/signing/${algorithm}`);
  }

  postCreateCertificate(data: CreateCertificate): Observable<any> {
    return this.http.post<any>('api/certificates', data);
  }

}
