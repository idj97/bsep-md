import { Component, OnInit, ViewChild } from '@angular/core';
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons';
import { CertificateAuthority } from 'src/app/dtos/CertificateAuthority.dto';
import { CertificateAuthorityService } from 'src/app/services/certificate-authority.service';
import { DateButton } from 'angular-bootstrap-datetimepicker';
import { DatePipe } from '@angular/common';
import { ToasterService } from 'src/app/services/toaster.service';
import { faPlus, faArrowUp, faCircle, faTimes } from '@fortawesome/free-solid-svg-icons';
import { CertificateService } from 'src/app/services/certificate.service';
import { CreateCertificate } from 'src/app/dtos/create-certificate.dto';

@Component({
  selector: 'app-new-certificate',
  templateUrl: './new-certificate.component.html',
  styleUrls: ['./new-certificate.component.css']
})
export class NewCertificateComponent implements OnInit {

  private createCertificateDTO: CreateCertificate = new CreateCertificate();

  private isSelfSigned: boolean = false;
  private validForDate: any = '0';
  private validUntil = null;
  private serNumber: number;
  private kSize: number = 2048;

  get validUntilText() {
    if (!this.validUntil) return null;
    return this.datePipe.transform(this.validUntil, 'dd-MM-yyyy');
  }
  
  private selects = {
    algorithm: {
      value: null,
      items: [
        {
          value: 'RSA',
          label: 'RSA',
        },
        {
          value: 'DSA',
          label: 'DSA',
        },
      ],
      focused: false,
    },
    signatureAlgorithm: {
      value: null,
      items: [],
      focused: false,
    },
    signWith: {
      value: null,
      items: [],
      focused: false,
    },
    periodType: {
      value: null,
      items: [
        {
          value: 'DAYS',
          label: 'Days',
        },
        {
          value: 'MONTHS',
          label: 'Months',
        },
        {
          value: 'YEARS',
          label: 'Years',
        },
      ],
      focused: false,
    }
  }

  

  private extensionsSelected: boolean = false;
  private additionalSelected: boolean = false;

  get sanHeight() {
    if (!this.allSelections.showSubjectAlternativeName) return 0;
    if (this.allSelections.showSubjectAlternativeName) {
      let val = this.tempData.subjectAlternativeName.length * 90 + 150;
      return val;
    }
  }

  private allSelections: any = {
    showKeyUsages: false,
    showExtendedKeyUsages: false,
    showBasicConstraints: false,
    showSubjectAlternativeName: false,
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
        focused: false,
        value: '',
        typeValue: null,
        type: [{
          value: 'IP',
          label: 'IP',
        },
        {
          value: 'DNS',
          label: 'DNS',
        }],
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
    subjectAlternativeName: [
      {
        focused: false,
        value: '',
        typeValue: null,
        type: [{
          value: 'IP',
          label: 'IP',
        },
        {
          value: 'DNS',
          label: 'DNS',
        }],
      },
    ],
  };

  //CONSTS
  EXTENDED_KEY_USAGE = 'EXTENDED_KEY_USAGE';
  KEY_USAGE = 'KEY_USAGE';
  BASIC_CONSTRAINTS = 'BASIC_CONSTRAINTS';
  SUBJECT_ALTERNATIVE_NAME = 'SUBJECT_ALTERNATIVE_NAME';

  SUBJECT_KEY_IDENTIFIER = 'SUBJECT_KEY_IDENTIFIER';
  AUTHORITY_KEY_IDENTIFIER = 'AUTHORITY_KEY_IDENTIFIER';
  AUTHORITY_INFO_ACCESS = 'AUTHORITY_INFO_ACCESS';


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
    this.createCertificateDTO.validFrom = this.datePipe.transform(date, 'dd-MM-yyyy');
    this.updateValidUntil();
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
    private toasterSvc: ToasterService,
    private certificateService: CertificateService,
    ) { }

  ngOnInit() {
    this.datePipe = new DatePipe('en-US');
    this.submitting = false;
    this.setDefaultFormValues();

    this.setUpSelects();
    this.updateValidUntil();
    
  }


  createCertificateFinal(): CreateCertificate {
    this.createCertificateDTO.keySize = this.kSize;
    this.createCertificateDTO.keyGenerationAlgorithm = this.selects.algorithm.value.value;
    this.createCertificateDTO.signatureAlgorithm = this.selects.signatureAlgorithm.value.value;
    this.createCertificateDTO.issuingCaSerialNumber = this.selects.signWith.value.value;
    this.createCertificateDTO.validUntil = this.validUntilText;
    this.createCertificateDTO.selfSigned = this.isSelfSigned;
    this.createCertificateDTO.serialNumber = this.serNumber.toString();
    this.createCertificateDTO.name.serialNumber = this.serNumber.toString();
    let extensions = [];
    for (let i = 0; i < this.selectedExtensions.length; i++) {
      let type = this.selectedExtensions[i].type;
      let isCritical = this.selectedExtensions[i].critical;
      let ex = <any> {
        type: type,
        isCritical: isCritical,
      };

      if (type == this.BASIC_CONSTRAINTS) {
        ex.isCa = this.savedData.basicConstraints.isCA;
        ex.pathLength = this.savedData.basicConstraints.pathLen;
      }
      else if (type == this.KEY_USAGE) {
        ex.digitalSignature = this.savedData.keyUsage.includes('DIGITAL_SIGNATURE');
        ex.nonRepudiation = this.savedData.keyUsage.includes('NON_REPUDIATION');
        ex.keyEncipherment = this.savedData.keyUsage.includes('KEY_ENCIPHERMENT');
        ex.dataEncipherment = this.savedData.keyUsage.includes('DATA_ENCIPHERMENT');
        ex.keyAgreement = this.savedData.keyUsage.includes('KEY_AGREEMENT');
        ex.keyCertSign = this.savedData.keyUsage.includes('CERTIFICATE_SIGNING');
        ex.cRLSign = this.savedData.keyUsage.includes('CRL_SIGN');
        ex.encipherOnly = this.savedData.keyUsage.includes('ENCIPHER_ONLY');
        ex.decipherOnly = this.savedData.keyUsage.includes('DECIPHER_ONLY');
      }
      else if (type == this.EXTENDED_KEY_USAGE) {
        ex.anyExtendedKeyUsage = this.savedData.extendedKeyUsage.includes('ANY_EXTENDED_USAGE');
        ex.serverAuth = this.savedData.extendedKeyUsage.includes('SERVER_AUTHENTICATION');
        ex.clientAuth = this.savedData.extendedKeyUsage.includes('CLIENT_AUTHENTICATION');
        ex.codeSigning = this.savedData.extendedKeyUsage.includes('CODE_SIGNING');
        ex.emailProtection = this.savedData.extendedKeyUsage.includes('EMAIL_PROTECTION');
        ex.OCSPSigning = this.savedData.extendedKeyUsage.includes('OCSP_SIGNING');
      }
      else if (type == this.SUBJECT_ALTERNATIVE_NAME) {
        let ips = this.savedData.subjectAlternativeName.filter(x => x.typeValue.value == 'IP').map(x => x.value);
        let dns = this.savedData.subjectAlternativeName.filter(x => x.typeValue.value == 'DNS').map(x => x.value);

        ex.dnsNames = dns;
        ex.ipAddresses = ips;
      }

      extensions.push(ex);
    }

    this.createCertificateDTO.extensions = extensions;

    return this.createCertificateDTO;
  }

  submit(): void {
    let dto = this.createCertificateFinal();
    console.log(dto);
    this.certificateService.postCreateCertificate(dto).subscribe(
      data => {
        console.log(data);
      },
      error => {
        console.log(error);
      }
    );
    
  }


  setUpSelects(): void {
    this.selects.algorithm.value = this.selects.algorithm.items[0];
    this.selects.periodType.value = this.selects.periodType.items[0];
    this.tempData.subjectAlternativeName[0].typeValue = this.tempData.subjectAlternativeName[0].type[0];

    this.certificateService.getSignWithCertificates().subscribe(
      data => {
        let items = data.map(x => {
          return {value: x.serialNumber, label: x.commonName};
        });
        this.selects.signWith.items = items;
      },
      error => {
        console.log(error);
      }
    );

    this.certificateService.getSignatureAlghoritms().subscribe(
      data => {
        let items = data.map(x => {
          return {value: x, label: x};
        });
        this.selects.signatureAlgorithm.items = items;
      },
      error => {
        console.log(error);
      }
    );

    
  }

  generateSerialNumber(): void {
    this.certificateService.getSerialNumber().subscribe(
      data => {
        this.serNumber = data;
      },
      error => {
        console.log(error);
      }
    );
  }

  test(el) {
    console.log(el);
    return false;
  }
  
  updateValidUntil() {
    if (!this.validFromDate ||
      isNaN(this.validForDate) ||
      this.validForDate == '' ||
      !this.selects.periodType.value) return;
    let val = this.selects.periodType.value.value
    let date = new Date();
    date.setTime(this.validFromDate.getTime());
    if (val == 'DAYS') {
      date.setDate(date.getDate() + parseInt(this.validForDate));
    }
    else if (val == 'MONTHS') {
      date.setMonth(date.getMonth() + parseInt(this.validForDate));
    }
    else if (val == 'YEARS') {
      date.setFullYear(date.getFullYear() + parseInt(this.validForDate));
    }
    
    this.validUntil = date;
  }

  toggleSelfSigned() {
    this.isSelfSigned = !this.isSelfSigned;
  }

  focusSelect(event, obj) {
    obj.focused = true;
  }

  blurSelect(event, obj) {
    obj.focused = false;
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
    if (!element) {
      element = (<HTMLInputElement>document.querySelector('.input-holder .custom[data-index="'+ index +'"]'));
    }
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
    this.validFromDate = new Date();
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




  //SUBJECT ALTERNATIVE NAME

  closeSubjectAlternativeNameSelected(): void {
    this.additionalSelected = true;
    this.extensionsSelected = true;
    this.allSelections.showSubjectAlternativeName = false;
  }

  openSubjectAlternativeNameSelected(): void {
    this.additionalSelected = true;
    this.extensionsSelected = false;
    this.allSelections.showSubjectAlternativeName = true;
  }

  subjectAlternativeNameSelected(): void {
    if (this.containsType(this.SUBJECT_ALTERNATIVE_NAME)) return;
    this.openSubjectAlternativeNameSelected();
  }


  addNewSubjectAlternativeName(): void {
    let item = {
      focused: false,
      value: '',
      typeValue: null,
      type: [{
        value: 'IP',
        label: 'IP',
      },
      {
        value: 'DNS',
        label: 'DNS',
      }],
    };
    item.typeValue = item.type[0];
    this.tempData.subjectAlternativeName.push(item);
  }

  removeSubjectAlternativeName(index: number): void {
    this.tempData.subjectAlternativeName.splice(index, 1);
  }

  cancelSubjectAlternativeNameSelected(): void {
    this.tempData.subjectAlternativeName = JSON.parse(JSON.stringify(this.savedData.subjectAlternativeName));
    this.closeSubjectAlternativeNameSelected();
  }

  saveSubjectAlternativeNameSelected(): void {
    this.closeSubjectAlternativeNameSelected();
    if (!this.containsType(this.SUBJECT_ALTERNATIVE_NAME)) {
      this.selectedExtensions.push({
        type: this.SUBJECT_ALTERNATIVE_NAME,
        name: 'Subject Alternative Name',
        critical: false,
      });
    }
    this.savedData.subjectAlternativeName = JSON.parse(JSON.stringify(this.tempData.subjectAlternativeName));
  }



  saveSubjectKeyIdentifierSelected(): void {
    if (!this.containsType(this.SUBJECT_KEY_IDENTIFIER)) {
      this.selectedExtensions.push({
        type: this.SUBJECT_KEY_IDENTIFIER,
        name: 'Subject Key Identifier',
        critical: false,
      });
    }
  }

  saveAuthorityKeyIdentifierSelected(): void {
    if (!this.containsType(this.AUTHORITY_KEY_IDENTIFIER)) {
      this.selectedExtensions.push({
        type: this.AUTHORITY_KEY_IDENTIFIER,
        name: 'Authority Key Identifier',
        critical: false,
      });
    }
  }

  saveAuthorityInfoAccessSelected(): void {
    if (!this.containsType(this.AUTHORITY_INFO_ACCESS)) {
      this.selectedExtensions.push({
        type: this.AUTHORITY_INFO_ACCESS,
        name: 'Authority Info Access',
        critical: false,
      });
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
    this.closeSubjectAlternativeNameSelected();
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
    else if (extension.type == this.SUBJECT_ALTERNATIVE_NAME) {
      this.tempData.subjectAlternativeName = JSON.parse(JSON.stringify(this.savedData.subjectAlternativeName));
      this.openSubjectAlternativeNameSelected();
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
    else if (extension.type === this.BASIC_CONSTRAINTS) {
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
    else if (extension.type === this.SUBJECT_ALTERNATIVE_NAME) {
      this.savedData.subjectAlternativeName = [
        {
          focused: false,
          value: '',
          typeValue: null,
          type: [{
            value: 'IP',
            label: 'IP',
          },
          {
            value: 'DNS',
            label: 'DNS',
          }],
        },
      ];
      this.tempData.subjectAlternativeName = [
        {
          focused: false,
          value: '',
          typeValue: null,
          type: [{
            value: 'IP',
            label: 'IP',
          },
          {
            value: 'DNS',
            label: 'DNS',
          }],
        },
      ];
      this.tempData.subjectAlternativeName[0].typeValue = this.tempData.subjectAlternativeName[0].type[0];
      this.selectedExtensions.splice(index, 1);
    }
    else {
      this.selectedExtensions.splice(index, 1);
    }
  }

}
