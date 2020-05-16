package bsep.pki.PublicKeyInfrastructure.repository;

import bsep.pki.PublicKeyInfrastructure.model.CA;
import bsep.pki.PublicKeyInfrastructure.model.enums.CAType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//TODO: DELETE (probably)
@Repository
public interface CARepository extends JpaRepository<CA, Long> {
    List<CA> findByType(CAType type);
    List<CA> findByTypeAndCertificateRevocationNull(CAType caType);
}
