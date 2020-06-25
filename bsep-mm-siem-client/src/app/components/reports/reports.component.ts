import { Component, OnInit } from '@angular/core';
import { ReportsService } from 'src/app/services/reports.service';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

  public totalLogs: number = 0;
  public logsLastMonth: any[] = [];
  public totalErrorLogs: number = 0;
  public totalInformationalLogs: number = 0;
  public totalWarningLogs: number = 0;
  public totalNALogs: number = 0;
  public monthlyReport = {};
  public totalLogsOperatingSystems = 0;
  public totalLogsMachines = 0;

  constructor(private reportsService: ReportsService) { }

  ngOnInit() {
    this.getTotalLogs();
    this.getLogsLastMonth();
    this.getMonthlyReport();
    Chart.defaults.global.defaultFontColor = '#fff';
  }

  getTotalLogs(): void {
    this.reportsService.getTotalLogs().subscribe(
      data => {
        this.totalLogs = data;
      },
      error => {
        console.log(error);
      }
    )
  }

  getLogsLastMonth(): void {
    this.reportsService.getLogsLastMonth().subscribe(
      data => {
        this.logsLastMonth = data;
        this.totalErrorLogs = data.filter(item => (<string>item.eventType).toLowerCase() === 'error').length;
        this.totalInformationalLogs = data.filter(item => (<string>item.eventType).toLowerCase() === 'informational').length;
        this.totalWarningLogs = data.filter(item => (<string>item.eventType).toLowerCase() === 'warning').length;
        this.totalNALogs = data.filter(item => (<string>item.eventType).toLowerCase() === '').length;
        this.pieChartData = [this.totalErrorLogs, this.totalInformationalLogs, this.totalWarningLogs, this.totalNALogs];

        let machineOSLabels = [];
        this.logsLastMonth.forEach(item => {
          if (!machineOSLabels.includes(item.machineOS)) {
            machineOSLabels.push(item.machineOS);
          }
        });
        this.mOSbarChartLabels = machineOSLabels;
        let barChartData = []
        for (let i = 0; i < machineOSLabels.length; i++) {
          let total = 0;
          for (let j = 0; j < this.logsLastMonth.length; j++) {
            if (machineOSLabels[i] == this.logsLastMonth[j].machineOS) {
              total++;
            }
          }
          barChartData.push(total);
        }
        this.mOSbarChartData[0].data = barChartData;

        let machinesLabels = [];
        this.logsLastMonth.forEach(item => {
          if (!machinesLabels.includes(item.machineIp)) {
            machinesLabels.push(item.machineIp);
          }
        });
        this.clientbarChartLabels = machinesLabels;
        let machinesbarChartData = []
        for (let i = 0; i < machinesLabels.length; i++) {
          let total = 0;
          for (let j = 0; j < this.logsLastMonth.length; j++) {
            if (machinesLabels[i] == this.logsLastMonth[j].machineIp) {
              total++;
            }
          }
          machinesbarChartData.push(total);
        }
        this.clientbarChartData[0].data = machinesbarChartData;
        this.totalLogsOperatingSystems = machineOSLabels.length;
        this.totalLogsMachines = machinesLabels.length;


      },
      error => {
        console.log(error);
      }
    )
  }

  getMonthlyReport() {
    this.reportsService.getMonthlyReport().subscribe(
      data => {
        console.log(data);
        let monthlyData = [
          {
            data: (<any[]>data).map(item => item.numOfLogs),
            label: 'Total Logs per Day',
            lineTension: 0
          },
        ];
        this.lineChartData = monthlyData;
        this.lineChartLabels = (<any[]>data).map(item => item.day);
      },
      error => {
        console.log(error);
      }
    )
  }

  //BAR CHART PER MACHINE OS
  public mOSbarChartOptions = {
    scaleShowVerticalLines: false,
    responsive: true
  };

  public mOSbarChartLabels = [''];
  public mOSbarChartType = 'bar';
  public mOSbarChartLegend = true;
  public mOSbarChartData = [
    {data: [0], label: 'Total Logs last month per Operating System'},
  ];
  public mOSchartColors: any[] = [
    { 
      backgroundColor:"#46cfa1",
      pointHoverBackgroundColor: '#FFFFFF',
    }
  ];

  
  //BAR CHART PER CLIENT
  public clientbarChartOptions = {
    scaleShowVerticalLines: false,
    responsive: true
  };

  public clientbarChartLabels = [''];
  public clientbarChartType = 'bar';
  public clientbarChartLegend = true;
  public clientbarChartData = [
    {data: [0], label: 'Total Logs last month per Machine'},
  ];
  public clientchartColors: any[] = [
    { 
      backgroundColor:"#46cfa1",
      pointHoverBackgroundColor: '#FFFFFF',
    }
  ];


  //PIE CHART
  public pieChartOptions = {
    responsive: true,
    legend: {
      position: 'top',
      fontColor: '#FFF',
    },
    plugins: {
      datalabels: {
        formatter: (value, ctx) => {
          const label = ctx.chart.data.labels[ctx.dataIndex];
          return label;
        },
      },
    }
  };
  public pieChartLabels = ['Error', 'Informational', 'Warning', 'N/A'];
  public pieChartData: number[] = [0, 0, 0, 0];
  public pieChartType = 'pie';
  public pieChartLegend = true;
  public pieChartColors = [
    {
      backgroundColor: ['#b82549', '#46cfa1', '#e38839', '#bdbdbd'],
      pointHoverBackgroundColor: '#FFFFFF',
    },
  ];





  public lineChartData = [
    { data: [0], label: '' }
  ];
  public lineChartLabels = [''];
  public lineChartLegend = true;
  public lineChartType = 'line';
  public lineChartOptions = {

  }
  public lineChartColors = [
    { // grey
      backgroundColor: 'rgba(90, 199, 163, 0.1)',
      borderColor: 'rgba(90, 199, 163, 1)',
    }
  ];

  

}
