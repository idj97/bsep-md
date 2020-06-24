export class SearchLogs {
    timestamp: string;
    machineIp: string;
    machineOS: string;
    machineName: string;
    eventId: string;
    eventName: string;
    eventType: string;
    message: string;
    logSource: string;
    rawText: string;
    source: string;
    sourceIp: string;
    sourcePort: string;
    protocol: string;
    action: string;
    command: string;
    workingDir: string;
    sourceUser: string;
    targetUser: string;
    lowerGenericTimestamp: string;
    upperGenericTimestamp: string;
    lowerRecievedAt: string;
    upperRecievedAt: string;

    pageNum: number = 0;
    pageSize: number = 30;
    timezone: String = 'CET';

}
