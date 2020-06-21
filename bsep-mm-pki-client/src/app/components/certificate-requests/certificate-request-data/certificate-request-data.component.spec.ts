import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificateRequestDataComponent } from './certificate-request-data.component';

describe('CertificateRequestDataComponent', () => {
  let component: CertificateRequestDataComponent;
  let fixture: ComponentFixture<CertificateRequestDataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CertificateRequestDataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CertificateRequestDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
