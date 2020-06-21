import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CACertificatesComponent } from './ca-certificates.component';

describe('CACertificatesComponent', () => {
  let component: CACertificatesComponent;
  let fixture: ComponentFixture<CACertificatesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CACertificatesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CACertificatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
