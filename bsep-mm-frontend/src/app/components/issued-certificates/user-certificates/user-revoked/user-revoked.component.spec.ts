import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRevokedComponent } from './user-revoked.component';

describe('UserRevokedComponent', () => {
  let component: UserRevokedComponent;
  let fixture: ComponentFixture<UserRevokedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserRevokedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRevokedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
