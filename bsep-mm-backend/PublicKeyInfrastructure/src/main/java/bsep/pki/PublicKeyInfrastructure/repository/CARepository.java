package bsep.pki.PublicKeyInfrastructure.repository;

import bsep.pki.PublicKeyInfrastructure.model.CA;
import bsep.pki.PublicKeyInfrastructure.model.enums.CAType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CARepository extends JpaRepository<CA, Long> {
    Optional<CA> findByType(CAType type);
    Optional<CA> findByTypeAndCertificateRevocationNull(CAType caType);
}
