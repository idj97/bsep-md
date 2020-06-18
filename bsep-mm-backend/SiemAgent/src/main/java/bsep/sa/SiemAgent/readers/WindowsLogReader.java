package bsep.sa.SiemAgent.readers;

import bsep.sa.SiemAgent.model.Log;
import bsep.sa.SiemAgent.model.LogFile;
import bsep.sa.SiemAgent.model.LogPattern;
import bsep.sa.SiemAgent.service.LogSenderScheduler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WindowsLogReader implements Runnable {

    private LogFile logFile;
    private LogSenderScheduler logSenderScheduler;

    private WinNT.EVENTLOGRECORD newestRecord;

    public WindowsLogReader(LogFile logfile, LogSenderScheduler logSenderScheduler) {
        this.logFile = logfile;
        this.logSenderScheduler = logSenderScheduler;
    }

    @Override
    public void run() {

        while (true) {
            WinNT.EVENTLOGRECORD tempRecord = this.getNewestEvent(logFile.getPath());
            System.out.println(this.extractMessageFromEventLog(tempRecord));
            //System.out.println(new Advapi32Util.EventLogRecord(tempRecord.getPointer()).getType());
            if (newestRecord == null || !tempRecord.dataEquals(newestRecord)) {
                newestRecord = tempRecord;



                Log log = new Log();

            }


            try {
                Thread.sleep(logFile.getReadFrequency());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

        /*int dwMessage = dwRead - record.StringOffset.intValue();
        int dxM = record.NumStrings.intValue();

        System.out.println("-----------------------");
        String sss = pevlr.getWideString(record.StringOffset.intValue());
        StringBuilder stringBuilder = new StringBuilder();

        //pevlr = pevlr.share(record.StringOffset.intValue());
        int incrementedOffset = 0;
        for (int i = 0; i < record.NumStrings.intValue(); i++) {
            String param = pevlr.getWideString( record.StringOffset.intValue() + incrementedOffset);
            incrementedOffset += param.length();
        }

        System.out.println(stringBuilder.toString());
        System.out.println("=========================");
*/
        return record;
    }

    private String extractMessageFromEventLog(WinNT.EVENTLOGRECORD record) {
        Pointer pointer = record.getPointer();
        StringBuilder message = new StringBuilder();
        int incrementedOffset = 0;
        for (int i = 0; i < record.NumStrings.intValue(); i++) {
            String param = pointer.getWideString( record.StringOffset.longValue() + incrementedOffset);
            incrementedOffset += param.length() * Native.WCHAR_SIZE + Native.WCHAR_SIZE;
            message.append(param);
            if (i != record.NumStrings.intValue() - 1) message.append(" ");
        }

        return message.toString();
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

            Log log = gson.fromJson(gson.toJson(logMap, typeMap), Log.class);
            logs.add(log);
        }
        return logs;
    }


}
