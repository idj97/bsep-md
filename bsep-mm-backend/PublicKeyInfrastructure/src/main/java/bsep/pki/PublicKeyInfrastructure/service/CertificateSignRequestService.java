package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.dto.CertificateSignRequestDto;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.model.CertificateSignRequest;
import bsep.pki.PublicKeyInfrastructure.repository.CertificateSignRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateSignRequestService {

    @Autowired
    CertificateSignRequestRepository certReqRepo;


    public List<CertificateSignRequestDto> findByStatus(CertificateRequestStatus status) {
        return certReqRepo
                .findAllByStatus(status)
                .stream()
                .map(csr -> new CertificateSignRequestDto(csr))
                .collect(Collectors.toList());
    }

    public CertificateSignRequestDto approveCertificateRequest(Long id) {

        CertificateSignRequest request = certReqRepo
                .findById(id).get(); // TODO: or else throw exception

        request.setStatus(CertificateRequestStatus.APPROVED);
        certReqRepo.save(request);

        // TODO: create and memorize certificate
        return new CertificateSignRequestDto(certReqRepo.save(request));
    }

    public CertificateSignRequestDto declineCertificateRequest(Long id) {
        CertificateSignRequest request = certReqRepo
                .findById(id).get(); // TODO: or else throw exception

        request.setStatus(CertificateRequestStatus.DENIED);
        return new CertificateSignRequestDto(certReqRepo.save(request));
    }

}
