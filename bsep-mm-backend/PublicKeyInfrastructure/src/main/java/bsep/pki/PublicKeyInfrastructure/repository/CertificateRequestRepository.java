package bsep.pki.PublicKeyInfrastructure.repository;

import bsep.pki.PublicKeyInfrastructure.model.CertificateRequestStatus;
import bsep.pki.PublicKeyInfrastructure.model.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {

    List<CertificateRequest> findAllByStatus(CertificateRequestStatus status);
}
