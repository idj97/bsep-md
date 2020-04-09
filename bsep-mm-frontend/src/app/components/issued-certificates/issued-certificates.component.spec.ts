import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IssuedCertificatesComponent } from './issued-certificates.component';

describe('IssuedCertificatesComponent', () => {
  let component: IssuedCertificatesComponent;
  let fixture: ComponentFixture<IssuedCertificatesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IssuedCertificatesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IssuedCertificatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
