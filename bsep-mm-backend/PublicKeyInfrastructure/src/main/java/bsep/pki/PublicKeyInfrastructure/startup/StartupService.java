package bsep.pki.PublicKeyInfrastructure.startup;

import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.service.RootCAService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.security.Security;

@Service
public class StartupService {

    @Autowired
    private KeyStoreService keystoreService;

    @Autowired
    private RootCAService rootCAService;

    @EventListener
    public void onStartup(ContextRefreshedEvent contextRefreshedEvent) {
        configure();
        initialize();
    }

    public void configure() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public void initialize() {
        keystoreService.tryCreateKeyStore();
        rootCAService.tryCreateRootCA();
    }
}
