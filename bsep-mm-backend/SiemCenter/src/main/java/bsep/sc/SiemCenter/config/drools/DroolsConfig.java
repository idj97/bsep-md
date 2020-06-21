package bsep.sc.SiemCenter.config.drools;

import bsep.sc.SiemCenter.util.KieSessionTemplate;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    @Bean
    public KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    public KieBaseConfiguration getKieBaseConfiguration() {

        KieBaseConfiguration config = getKieServices().newKieBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        return config;
    }

    @Bean
    public KieSessionTemplate getKieSessionTemplate() {
        return new KieSessionTemplate();
    }
}
