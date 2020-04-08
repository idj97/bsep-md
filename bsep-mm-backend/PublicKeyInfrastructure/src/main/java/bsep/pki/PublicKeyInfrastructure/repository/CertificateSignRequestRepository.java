package bsep.pki.PublicKeyInfrastructure.repository;

import bsep.pki.PublicKeyInfrastructure.model.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.model.CertificateSignRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CertificateSignRequestRepository extends JpaRepository<CertificateSignRequest, Long> {

    List<CertificateSignRequest> findAllByStatus(CertificateRequestStatus status);
}
