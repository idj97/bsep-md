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

  certificateAuthority: CertificateAuthority;
  validFromDate: Date;
  validUntilDate: Date;

  datesValid: boolean;
  datePipe: DatePipe;
  @ViewChild("ncf", {static: false}) newCertificateForm: any;

  private blurTimeout;

  //ICONS
  faQuestionCircle = faQuestionCircle;

  constructor(private caService: CertificateAuthorityService) { }

  ngOnInit() {
    this.certificateAuthority = new CertificateAuthority();
    this.datesValid = true;
    this.certificateAuthority.caType = 0;
    this.datePipe = new DatePipe('en-US');
  }

  focusInput(event: FocusEvent) {
    let el = event.target;
    let parent = (<HTMLElement> el).parentElement;
    let text = <HTMLElement> parent.getElementsByClassName('input-text-value')[0];

    text.classList.add('focused');
    text.classList.remove('blurred');
  }

  blurInput(event: FocusEvent) {
    clearTimeout(this.blurTimeout);
    this.blurTimeout = setTimeout(() => {
      let el = <HTMLInputElement>event.target;

      if (el.value != '') return;

      let parent = (<HTMLElement> el).parentElement;
      let text = <HTMLElement> parent.getElementsByClassName('input-text-value')[0];
      text.classList.remove('focused');
      text.classList.add('blurred');
    }, 20);
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

}
