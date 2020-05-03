package bsep.pki.PublicKeyInfrastructure.repository;

import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findBySerialNumber(String serialNumber);
    List<Certificate> findByCNContainingAndIssuedForCANotNull(String commonName);
    List<Certificate> findByIssuedForCANull();

    @Query("SELECT c FROM Certificate c WHERE " +
            "c.CN LIKE %:commonName% AND " +
            "(:revoked = CASE WHEN (c.revocation IS NULL) THEN FALSE ELSE TRUE END) AND " +
            "(:isCaCert = CASE WHEN (c.issuedForCA IS NULL) THEN FALSE ELSE TRUE END) AND " +
            "((c.validFrom > :validFrom) OR (:validFrom IS NULL)) AND " +
            "((c.validUntil < :validUntil) OR (:validUntil IS NULL)) AND " +
            "((c.certificateType = :certificateType) OR (:certificateType IS NULL))")
    Page<Certificate> search(
            @Param("commonName") String commonName,
            @Param("revoked") boolean revoked,
            @Param("isCaCert") boolean isCaCert,
            @Param("validFrom") Date validFrom,
            @Param("validUntil") Date validUntil,
            @Param("certificateType") CertificateType certificateType,
            Pageable pageable);
}
