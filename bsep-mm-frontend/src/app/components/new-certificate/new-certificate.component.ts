import { Component, OnInit, ViewChild } from '@angular/core';
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons';
import { CertificateAuthority } from 'src/app/dtos/CertificateAuthority.dto';
import { Certificate } from 'src/app/dtos/Certificate.dto';

@Component({
  selector: 'app-new-certificate',
  templateUrl: './new-certificate.component.html',
  styleUrls: ['./new-certificate.component.css']
})
export class NewCertificateComponent implements OnInit {

  certificateAuthority: CertificateAuthority;
  @ViewChild("ncf", {static: false}) newCertificateForm: any;

  private blurTimeout;

  //ICONS
  faQuestionCircle = faQuestionCircle;

  constructor() { }

  ngOnInit() {
    this.certificateAuthority = new CertificateAuthority();
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

  createCertificate() {
    console.log(this.certificateAuthority);
    console.log(this.newCertificateForm.valid);
  }

}
