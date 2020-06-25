import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-rule',
  templateUrl: './rule.component.html',
  styleUrls: ['./rule.component.css']
})
export class RuleComponent implements OnInit {

  selectedMenuItem: number; // identifies the menu item

  constructor(private router: Router) { }

  ngOnInit() {
    this.changeMenuColor();
  }

  changeMenuColor() {

    if (this.router.url.includes('rules/view')) {
      this.selectedMenuItem = 0;
    } else {
      this.selectedMenuItem = 1;
    }

  }

  changeSelectedMenu(id: number) {
    this.selectedMenuItem = id;
  }


}
