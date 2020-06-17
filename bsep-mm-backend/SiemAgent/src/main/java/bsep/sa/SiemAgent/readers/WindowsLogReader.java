package bsep.sa.SiemAgent.readers;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.util.Date;

public class WindowsLogReader implements Runnable {

    private WinNT.EVENTLOGRECORD newestRecord;

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

    @Override
    public void run() {

    }
}
