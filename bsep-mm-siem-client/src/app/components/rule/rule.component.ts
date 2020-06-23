import { Component, OnInit, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-rule',
  templateUrl: './rule.component.html',
  styleUrls: ['./rule.component.css']
})
export class RuleComponent implements OnInit {

  defaultClicked: boolean; // default menu button clicked

  constructor() { }

  ngOnInit() {
    this.defaultClicked = true;
  }

  menuClicked(value: boolean) {
    this.defaultClicked = value;
  }

}
