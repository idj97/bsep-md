package bsep.sc.SiemCenter.repository;

import bsep.sc.SiemCenter.model.Alarm;
import bsep.sc.SiemCenter.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface AlarmRepository extends MongoRepository<Alarm, UUID> {

    @Query("{$and:[" +
            "{timestamp:{$gte: ?0, $lte: ?1}}," +
            "{$or: [{name: {$regex: ?2}}, {$expr: {$eq:[?3,'']}}]}," +
            "{$or: [{description: {$regex: ?4}}, {$expr: {$eq:[?5,'']}}]}," +
            "{$or: [{machineIp: {$regex: ?6}}, {$expr: {$eq:[?7,'']}}]}," +
            "{$or: [{machineOS: {$regex: ?8}}, {$expr: {$eq:[?9,'']}}]}," +
            "{$or: [{machineName: {$regex: ?10}}, {$expr: {$eq:[?11,'']}}]}," +
            "{$or: [{agentInfo: {$regex: ?12}}, {$expr: {$eq:[?13,'']}}]}," +
            "{$or: [{alarmType: {$regex: ?14}}, {$expr: {$eq:[?15,'']}}]}" +
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
