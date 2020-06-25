import { RuleComponent } from './rule.component';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ViewRulesComponent } from './view-rules/view-rules.component';
import { NewRuleComponent } from './new-rule/new-rule.component';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { HttpClientModule } from '@angular/common/http';
import { AceEditorModule } from 'ng2-ace-editor';
import { FormsModule } from '@angular/forms';


@NgModule({
    declarations: [
      RuleComponent,
      ViewRulesComponent,
      NewRuleComponent
    ],
    imports: [
      BrowserModule,
      AppRoutingModule,
      HttpClientModule,
      AceEditorModule,
      FormsModule
    ]
  })
  
export class RuleModule { }