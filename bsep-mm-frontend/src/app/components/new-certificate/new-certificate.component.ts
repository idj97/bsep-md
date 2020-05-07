import { Component, OnInit, ViewChild } from '@angular/core';
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons';
import { CertificateAuthority } from 'src/app/dtos/CertificateAuthority.dto';
import { CertificateAuthorityService } from 'src/app/services/certificate-authority.service';
import { DateButton } from 'angular-bootstrap-datetimepicker';
import { DatePipe } from '@angular/common';
import { ToasterService } from 'src/app/services/toaster.service';
import { faPlus, faArrowUp, faCircle, faTimes } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-new-certificate',
  templateUrl: './new-certificate.component.html',
  styleUrls: ['./new-certificate.component.css']
})
export class NewCertificateComponent implements OnInit {

  private extensionsSelected: boolean = false;
  private additionalSelected: boolean = false;

  private allSelections: any = {
    showKeyUsages: false,
    showExtendedKeyUsages: false,
    showBasicConstraints: false,
  };

  private selectedExtensions: any[] = [];

  private tempData: any = {
    keyUsage: [],
    extendedKeyUsage: [],
    basicConstraints: {
      isCA: false,
      pathLen: 0,
    },
    subjectAlternativeName: [
      {
        value: '',
        type: 'ip',
      },
    ],
  }

  private savedData: any = {
    keyUsage: [],
    extendedKeyUsage: [],
    basicConstraints: {
      isCA: false,
      pathLen: 0,
    },
  };

  //CONSTS
  EXTENDED_KEY_USAGE = 'EXTENDED_KEY_USAGE';
  KEY_USAGE = 'KEY_USAGE';
  BASIC_CONSTRAINTS = 'BASIC_CONSTRAINTS';


  //ICONS
  faPlus = faPlus;
  faArrowUp = faArrowUp;
  faCircle = faCircle;
  faTimes = faTimes;
  //---

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

  test(el) {
    console.log(el);
    return false;
  }

  focusInput(event: FocusEvent, index) {
    this.activeInput = index;
  }

  blurInput(event: FocusEvent, index) {
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

  isEmpty(index: any) {
    let element = (<HTMLInputElement>document.querySelector('.input-holder .input-styled[data-index="'+ index +'"]'));
    return element.value == '';
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

  toggleExtensions(): void {
    for (let i in this.allSelections) {
      this.allSelections[i] = false;
    }

    if (this.additionalSelected) {
      this.extensionsSelected = false;
      this.additionalSelected = false;
      return;
    }
    this.extensionsSelected = !this.extensionsSelected;
    this.additionalSelected = !this.additionalSelected;

    
  }

  //BASIC CONSTRAINTS

  toggleIsCA(): void {
    this.tempData.basicConstraints.isCA = !this.tempData.basicConstraints.isCA;
  }

  closeBasicConstraintsSelected(): void {
    this.additionalSelected = true;
    this.extensionsSelected = true;
    this.allSelections.showBasicConstraints = false;
  }

  openBasicConstraintsSelected(): void {
    this.additionalSelected = true;
    this.extensionsSelected = false;
    this.allSelections.showBasicConstraints = true;
  }

  basicConstraintsSelected(): void {
    if (this.containsType(this.BASIC_CONSTRAINTS)) return;
    this.openBasicConstraintsSelected();
  }

  cancelBasicConstraintsSelected(): void {
    this.tempData.basicConstraints = JSON.parse(JSON.stringify(this.savedData.basicConstraints));
    this.closeBasicConstraintsSelected();
  }

  saveBasicConstraintsSelected(): void {
    this.closeBasicConstraintsSelected();
    if (!this.containsType(this.BASIC_CONSTRAINTS)) {
      this.selectedExtensions.push({
        type: this.BASIC_CONSTRAINTS,
        name: 'Basic Constraints',
        critical: false,
      });
    }
    this.savedData.basicConstraints = JSON.parse(JSON.stringify(this.tempData.basicConstraints));
  }


  //KEY USAGE

  closeKeyUsageSelected(): void {
    this.additionalSelected = true;
    this.extensionsSelected = true;
    this.allSelections.showKeyUsages = false;
  }

  openKeyUsageSelected(): void {
    this.additionalSelected = true;
    this.extensionsSelected = false;
    this.allSelections.showKeyUsages = true;
  }

  keyUsageSelected(): void {
    if (this.containsType(this.KEY_USAGE)) return;
    this.openKeyUsageSelected();
  }

  cancelKeyUsageSelected(): void {
    this.tempData.keyUsage = JSON.parse(JSON.stringify(this.savedData.keyUsage));
    this.closeKeyUsageSelected();
  }

  saveKeyUsageSelected(): void {
    this.closeKeyUsageSelected();
    if (!this.containsType(this.KEY_USAGE)) {
      this.selectedExtensions.push({
        type: this.KEY_USAGE,
        name: 'Key Usage',
        critical: false,
      });
    }
    this.savedData.keyUsage = JSON.parse(JSON.stringify(this.tempData.keyUsage));
  }

  toggleKeyUsageSelection(event: Event): void {
    let el = <HTMLElement>event.currentTarget;
    if (this.tempData.keyUsage.includes(el.dataset.usage)) {
      this.tempData.keyUsage.splice(this.tempData.keyUsage.findIndex(x => x == el.dataset.usage), 1);
    }
    else {
      this.tempData.keyUsage.push(el.dataset.usage);
    }
  }





  //EXTENDED KEY USAGE

  closeExtendedKeyUsageSelected(): void {
    this.additionalSelected = true;
    this.extensionsSelected = true;
    this.allSelections.showExtendedKeyUsages = false;
  }

  openExtendedKeyUsageSelected(): void {
    this.additionalSelected = true;
    this.extensionsSelected = false;
    this.allSelections.showExtendedKeyUsages = true;
  }


  extendedKeyUsageSelected(): void {
    if (this.containsType(this.EXTENDED_KEY_USAGE)) return;
    this.openExtendedKeyUsageSelected();
  }

  cancelExtendedKeyUsageSelected(): void {
    this.tempData.extendedKeyUsage = JSON.parse(JSON.stringify(this.savedData.extendedKeyUsage));
    this.closeExtendedKeyUsageSelected();
  }

  saveExtendedKeyUsageSelected(): void {
    this.closeExtendedKeyUsageSelected();
    if (!this.containsType(this.EXTENDED_KEY_USAGE)) {
      this.selectedExtensions.push({
        type: this.EXTENDED_KEY_USAGE,
        name: 'Extended Key Usage',
        critical: false,
      });
    }
    this.savedData.extendedKeyUsage = JSON.parse(JSON.stringify(this.tempData.extendedKeyUsage));
  }

  toggleExtendedKeyUsageSelection(event: Event): void {
    let el = <HTMLElement>event.currentTarget;
    if (this.tempData.extendedKeyUsage.includes(el.dataset.usage)) {
      this.tempData.extendedKeyUsage.splice(this.tempData.extendedKeyUsage.findIndex(x => x == el.dataset.usage), 1);
    }
    else {
      this.tempData.extendedKeyUsage.push(el.dataset.usage);
    }
  }












  containsType(type: string): boolean {
    if (this.selectedExtensions.findIndex(x => x.type == type) == -1) return false;
    return true;
  }

  getByType(type: string): any {
    return this.selectedExtensions[this.selectedExtensions.findIndex(x => x.type == type)];
  }

  closeAll(): void {
    this.closeExtendedKeyUsageSelected();
    this.closeKeyUsageSelected();
    this.closeBasicConstraintsSelected();
  }

  editExtension(event): void {
    let parent = <HTMLElement> event.currentTarget.parentNode;
    let extension = this.getByType(parent.dataset.type);
    this.closeAll();
    if (extension.type == this.KEY_USAGE) {
      this.tempData.keyUsage = JSON.parse(JSON.stringify(this.savedData.keyUsage));
      this.openKeyUsageSelected();
    }
    else if (extension.type == this.EXTENDED_KEY_USAGE) {
      this.tempData.extendedKeyUsage = JSON.parse(JSON.stringify(this.savedData.extendedKeyUsage));
      this.openExtendedKeyUsageSelected();
    }
    else if (extension.type == this.BASIC_CONSTRAINTS) {
      this.tempData.basicConstraints = JSON.parse(JSON.stringify(this.savedData.basicConstraints));
      this.openBasicConstraintsSelected();
    }
  }

  markAsCritical(event): void {
    let parent = event.currentTarget.parentNode;
    let extension = this.getByType(parent.dataset.type);
    extension.critical = !extension.critical;
  }

  removeExtension(event): void {
    let parent = <HTMLElement> event.currentTarget.parentNode;
    let index = this.selectedExtensions.findIndex(x => x.type === parent.dataset.type);
    let extension = this.selectedExtensions[index];
    if (extension.type === this.KEY_USAGE) {
      this.savedData.keyUsage = [];
      this.tempData.keyUsage = [];
      this.selectedExtensions.splice(index, 1);
    }
    else if (extension.type === this.EXTENDED_KEY_USAGE) {
      this.savedData.extendedKeyUsage = [];
      this.tempData.extendedKeyUsage = [];
      this.selectedExtensions.splice(index, 1);
    }
    if (extension.type === this.BASIC_CONSTRAINTS) {
      this.savedData.basicConstraints = {
        isCA: false,
        pathLen: 0,
      };
      this.tempData.basicConstraints = {
        isCA: false,
        pathLen: 0,
      };
      this.selectedExtensions.splice(index, 1);
    }
  }

}
