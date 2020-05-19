import { Directive } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[validateValidFor]',
  providers: [{
    provide: NG_VALIDATORS,
    useExisting: ValidForDirective,
    multi: true
  }]
})
export class ValidForDirective {

  private validForMin: number = 1;

  validate(control: AbstractControl) : {[key: string]: any} | null {
    
    if (control.value) {
      let number = parseInt(control.value);
      if (isNaN(number)) return { validForNotNumber: true };
      if (number < this.validForMin) return { 
        validForLessThanMin: true,
        validForMin: this.validForMin,
      };
    }

    return null; // return null if validation is passed.
  }

}
