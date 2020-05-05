import { Component, OnInit, ViewChild } from '@angular/core';
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons';
import { CertificateAuthority } from 'src/app/dtos/CertificateAuthority.dto';
import { CertificateAuthorityService } from 'src/app/services/certificate-authority.service';
import { DateButton } from 'angular-bootstrap-datetimepicker';
import { DatePipe } from '@angular/common';
import { ToasterService } from 'src/app/services/toaster.service';

@Component({
  selector: 'app-new-certificate',
  templateUrl: './new-certificate.component.html',
  styleUrls: ['./new-certificate.component.css']
})
export class NewCertificateComponent implements OnInit {

  activeInput: number = -1;
  private _validFromDate: Date;

  certificateAuthority: CertificateAuthority = new CertificateAuthority();

  set validFromDate(date: Date) {
    this._validFromDate = date;
    this.certificateAuthority.certificateDto.validFrom = this.datePipe.transform(date, 'dd-MM-yyyy');
  }

  get validFromDate() {
    return this._validFromDate;
  }


  datePipe: DatePipe;
  @ViewChild("ncf", {static: false}) newCertificateForm: any;

  submitting: boolean;
  private blurTimeout;

  //ICONS
  faQuestionCircle = faQuestionCircle;

  constructor(
    private caService: CertificateAuthorityService,
    private toasterSvc: ToasterService
    ) { }

  ngOnInit() {
    this.submitting = false;
    this.setDefaultFormValues();
    this.datePipe = new DatePipe('en-US');
  }

  focusInput(event: FocusEvent, index) {
    this.activeInput = index;
  }

  blurInput(event: FocusEvent, index) {
    console.log(event);
    if (event.relatedTarget) {
      
      let relTarget = <HTMLElement>event.relatedTarget;
      if (relTarget.classList.contains('dl-abdtp-date-button') ||
      relTarget.classList.contains('dl-abdtp-right-button') ||
      relTarget.classList.contains('dl-abdtp-left-button')) {
        (<HTMLInputElement>event.target).focus();
        return;
      } 
    }
    
    if (this.activeInput == index || this.isEmpty(index)) {
      this.activeInput = -1;
    }
  }

  isEmpty(index: number) {
    return (<HTMLInputElement>document.querySelectorAll('.input-holder .input-styled')[index]).value == '';
  }

  isActiveInput(index: number) {
    document.querySelectorAll('input-holder .input-styled')
  }

  futureDatesOnly(dateButton: DateButton, viewName: string) {
    return dateButton.value > (new Date()).getTime();
  }

  createCertificate() {

    if(this.newCertificateForm.valid) {
      this.submitting = true;
      this.formatDate();
      this.certificateAuthority.certificateDto.certificateType = this.getEnumString(this.certificateAuthority.caType);
      this.caService.createCA(this.certificateAuthority).subscribe(
        data => {
          this.newCertificateForm.resetForm();
          this.setDefaultFormValues();
          this.toasterSvc.showMessage('Success', 'Certificate authority successfully created');
        },
        err => {
          this.toasterSvc.showErrorMessage(err);
        }
      ).add(() => {
        this.submitting = false;
      });
    }
  }

  formatDate() {
    this.certificateAuthority.certificateDto.validFrom =
      this.datePipe.transform(this.validFromDate, 'dd-MM-yyyy HH:mm');
  }

  setDefaultFormValues() {
    this.certificateAuthority.certificateDto.certificateType = 'SIEM_AGENT_ISSUER';
    this.certificateAuthority.caType = 1;
    this.certificateAuthority.certificateDto.validityInMonths = 6;
  }

  caTypeChanged(event: any) {
    if (event.target.value == 0) { // if root ca selected
      this.certificateAuthority.certificateDto.validityInMonths = 72;
    } else {
      this.certificateAuthority.certificateDto.validityInMonths = 6;
    }
  }

  
  getEnumString(enumNumber: number) {

    if(enumNumber == 0) {
      return 'ROOT';
    } else if(enumNumber == 1) {
      return 'SIEM_AGENT_ISSUER';
    } else {
      return 'SIEM_CENTER_ISSUER';
    }

  }

}
