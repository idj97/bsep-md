import { Component, OnInit } from '@angular/core';
import { Rule } from 'src/app/dtos/rule.dto';
import { RuleService } from 'src/app/services/rule.service';
import { ToasterService } from 'src/app/services/toaster.service';

@Component({
  selector: 'app-view-rules',
  templateUrl: './view-rules.component.html',
  styleUrls: ['./view-rules.component.css']
})
export class ViewRulesComponent implements OnInit {

  private rules: Array<Rule>;

  constructor(
    private ruleService: RuleService,
    private toasterSvc: ToasterService
    ) {
    this.rules = new Array<Rule>();
  }

  ngOnInit() {
    this.getAllRules();
  }

  getAllRules(): void {
    this.ruleService.getAllRules().subscribe(
      data => {
        this.rules = data;
      },
      err => {
        this.toasterSvc.showErrorMessage(err);
      }
    );
  }


}