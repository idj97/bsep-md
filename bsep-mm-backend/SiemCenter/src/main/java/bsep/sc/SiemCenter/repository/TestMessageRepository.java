package bsep.sc.SiemCenter.repository;

import bsep.sc.SiemCenter.model.TestMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface TestMessageRepository extends MongoRepository<TestMessage, UUID> {

}
