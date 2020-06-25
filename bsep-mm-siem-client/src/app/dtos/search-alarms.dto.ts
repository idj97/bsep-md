export class SearchAlarms {
    name: string;
    description: string;
    machineIp: string;
    machineOs: string;
    machinaName: string;
    agentInfo: string;
    lowerTimestamp: string;
    upperTimestamp: string;
    alarmType: string;
    timezone: string = "CET";

    pageNum: number = 0;
    pageSize: number = 30;
}
