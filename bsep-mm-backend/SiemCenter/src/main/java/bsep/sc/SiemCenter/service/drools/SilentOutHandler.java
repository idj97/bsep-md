package bsep.sc.SiemCenter.service.drools;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

import java.io.IOException;

public class SilentOutHandler implements InvocationOutputHandler {

    @Override
    public void consumeLine(String s) throws IOException {

    }
}
