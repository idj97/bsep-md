import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CaRevokedComponent } from './ca-revoked.component';

describe('CaRevokedComponent', () => {
  let component: CaRevokedComponent;
  let fixture: ComponentFixture<CaRevokedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CaRevokedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CaRevokedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
