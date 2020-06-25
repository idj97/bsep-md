import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LogEventsComponent } from './components/log-events/log-events.component';
import { AlarmEventsComponent } from './components/alarm-events/alarm-events.component';
import { ReportsComponent } from './components/reports/reports.component';
import { RuleComponent } from './components/rule/rule.component';
import { ViewRulesComponent } from './components/rule/view-rules/view-rules.component';
import { NewRuleComponent } from './components/rule/new-rule/new-rule.component';


const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'log-events',
    component: LogEventsComponent
  },
  {
    path: 'alarm-events',
    component: AlarmEventsComponent
  },
  {
    path: 'rules',
    component: RuleComponent,
    children: [
      {
        path: '',
        redirectTo: 'view',
        pathMatch: 'full'
      },
      {
        path: 'view',
        component: ViewRulesComponent
      },
      {
        path: 'new',
        component: NewRuleComponent
      }
    ]
  },
  {
    path: 'reports',
    component: ReportsComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
