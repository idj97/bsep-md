import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LogEventsComponent } from './components/log-events/log-events.component';
import { AlarmEventsComponent } from './components/alarm-events/alarm-events.component';
import { AlarmsComponent } from './components/alarms/alarms.component';
import { ReportsComponent } from './components/reports/reports.component';


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
    path: 'alarms',
    component: AlarmsComponent
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
