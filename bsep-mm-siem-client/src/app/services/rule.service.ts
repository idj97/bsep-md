import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RuleDto } from '../dtos/rule.dto';

@Injectable({
  providedIn: 'root'
})
export class RuleService {

  constructor(private http: HttpClient) { }

  getAllRules(): Observable<any> {
    return this.http.get('api/rule');
  }

  getSimpleTemplate(): Observable<any> {
    return this.http.get('api/rule/simple-template', { responseType: 'text' });
  }

  getCepTemplate(): Observable<any> {
    return this.http.get('api/rule/cep-template', { responseType: 'text' });
  }


  createNewRule(ruleDto: RuleDto): Observable<any> {
    return this.http.post('api/rule', ruleDto);
  }

}
