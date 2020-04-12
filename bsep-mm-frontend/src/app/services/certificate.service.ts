import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

}
