package bsep.pki.PublicKeyInfrastructure.repository;

import bsep.pki.PublicKeyInfrastructure.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Integer> {
    Optional<Template> findByName(String name);
}
