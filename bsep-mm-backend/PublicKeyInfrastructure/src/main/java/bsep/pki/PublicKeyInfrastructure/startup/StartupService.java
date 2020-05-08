package bsep.pki.PublicKeyInfrastructure.startup;

import bsep.pki.PublicKeyInfrastructure.dto.RevocationDto;
import bsep.pki.PublicKeyInfrastructure.model.enums.CAType;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import bsep.pki.PublicKeyInfrastructure.model.enums.RevokeReason;
import bsep.pki.PublicKeyInfrastructure.service.CAService;
import bsep.pki.PublicKeyInfrastructure.service.CRLService;
import bsep.pki.PublicKeyInfrastructure.service.CertificateService;
import bsep.pki.PublicKeyInfrastructure.service.RootCAService;
import bsep.pki.PublicKeyInfrastructure.utility.DateService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private CAService caService;

    @Autowired
    private CRLService crlService;

    @Value("${app.init}")
    private Boolean initApp;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private DateService dateService;

    @EventListener
    public void onStartup(ContextRefreshedEvent contextRefreshedEvent) {
        configure();
        initialize();
    }

    public void configure() {
        Security.addProvider(new BouncyCastleProvider());
    }

    // TODO: comment out
    public void initialize() {
        if (initApp) {
            keystoreService.tryCreateKeyStore();
            rootCAService.tryCreateRootCA();

            caService.tryCreateCA(1L, CAType.SIEM_AGENT_ISSUER, CertificateType.SIEM_AGENT_ISSUER);
            caService.tryCreateCA(1L, CAType.SIEM_CENTER_ISSUER, CertificateType.SIEM_CENTER_ISSUER);

            crlService.createCRL();
            //crlService.revokeCertificate(new RevocationDto(null, 1L, RevokeReason.KEY_COMPROMISE, null));
            //crlService.revokeCertificate(new RevocationDto(null, 2L, RevokeReason.PRIVILEGE_WITHDRAWN, null));
        }
    }
}
