package bsep.sc.SiemCenter.repository;

import bsep.sc.SiemCenter.model.Alarm;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface AlarmRepository extends MongoRepository<Alarm, UUID> {
}
