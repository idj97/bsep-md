package bsep.sa.SiemAgent.readers;

import bsep.sa.SiemAgent.model.Log;
import bsep.sa.SiemAgent.model.LogSource;
import bsep.sa.SiemAgent.model.LogPattern;
import bsep.sa.SiemAgent.service.LogSenderScheduler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.*;

public class WindowsLogReader implements Runnable {

    private LogSource logFile;
    private LogSenderScheduler logSenderScheduler;

    private WinNT.EVENTLOGRECORD newestRecord;

    public WindowsLogReader(LogSource logfile, LogSenderScheduler logSenderScheduler) {
        this.logFile = logfile;
        this.logSenderScheduler = logSenderScheduler;
    }

    @Override
    public void run() {
        System.out.println("Started reader for " + logFile.getSource());
        while (true) {
            try {
                new Advapi32Util.EventLogIterator(logFile.getSource());
            }
            catch (Exception e) {
                System.out.println("A required privilege is not held by the client when accessing " + logFile.getSource() + ".");
                break;
            }
            WinNT.EVENTLOGRECORD tempRecord = this.getNewestEvent(logFile.getSource());

            if (newestRecord == null || !tempRecord.dataEquals(newestRecord)) {
                newestRecord = tempRecord;
                String logString = createLogLine(newestRecord);
                System.out.println(logString);
                List<Log> logs = parse(logString);
                logs.stream().forEach(log -> logSenderScheduler.addLog(log));
                continue;
            }

            try {
                Thread.sleep(logFile.getReadFrequency());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private String createLogLine(WinNT.EVENTLOGRECORD record) {
        Advapi32Util.EventLogRecord eRecord = new Advapi32Util.EventLogRecord(record.getPointer());

        Date date = new Date(eRecord.getRecord().TimeGenerated.longValue() * 1000);
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd HH:mm:ss");
        String logString = String.format(
                "%s %s %s %s ; %s ; %s %s",
                format.format(date),
                extractMachineName(eRecord.getRecord()),
                eRecord.getEventId(),
                eRecord.getType(),
                eRecord.getSource(),
                eRecord.getStatusCode(),
                extractMessageFromEventLog(eRecord.getRecord()));

        return logString;
    }

    private WinNT.EVENTLOGRECORD getNewestEvent(String sourceName) {
        WinNT.HANDLE h = Advapi32.INSTANCE.OpenEventLog(null, sourceName);
        IntByReference pnBytesRead = new IntByReference();
        IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
        Memory buffer = new Memory(1024 * 64);
        IntByReference pOldestRecord = new IntByReference();
        //Advapi32.INSTANCE.GetOldestEventLogRecord(h, pOldestRecord);
        int dwRecord = pOldestRecord.getValue();
        int rc = 0;

        Advapi32.INSTANCE.ReadEventLog(h,
                WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_BACKWARDS_READ,
                0, buffer, (int) buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded);

        int dwRead = pnBytesRead.getValue();
        Pointer pevlr = buffer;

        WinNT.EVENTLOGRECORD record = new WinNT.EVENTLOGRECORD(pevlr);

        return record;
    }

    private String extractMessageFromEventLog(WinNT.EVENTLOGRECORD record) {
        Advapi32Util.EventLogRecord eRecord = new Advapi32Util.EventLogRecord(record.getPointer());
        String[] recordStrings = eRecord.getStrings();
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < recordStrings.length; i++) {
            message.append(recordStrings[i]);
            if (i != recordStrings.length - 1) message.append(" ");
        }

        return message.toString();
    }

    private String extractMachineName(WinNT.EVENTLOGRECORD record) {
        Pointer pointer = record.getPointer();

        if (record.size() > 0) {
            ByteBuffer names = pointer.getByteBuffer(record.size(),
                    (record.UserSidLength.intValue() != 0 ? record.UserSidOffset.intValue() : record.StringOffset.intValue()) - record.size());
            names.position(0);
            CharBuffer namesBuf = names.asCharBuffer();
            String[] splits = namesBuf.toString().split("\0");
            return splits[1];
        }

        return null;



    }

    public List<Log> parse(String line) {
        Gson gson = new Gson();
        GrokCompiler grokCompiler = GrokCompiler.newInstance();
        grokCompiler.registerDefaultPatterns();

        List<Log> logs = new LinkedList<>();
        Log templateLog = new Log();
        Type typeMap = new TypeToken<Map<String, String>>(){}.getType();

        for (LogPattern logPattern : logFile.getLogPatterns()) {
            Map<String, Object> logMap = gson.fromJson(gson.toJson(templateLog), typeMap);
            Grok grok = grokCompiler.compile(logPattern.getPattern());
            Match gm = grok.match(line);
            Map<String, Object> capturedFields = gm.capture();

            for (String logField : logMap.keySet()) {
                if (capturedFields.containsKey(logField)) {
                    logMap.put(logField, capturedFields.get(logField));
                }
            }

            logMap.put("genericTimestamp", new Date().getTime());
            logMap.put("message", line);

            Log log = gson.fromJson(gson.toJson(logMap, typeMap), Log.class);
            logs.add(log);
        }
        return logs;
    }


}
