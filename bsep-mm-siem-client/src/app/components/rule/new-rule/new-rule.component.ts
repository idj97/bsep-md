import { Component, OnInit } from '@angular/core';
import 'brace/index';
import 'brace/mode/xml';
import 'brace/theme/monokai';


@Component({
  selector: 'app-new-rule',
  templateUrl: './new-rule.component.html',
  styleUrls: ['./new-rule.component.css']
})
export class NewRuleComponent implements OnInit {

  ruleContent: string = '';
  readOnly: boolean = false;
  options: any = {maxLines: 1000, printMargin: false, showInvisibles: false}

  constructor() { 
    
  }

  ngOnInit() {

  }

}
