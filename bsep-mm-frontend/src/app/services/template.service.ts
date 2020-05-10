import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TemplateService {

  constructor(private http: HttpClient) { }


  getAllTemplates(): Observable<any> {
    return this.http.get<any>('/api/templates');
  }

  postCreateTemplate(template: any): Observable<any> {
    return this.http.post<any>('/api/templates', template);
  }

  deleteRemoveTemplate(templateName: string) {
    return this.http.delete<any>(`/api/templates/${templateName}`);
  }
  
}
