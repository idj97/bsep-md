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

  public totalAlarms: number = 0;
  public alarmsLastMonth: any[] = [];
  public totalErrorAlarms: number = 0;
  public totalInformationalAlarms: number = 0;
  public totalWarningAlarms: number = 0;
  public totalNAAlarms: number = 0;
  public monthlyAlarmsReport = {};
  public totalAlarmsOperatingSystems = 0;
  public totalAlarmsMachines = 0;

  constructor(private reportsService: ReportsService) { }

  ngOnInit() {
    this.getTotalLogs();
    this.getLogsLastMonth();
    this.getMonthlyReport();

    this.getTotalAlarms();
    this.getAlarmsLastMonth();
    this.getMonthlyAlarmsReport();

    Chart.defaults.global.defaultFontColor = '#fff';
  }

  getTotalAlarms(): void {
    this.reportsService.getTotalAlarms().subscribe(
      data => {
        this.totalAlarms = data;
      },
      error => {
        console.log(error);
      }
    )
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

  getAlarmsLastMonth(): void {
    this.reportsService.getAlarmsLastMonth().subscribe(
      data => {
        console.log(data);
        this.alarmsLastMonth = data;
        let alarmTypes = [];
        for (let i = 0; i < data.length; i++) {
          if (!alarmTypes.includes(data[i].alarmType)) {
            if (data[i].alarmType === null && !alarmTypes.includes('N/A')) {
              alarmTypes.push("N/A");
            }
            else {
              if (data[i].alarmType === null) {
                continue;
              }
              alarmTypes.push(data[i].alarmType);
            }
          }
        }

        for (let i = 0; i < alarmTypes.length; i++) {
          this.alarmspieChartColors[0].backgroundColor[i] = `rgba(${Math.floor(Math.random()* 256)},${Math.floor(Math.random()* 256)},${Math.floor(Math.random()* 256)},1)`;
        }

        console.log(alarmTypes);
        let alarmsPieChartData = [];

        for (let i = 0; i < alarmTypes.length; i++) {
          let counter = 0;
          for (let j = 0; j < data.length; j++) {
            let type = data[j].alarmType === null ? 'N/A' : data[j].alarmType;
            if (type == alarmTypes[i]) {
              counter++;
            }
          }
          alarmsPieChartData.push(counter);
        }

        this.alarmspieChartData = alarmsPieChartData;
        this.alarmspieChartLabels = alarmTypes;


        let machineOSLabels = [];
        this.alarmsLastMonth.forEach(item => {
          if (!machineOSLabels.includes(item.machineOS)) {
            machineOSLabels.push(item.machineOS);
          }
        });
        this.alarmsmOSbarChartLabels = machineOSLabels;
        let barChartData = []
        for (let i = 0; i < machineOSLabels.length; i++) {
          let total = 0;
          for (let j = 0; j < this.alarmsLastMonth.length; j++) {
            if (machineOSLabels[i] == this.alarmsLastMonth[j].machineOS) {
              total++;
            }
          }
          barChartData.push(total);
        }
        this.alarmsmOSbarChartData[0].data = barChartData;
        

        let machinesLabels = [];
        this.alarmsLastMonth.forEach(item => {
          if (!machinesLabels.includes(item.machineIp)) {
            machinesLabels.push(item.machineIp);
          }
        });
        this.alarmsclientbarChartLabels = machinesLabels;
        let machinesbarChartData = []
        for (let i = 0; i < machinesLabels.length; i++) {
          let total = 0;
          for (let j = 0; j < this.alarmsLastMonth.length; j++) {
            if (machinesLabels[i] == this.alarmsLastMonth[j].machineIp) {
              total++;
            }
          }
          machinesbarChartData.push(total);
        }
        this.alarmsclientbarChartData[0].data = machinesbarChartData;

        this.totalAlarmsMachines = machinesLabels.length;
        this.totalAlarmsOperatingSystems = machineOSLabels.length;


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

  getMonthlyAlarmsReport() {
    this.reportsService.getMonthlyAlarmsReport().subscribe(
      data => {
        console.log(data);
        let monthlyData = [
          {
            data: (<any[]>data).map(item => item.numOfAlarms),
            label: 'Total Alarms per Day',
            lineTension: 0
          },
        ];
        this.alarmslineChartData = monthlyData;
        this.alarmslineChartLabels = (<any[]>data).map(item => item.day);
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

  //ALARMS --------------------------------------------------------------------------------------------

  public alarmslineChartData = [
    { data: [0], label: '' }
  ];
  public alarmslineChartLabels = [''];
  public alarmslineChartLegend = true;
  public alarmslineChartType = 'line';
  public alarmslineChartOptions = {

  }
  public alarmslineChartColors = [
    { // grey
      backgroundColor: 'rgba(90, 199, 163, 0.1)',
      borderColor: 'rgba(90, 199, 163, 1)',
    }
  ];

  //PIE CHART
  public alarmspieChartOptions = {
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
  public alarmspieChartLabels = [];
  public alarmspieChartData: number[] = [];
  public alarmspieChartType = 'pie';
  public alarmspieChartLegend = true;
  public alarmspieChartColors = [
    {
      backgroundColor: ['#46cfa1'],
      pointHoverBackgroundColor: '#FFFFFF',
    },
  ];

  //BAR CHART PER MACHINE OS
  public alarmsmOSbarChartOptions = {
    scaleShowVerticalLines: false,
    responsive: true
  };

  public alarmsmOSbarChartLabels = [''];
  public alarmsmOSbarChartType = 'bar';
  public alarmsmOSbarChartLegend = true;
  public alarmsmOSbarChartData = [
    {data: [0], label: 'Total Alarms last month per Operating System'},
  ];
  public alarmsmOSchartColors: any[] = [
    { 
      backgroundColor:"#46cfa1",
      pointHoverBackgroundColor: '#FFFFFF',
    }
  ];

  //BAR CHART PER CLIENT
  public alarmsclientbarChartOptions = {
    scaleShowVerticalLines: false,
    responsive: true
  };

  public alarmsclientbarChartLabels = [''];
  public alarmsclientbarChartType = 'bar';
  public alarmsclientbarChartLegend = true;
  public alarmsclientbarChartData = [
    {data: [0], label: 'Total Alarms last month per Machine'},
  ];
  public alarmsclientchartColors: any[] = [
    { 
      backgroundColor:"#46cfa1",
      pointHoverBackgroundColor: '#FFFFFF',
    }
  ];


  

}
