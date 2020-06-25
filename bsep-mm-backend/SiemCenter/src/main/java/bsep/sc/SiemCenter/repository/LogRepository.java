package bsep.sc.SiemCenter.repository;

import bsep.sc.SiemCenter.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends MongoRepository<Log, UUID> {

    List<Log> findByDateReceivedBetween(Date startDate, Date endDate);

    @Query("{$and:[" +
            "{$or: [{timestamp: {$regex: ?0}}, {$expr: {$eq:[?0,'']}}]}," +
            "{genericTimestampDate:{$gte: ?1, $lte: ?2}}," +
            "{dateReceived:{$gte: ?3, $lte: ?4}}," +
            "{$or: [{machineIp: {$regex: ?5}}, {$expr: {$eq:[?5,'']}}]}," +
            "{$or: [{machineOS: {$regex: ?6}}, {$expr: {$eq:[?6,'']}}]}," +
            "{$or: [{machineName: {$regex: ?7}}, {$expr: {$eq:[?7,'']}}]}," +
            "{$or: [{agentInfo: {$regex: ?8}}, {$expr: {$eq:[?8,'']}}]}," +
            "{$or: [{eventId: {$regex: ?9}}, {$expr: {$eq:[?9,'']}}]}," +
            "{$or: [{eventName: {$regex: ?10}}, {$expr: {$eq:[?10,'']}}]}," +
            "{$or: [{eventType: {$regex: ?11}}, {$expr: {$eq:[?11,'']}}]}," +
            "{$or: [{message: {$regex: ?12}}, {$expr: {$eq:[?12,'']}}]}," +
            "{$or: [{logSource: {$regex: ?13}}, {$expr: {$eq:[?13,'']}}]}," +
            "{$or: [{rawText: {$regex: ?14}}, {$expr: {$eq:[?14,'']}}]}," +
            "{$or: [{source: {$regex: ?15}}, {$expr: {$eq:[?15,'']}}]}," +
            "{$or: [{sourceIp: {$regex: ?16}}, {$expr: {$eq:[?16,'']}}]}," +
            "{$or: [{sourcePort: {$regex: ?17}}, {$expr: {$eq:[?17,'']}}]}," +
            "{$or: [{protocol: {$regex: ?18}}, {$expr: {$eq:[?18,'']}}]}," +
            "{$or: [{action: {$regex: ?19}}, {$expr: {$eq:[?19,'']}}]}," +
            "{$or: [{command: {$regex: ?20}}, {$expr: {$eq:[?20,'']}}]}," +
            "{$or: [{workingDir: {$regex: ?21}}, {$expr: {$eq:[?21,'']}}]}," +
            "{$or: [{sourceUser: {$regex: ?22}}, {$expr: {$eq:[?22,'']}}]}," +
            "{$or: [{targetUser: {$regex: ?23}}, {$expr: {$eq:[?23,'']}}]}" +
            "]}")
    Page<Log> search(
            String timestamp,
            Date lowerGenericTimestamp, Date upperGenericTimestamp,
            Date lowerRecievedAt, Date upperRecievedAt,
            String machineIp,
            String machineOS,
            String machineName,
            String agentInfo,
            String eventId,
            String eventName,
            String eventType,
            String message,
            String logSource,
            String rawText,
            String source,
            String sourceIp,
            String sourcePort,
            String protocol,
            String action,
            String command,
            String workingDir,
            String sourceUser,
            String targetUser,
            Pageable pageable);

    List<Log> findByMachineIp(String machineIp);
}
