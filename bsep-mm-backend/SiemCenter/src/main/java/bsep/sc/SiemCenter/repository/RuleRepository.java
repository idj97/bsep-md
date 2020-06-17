package bsep.sc.SiemCenter.repository;

import bsep.sc.SiemCenter.model.Rule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleRepository extends MongoRepository<Rule, UUID> {

    Optional<Rule> findByRuleName(String ruleName);
}
