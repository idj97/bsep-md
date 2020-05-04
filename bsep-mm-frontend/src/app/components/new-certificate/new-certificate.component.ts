import { Component, OnInit, ViewChild } from '@angular/core';
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons';
import { CertificateAuthority } from 'src/app/dtos/CertificateAuthority.dto';
import { Certificate } from 'src/app/dtos/Certificate.dto';
import { CertificateAuthorityService } from 'src/app/services/certificate-authority.service';
import { DateButton } from 'angular-bootstrap-datetimepicker';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-new-certificate',
  templateUrl: './new-certificate.component.html',
  styleUrls: ['./new-certificate.component.css']
})
export class NewCertificateComponent implements OnInit {

  activeInput: number = -1;
  private _validFromDate: Date;
  private _validUntilDate: Date;

  certificateAuthority: CertificateAuthority = new CertificateAuthority();;
  set validFromDate(date: Date) {
    this._validFromDate = date;
    this.certificateAuthority.certificateDto.validFrom = this.datePipe.transform(date, 'dd-MM-yyyy');
  }

  get validFromDate() {
    return this._validFromDate;
  }

  set validUntilDate(date: Date) {
    this._validUntilDate = date;
    this.certificateAuthority.certificateDto.validUntil = this.datePipe.transform(date, 'dd-MM-yyyy');
  }

  get validUntilDate() {
    return this._validUntilDate;
  }

  datesValid: boolean;
  datePipe: DatePipe;
  @ViewChild("ncf", {static: false}) newCertificateForm: any;

  private blurTimeout;

  //ICONS
  faQuestionCircle = faQuestionCircle;

  constructor(private caService: CertificateAuthorityService) { }

  ngOnInit() {
    this.datesValid = true;
    this.certificateAuthority.caType = 0;
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

    // validating dates
    if(this.validFromDate >= this.validUntilDate) {
      this.datesValid = false;
      return;
    } else {
      this.datesValid = true;
    }

    if(this.newCertificateForm.valid) {
      this.formatDates();
      this.certificateAuthority.certificateDto.certificateType = this.getType(this.certificateAuthority.caType);
      this.caService.createCA(this.certificateAuthority).subscribe(
        data => {
          console.log(data);
          this.newCertificateForm.resetForm();
        },
        err => {
          console.log(err.error);
        }
      );
    }
  }

  formatDates() {
    this.certificateAuthority.certificateDto.validFrom =
      this.datePipe.transform(this.validFromDate, 'dd-MM-yyyy HH:mm');
    this.certificateAuthority.certificateDto.validUntil =
      this.datePipe.transform(this.validUntilDate, 'dd-MM-yyyy HH:mm');
  }

  getType(enumValue: number) {

    if(enumValue == 0) {
      return 'ROOT';
    } else if(enumValue == 1) {
      return 'SIEM_AGENT_ISSUER';
    } else{
      return 'SIEM_CENTER_ISSUER';
    }

  }

}
