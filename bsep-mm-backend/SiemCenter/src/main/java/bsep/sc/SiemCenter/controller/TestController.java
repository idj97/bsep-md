package bsep.sc.SiemCenter.controller;


import bsep.sc.SiemCenter.model.TestMessage;
import bsep.sc.SiemCenter.repository.TestMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequestMapping("api/test")
@CrossOrigin("*")
public class TestController {

    @Autowired
    TestMessageRepository testMessageRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello operator from secured";
    }

    @GetMapping("/message")
    public ResponseEntity<List<TestMessage>> getMessages() {
        return new ResponseEntity<>(this.testMessageRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/message/{id}")
    public ResponseEntity<TestMessage> getMessage(@PathVariable UUID id) {
        Optional<TestMessage> message = this.testMessageRepository.findById(id);

        return message
                .map(testMessage -> new ResponseEntity<>(testMessage, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping("/message")
    public ResponseEntity<TestMessage> createMessage(@RequestBody TestMessage testMessage) {
        testMessage.setId(UUID.randomUUID());
        return new ResponseEntity<>(this.testMessageRepository.save(testMessage), HttpStatus.CREATED);
    }
}
