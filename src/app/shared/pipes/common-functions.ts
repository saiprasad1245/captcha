import {FormArray, FormControl, FormGroup } from "@angular/forms";
import * as moment from "moment";


export function validateAllFormFields(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);
      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        validateAllFormFields(control);
      } else if (control instanceof FormArray) {
        validateFormArray(control);
      }
    });
  }
  
  export function validateFormArray(formArray) {
    for (const control of formArray.controls) {
      if (control instanceof FormGroup) {
        validateAllFormFields(control);
      } else if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormArray) {
        validateFormArray(control);
      }
    }
  }

  export function noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { 'whitespace': true };
  }

  export function ConvertDateTimeToUTC(date){
    return moment.utc(date).format('YYYY-MM-DD HH:mm:ss')
  }

  export function ConvertDateTimeFromUTC(date,format){
    return moment.utc(date).local().format(format)
  }
  


  