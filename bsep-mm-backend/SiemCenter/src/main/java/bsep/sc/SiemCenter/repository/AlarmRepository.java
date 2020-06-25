package bsep.sc.SiemCenter.repository;

import bsep.sc.SiemCenter.model.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AlarmRepository extends MongoRepository<Alarm, UUID> {

    List<Alarm> findByTimestampBetween(Date startDate, Date endDate);

    @Query("{$and:[" +
            "{timestamp:{$gte: ?0, $lte: ?1}}," +
            "{$or: [{name: {$regex: ?2}}, {$expr: {$eq:[?2,'']}}]}," +
            "{$or: [{description: {$regex: ?3}}, {$expr: {$eq:[?3,'']}}]}," +
            "{$or: [{machineIp: {$regex: ?4}}, {$expr: {$eq:[?4,'']}}]}," +
            "{$or: [{machineOS: {$regex: ?5}}, {$expr: {$eq:[?5,'']}}]}," +
            "{$or: [{machineName: {$regex: ?6}}, {$expr: {$eq:[?6,'']}}]}," +
            "{$or: [{agentInfo: {$regex: ?7}}, {$expr: {$eq:[?7,'']}}]}," +
            "{$or: [{alarmType: {$regex: ?8}}, {$expr: {$eq:[?8,'']}}]}" +
            "]}")
    Page<Alarm> search(
            Date lowerTimestamp, Date upperTimestamp,
            String name,
            String description,
            String machineIp,
            String machineOS,
            String machineName,
            String agentInfo,
            String alarmType,
            Pageable pageable);
}
