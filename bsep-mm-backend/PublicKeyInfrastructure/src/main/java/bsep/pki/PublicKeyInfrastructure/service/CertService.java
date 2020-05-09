package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.config.AlgorithmsConfig;
import bsep.pki.PublicKeyInfrastructure.data.X509CertificateWithKeys;
import bsep.pki.PublicKeyInfrastructure.dto.CertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.CreateCertificateDto;
import bsep.pki.PublicKeyInfrastructure.dto.extensions.AbstractExtensionDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiInternalServerErrorException;
import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequest;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRepository;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateRequestRepository;
import bsep.pki.PublicKeyInfrastructure.utility.DateService;
import bsep.pki.PublicKeyInfrastructure.utility.KeyStoreService;
import bsep.pki.PublicKeyInfrastructure.utility.UriService;
import bsep.pki.PublicKeyInfrastructure.utility.X500Service;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CertService {
    @Autowired
    private UriService uriService;

    @Autowired
    private DateService dateService;

    @Autowired
    private X500Service x500Service;

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateRequestRepository csrRepository;

    public CertificateDto create(CreateCertificateDto dto) {
        verifyCreateCertificate(dto);

        // create cert, chain and key pair
        X509CertificateWithKeys x509certificateWithKeys = createX509Certificate(dto);

        // create entity
        Certificate certificateEntity = createCertificateEntity(dto, x509certificateWithKeys.getX509Certificate());

        // save private key and chain
        keyStoreService.saveEntry(
                x509certificateWithKeys.getX509CertificatesChain(),
                x509certificateWithKeys.getPrivateKey(),
                dto.getSerialNumber());

        // save entity
        certificateEntity = certificateRepository.save(certificateEntity);
        return new CertificateDto(certificateEntity);
    }

    public X509CertificateWithKeys createX509Certificate(CreateCertificateDto dto) {
        X500Name subjectName = dto.getName().getBCX500Name();
        X500Name issuerName;
        if (dto.getSelfSigned()) issuerName = subjectName;
        else                     issuerName = x500Service.getX500Name(dto.getIssuingCaSerialNumber());

        Date validFrom  = dateService.getDate(dto.getValidFrom());
        Date validUntil = dateService.getDate(dto.getValidUntil());

        PublicKey subjectPublicKey;
        PrivateKey subjectPrivateKey;
        if (dto.getCsrId() == null) {
            KeyPair subjectKeyPair = x500Service.generateKeyPair(dto.getKeyGenerationAlgorithm(), dto.getKeySize());
            subjectPublicKey = subjectKeyPair.getPublic();
            subjectPrivateKey = subjectKeyPair.getPrivate();
        } else {
            subjectPublicKey = (PublicKey) keyStoreService.getKey(dto.getCsrId().toString());
            subjectPrivateKey = null;
        }
        PublicKey issuerPublicKey;
        PrivateKey issuerPrivateKey;
        if (dto.getSelfSigned()) {
            issuerPublicKey = subjectPublicKey;
            issuerPrivateKey = subjectPrivateKey;
        } else {
            issuerPublicKey = keyStoreService.getSingleCertificate(dto.getIssuingCaSerialNumber()).getPublicKey();
            issuerPrivateKey = (PrivateKey) keyStoreService.getKey(dto.getIssuingCaSerialNumber());
        }

        Map<String , Object> params = new HashMap<>();
        params.put("subjectPublicKey", subjectPublicKey);
        params.put("issuerPublicKey", issuerPublicKey);
        params.put("ocspResponderUris", uriService.ocspResponderUris);
        if (dto.getSelfSigned()) params.put("caIssuersUris", Arrays.asList());
        else params.put("caIssuersUris", Arrays.asList(uriService.getCertificateAddress(dto.getIssuingCaSerialNumber())));

        List<Extension> bcExtensions = new ArrayList<>();
        try {
            for (AbstractExtensionDto e : dto.getExtensions()) {
                Extension bcExtension = e.getBCExtension(params);
                bcExtensions.add(bcExtension);
            }
        } catch (IOException ex) {
            throw new ApiInternalServerErrorException("Something went wrong while generating extensions.");
        }

        X509Certificate subjectX509Certificate = x500Service.generate(
                dto.getSignatureAlgorithm(),
                dto.getSerialNumber(),
                subjectName,
                issuerName,
                validFrom,
                validUntil,
                bcExtensions,
                subjectPublicKey,
                issuerPrivateKey
        );
        X509Certificate[] chain = x500Service.createX509CertChain(subjectX509Certificate, dto.getIssuingCaSerialNumber());
        return new X509CertificateWithKeys(subjectX509Certificate, chain, subjectPublicKey, subjectPrivateKey);
    }

    public Certificate createCertificateEntity(CreateCertificateDto dto, X509Certificate subjectX509Cert) {
        Certificate cert = new Certificate();
        cert.setKeyGenerationAlgorithm(dto.getKeyGenerationAlgorithm());
        cert.setKeySize(dto.getKeySize());
        cert.setSigningAlgorithm(dto.getSignatureAlgorithm());
        cert.setSerialNumber(dto.getSerialNumber());
        cert.setValidFrom(dateService.getDate(dto.getValidFrom()));
        cert.setValidUntil(dateService.getDate(dto.getValidUntil()));
        cert.setSelfSigned(dto.getSelfSigned());
        cert.setKeyStoreAlias(dto.getSerialNumber());
        cert.setCN(dto.getName().getCommonName());

        Map<String , Object> params = new HashMap<>();
        params.put("subjectX509Cert", subjectX509Cert);
        params.put("ocspResponderUris", uriService.ocspResponderUris);
        if (dto.getSelfSigned()) params.put("caIssuersUris", Arrays.asList());
        else                     params.put("caIssuersUris", Arrays.asList(uriService.getCertificateAddress(dto.getIssuingCaSerialNumber())));

        cert.getExtensions().addAll(dto.getExtensions().stream().map(e -> e.getExtensionEntity(params)).collect(Collectors.toList()));

        if (dto.getCsrId() != null) {
            CertificateRequest csr = csrRepository.findById(dto.getCsrId()).get();
            cert.setCertificateRequest(csr);
            csr.setCertificate(cert);
            csr.setStatus(CertificateRequestStatus.APPROVED);
        }

        Boolean isCa = false;
        int pathLength = subjectX509Cert.getBasicConstraints();
        if (pathLength != -1) {
            isCa = true;
        }

        cert.setIsCa(isCa);
        cert.setPathLen(pathLength);
        return cert;
    }

    public void verifyCreateCertificate(CreateCertificateDto dto) {
        if (!AlgorithmsConfig.secureKeyGenerationAlgorithms.contains(dto.getKeyGenerationAlgorithm())) {
            throw new ApiBadRequestException("Invalid key algorithm");
        }

        if (!AlgorithmsConfig.secureSigningAlgorithms.contains(dto.getSignatureAlgorithm())) {
            throw new ApiBadRequestException("Invalid signing algorithm");
        }

        if (certificateRepository.findBySerialNumber(dto.getSerialNumber()).isPresent()) {
            throw new ApiBadRequestException("Serial number not available");
        }

        if (certificateRepository.findByCN(dto.getName().getCommonName()).isPresent()) {
            throw new ApiBadRequestException("Common name not available");
        }

        Optional<Certificate> optionalCertificate = certificateRepository.findBySerialNumber(dto.getIssuingCaSerialNumber());
        if (!dto.getSelfSigned() && optionalCertificate.isEmpty()) {
            throw new ApiBadRequestException("Invalid issuer serial number");
        }

        if (dto.getSelfSigned() && optionalCertificate.isPresent()) {
            throw new ApiBadRequestException("Self signed and issuer CA cannot be present at same time.");
        }

        //TODO: CA certificate path validation according to KeySigning action
        boolean caPathValid = true;
        if (!caPathValid) {
            throw new ApiBadRequestException("Invalid CA for this type of action");
        }

        Date validFrom  = dateService.getDate(dto.getValidFrom());
        Date validUntil = dateService.getDate(dto.getValidUntil());
        if (!validFrom.before(validUntil) || validFrom.equals(validUntil)) {
            throw new ApiBadRequestException("Invalid validity dates");
        }

        if (dto.getCsrId() != null && dto.getSelfSigned()) {
            throw new ApiBadRequestException("Invalid csr request");
        }

        if (dto.getCsrId() != null) {
            Optional<CertificateRequest> requestOptional = csrRepository.findById(dto.getCsrId());
            if (requestOptional.isEmpty()) {
                throw new ApiBadRequestException("Invalid csr id");
            } else if (!requestOptional.get().getStatus().equals(CertificateRequestStatus.PENDING)) {
                throw new ApiBadRequestException("Csr is already processed");
            }
        }
    }

    public List<CertificateDto> getCaCertificates() {
        Sort sort = Sort.by(Sort.Direction.ASC, "dateCreated");
        return certificateRepository.findValidCaCertificates(sort).stream()
                .map(CertificateDto::new)
                .collect(Collectors.toList());
    }
}