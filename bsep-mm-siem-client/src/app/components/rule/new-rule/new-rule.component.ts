import { Component, OnInit } from '@angular/core';
import 'brace/index';
import 'brace/mode/xml';
import 'brace/theme/monokai';
import { RuleService } from 'src/app/services/rule.service';
import { RuleDto } from 'src/app/dtos/rule.dto';
import { ToasterService } from 'src/app/services/toaster.service';


@Component({
  selector: 'app-new-rule',
  templateUrl: './new-rule.component.html',
  styleUrls: ['./new-rule.component.css']
})
export class NewRuleComponent implements OnInit {

  ruleData: RuleDto;
  readOnly: boolean = false;
  options: any = {maxLines: 1000, printMargin: false, showInvisibles: false}

  sendingRequest: boolean;

  constructor(
    private ruleService: RuleService,
    private toasterSvc: ToasterService
    ) { 
    this.ruleData = new RuleDto();
    this.sendingRequest = false;
  }

  ngOnInit() {

  }

  createRule(): void {

    this.sendingRequest = true;

    this.ruleService.createNewRule(this.ruleData).subscribe(
      data => {
        this.toasterSvc.showMessage('Info', data);
      },
      err => {
        this.toasterSvc.showErrorMessage(err);
      }
    ).add(
      () => {
        this.sendingRequest = false;
      }
    );
  }

  getSimpleTemplate(): void {

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
