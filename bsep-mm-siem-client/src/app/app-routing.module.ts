import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LogEventsComponent } from './components/log-events/log-events.component';
import { AlarmEventsComponent } from './components/alarm-events/alarm-events.component';
import { ReportsComponent } from './components/reports/reports.component';
import { RuleComponent } from './components/rule/rule.component';
import { ViewRulesComponent } from './components/rule/view-rules/view-rules.component';
import { NewRuleComponent } from './components/rule/new-rule/new-rule.component';
import { AuthenticationGuard } from './keycloak/authentication.guard';


const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthenticationGuard],
    data: {roles: ['admin', 'operator']}
  },
  {
    path: 'log-events',
    component: LogEventsComponent,
    canActivate: [AuthenticationGuard],
    data: {roles: ['admin', 'operator']}
  },
  {
    path: 'alarm-events',
    component: AlarmEventsComponent,
    canActivate: [AuthenticationGuard],
    data: {roles: ['admin', 'operator']}
  },
  {
    path: 'rules',
    component: RuleComponent,
    canActivate: [AuthenticationGuard],
    data: {roles: ['admin']},
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
    component: ReportsComponent,
    canActivate: [AuthenticationGuard],
    data: {roles: ['admin', 'operator']}
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
