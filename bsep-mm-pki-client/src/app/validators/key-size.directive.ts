import { Directive } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[validateKeySize]',
  providers: [{
    provide: NG_VALIDATORS,
    useExisting: KeySizeDirective,
    multi: true
  }]
})
export class KeySizeDirective implements Validator {

  private keySizeMax: number = 2048;
  private keySizeMin: number = 512;

  validate(control: AbstractControl) : {[key: string]: any} | null {
    
    if (control.value) {
      let number = parseInt(control.value);
      if (isNaN(number)) return {keySizeNotNumber: true};
      if (number % 8 !== 0) return { keySizeInvalid: true };
      if (number < this.keySizeMin) return { 
        keySizeLessThanMin: true,
        keySizeMin: this.keySizeMin
      }
      if (number > this.keySizeMax) return {
        keySizeGreaterThanMax: true,
        keySizeMax: this.keySizeMax
      }
    }

    return null; // return null if validation is passed.

  }

}
