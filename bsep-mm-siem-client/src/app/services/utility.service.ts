import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UtilityService {

  constructor() { }

  isEmpty(index: any) {
    let element = (<HTMLInputElement>document.querySelector('.input-holder .input-styled[data-index="'+ index +'"]'));
    if (!element) {
      element = (<HTMLInputElement>document.querySelector('.input-holder .custom[data-index="'+ index +'"]'));
    }
    return element.value == '';
  }

  
}
