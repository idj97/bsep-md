import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CaAllComponent } from './ca-all.component';

describe('CaAllComponent', () => {
  let component: CaAllComponent;
  let fixture: ComponentFixture<CaAllComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CaAllComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CaAllComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
