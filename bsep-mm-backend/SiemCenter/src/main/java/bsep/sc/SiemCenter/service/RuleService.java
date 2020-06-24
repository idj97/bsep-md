package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.RuleDto;
import bsep.sc.SiemCenter.events.LogEvent;
import bsep.sc.SiemCenter.exception.ApiBadRequestException;
import bsep.sc.SiemCenter.exception.ApiNotFoundException;
import bsep.sc.SiemCenter.repository.RuleRepository;
import bsep.sc.SiemCenter.util.KieSessionTemplate;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RuleService {

    @Autowired
    private KieSessionTemplate kieSessionTemplate;

    @Autowired
    private RuleRepository ruleRepository;


    public List<RuleDto> getAllRules() {

        return ruleRepository
                .findAll()
                .stream()
                .map(rule -> new RuleDto(rule.getId(), rule.getRuleName(), rule.getRuleContent()))
                .collect(Collectors.toList());

    }

    public String getTemplate(String templatePath) {

        File file = new File(templatePath);
        try {
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ApiBadRequestException("Failed to load template");
        }

    }


    public boolean createRule(RuleDto ruleDto) {

        if(ruleDto.getRuleContent() == null) {
            return false;
        }

        kieSessionTemplate.createSessionFromDRL(ruleDto.getRuleContent());
        return true;

    }

    
}
