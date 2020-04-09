package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.utility.CertificateGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

    @Autowired
    CertificateGenerationService certificateGenSvc;

    // TODO: create root certificate (set validity period)
    public Certificate createSelfSignedSertificate(CertificateDto certificateDto) {
        return null;
    }

    // TODO: create subordinate certificate (set validity period)

    // TODO: create end user certificate (set validity period)

}
