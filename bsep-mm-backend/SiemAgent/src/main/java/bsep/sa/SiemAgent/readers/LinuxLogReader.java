package bsep.sa.SiemAgent.readers;

import bsep.sa.SiemAgent.model.Log;
import bsep.sa.SiemAgent.model.LogFile;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Setter
public class LinuxLogReader implements Runnable {
    private LogFile logFile;

    public LinuxLogReader(LogFile logFile) {
        super();
        this.logFile = logFile;
    }

    @Override
    public void run() {
        System.out.println("Started reader for" + logFile.getPath());
        try {
            FileReader fr = new FileReader(logFile.getPath());
            BufferedReader br = new BufferedReader(fr);
            jumpToEnd(br);

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    Thread.sleep(logFile.getReadFrequency());
                } else {
                    System.out.println(line);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void jumpToEnd(BufferedReader br) throws IOException {
        while (br.readLine() != null) {}
    }
}
