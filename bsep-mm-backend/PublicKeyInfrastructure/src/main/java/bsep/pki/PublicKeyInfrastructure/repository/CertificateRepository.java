package bsep.pki.PublicKeyInfrastructure.repository;

import bsep.pki.PublicKeyInfrastructure.model.Certificate;
import bsep.pki.PublicKeyInfrastructure.model.enums.CertificateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    Optional<Certificate> findByCommonName(String commonName);

    @Query(value = "SELECT c FROM Certificate c WHERE " +
            "c.revocation IS NULL AND " +
            "c.isCa IS TRUE AND " +
            "CURRENT_DATE < c.validUntil")
    List<Certificate> findValidCaCertificates(Sort sort);

    @Query(value = "SELECT c FROM Certificate c WHERE " +
            "c.revocation IS NULL AND " +
            "c.isOcspResponder IS TRUE AND " +
            "CURRENT_DATE < c.validUntil")
    Optional<Certificate> findOcspResponderCertificate();

    @Query(value = "SELECT c FROM Certificate c WHERE " +
            "c.commonName LIKE %:commonName% AND " +
            "(:revoked = CASE WHEN (c.revocation IS NULL) THEN FALSE ELSE TRUE END) AND " +
            "(:isCa = c.isCa) AND " +
            "((c.validFrom > :validFrom) OR (:validFrom IS NULL)) AND " +
            "((c.validUntil < :validUntil) OR (:validUntil IS NULL)) AND " +
            "((c.certificateType = :certificateType) OR (:certificateType IS NULL))")
    Page<Certificate> search(
            @Param("commonName")      String commonName,
            @Param("revoked")         boolean revoked,
            @Param("isCa")            boolean isCa,
            @Param("validFrom")       Date validFrom,
            @Param("validUntil")      Date validUntil,
            @Param("certificateType") CertificateType certificateType,
            Pageable pageable);
}
