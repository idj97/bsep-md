package bsep.sc.SiemCenter.config;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    @Bean
    public KieBase kieBase() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("siem.center.kjar", "SiemCenterKjar", "0.0.1-SNAPSHOT"));

        KieBaseConfiguration kieBaseConfiguration = ks.newKieBaseConfiguration();
        kieBaseConfiguration.setOption(EventProcessingOption.STREAM);

        kContainer.newKieBase(kieBaseConfiguration);
        KieBase kieBase = kContainer.getKieBase();

        KieScanner kScanner = ks.newKieScanner(kContainer);
        kScanner.start(1000);
        return kieBase;
    }
}
