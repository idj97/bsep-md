import { Component, OnInit } from '@angular/core';
import { RuleService } from 'src/app/services/rule.service';
import { RuleDto } from 'src/app/dtos/rule.dto';
import { ToasterService } from 'src/app/services/toaster.service';

import * as ace from 'ace-builds';

import 'brace/index';
import 'brace/mode/xml';
import 'brace/theme/monokai';


@Component({
  selector: 'app-new-rule',
  templateUrl: './new-rule.component.html',
  styleUrls: ['./new-rule.component.css']
})
export class NewRuleComponent implements OnInit {

  ruleData: RuleDto;
  readOnly: boolean = false;
  options: any = {maxLines: 1000, showInvisibles: false}

  sendingRequest: boolean = false;
  errorMessage: string = '';

  constructor(
    private ruleService: RuleService,
    private toasterSvc: ToasterService
    ) { 
    this.ruleData = new RuleDto();
  }

  ngOnInit() {

  }

  get EditorMode() {
    var oop = ace.require("ace/lib/oop");
    var TextMode = ace.require("ace/mode/text").Mode;
    var TextHighlightRules = ace.require("ace/mode/text_highlight_rules").TextHighlightRules;

    var CustomHighlightRules = function(){
        this.$rules = {
            'start': [
                {
                  regex: /\b(package|import|rule|when|then|end|no-loop|enabled|insert|modify|not|from|accumulate|over|window:time|window:length|global|timer|update|delete|agenda-group|exists|collect|contains|forAll|forall|this|new|lock-on-active|salience|declare|@Watch|null|matches|memberOf)\b/,
                  token: 'keyword',
                },
                {
                  regex: /(["'])(.*?[^\\])\1/,
                  token: 'variable',
                },
                {
                  regex: /\$\w+/,
                  token: 'variable',
                }
            ],
        };
    };

    oop.inherits(CustomHighlightRules, TextHighlightRules);

    var Mode = function() {
        this.HighlightRules = CustomHighlightRules;
    };
    oop.inherits(Mode, TextMode);

    return new Mode;

  }
  

  createRule(): void {

    this.sendingRequest = true;
    this.readOnly = true;

    this.ruleService.createNewRule(this.ruleData).subscribe(
      data => {
        this.errorMessage = 'Rule successfully added';
        this.ruleData.ruleContent = '';
      },
      err => {
        if (err.error.errors != null) { // validation exception
          this.errorMessage = err.error.errors[0].defaultMessage;
        } else { // other exception
          this.errorMessage = err.error.message;
        }
        
      }
    ).add(
      () => {
        this.sendingRequest = false;
        this.readOnly = false;
      }
    );
  }

  getSimpleTemplate(): void {

    console.log(this.ruleData);

    this.ruleService.getSimpleTemplate().subscribe(
      data => {
        this.ruleData.ruleContent = data;
      },
      err => {
        this.toasterSvc.showErrorMessage(err);
      }
    );
  }

  getCepTemplate(): void {

    this.ruleService.getCepTemplate().subscribe(
      data => {
        this.ruleData.ruleContent = data;
      },
      err => {
        this.toasterSvc.showErrorMessage(err);
      }
    );
  }

}
