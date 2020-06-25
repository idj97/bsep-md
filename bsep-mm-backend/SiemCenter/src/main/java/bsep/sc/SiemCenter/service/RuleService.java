package bsep.sc.SiemCenter.service;

import bsep.sc.SiemCenter.dto.rules.RuleDTO;
import bsep.sc.SiemCenter.exception.ApiBadRequestException;
import bsep.sc.SiemCenter.model.Rule;
import bsep.sc.SiemCenter.repository.RuleRepository;
import bsep.sc.SiemCenter.service.drools.KieSessionService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RuleService {

    @Autowired
    private KieSessionService kieSessionService;

    @Autowired
    private RuleRepository ruleRepository;

    @Value("${kjar.rule.path}")
    private String kjarRulesPath;

    public void create(RuleDTO ruleDTO) {
        ruleDTO.setRuleName(extractRuleName(ruleDTO.getRuleContent()));

        if(ruleDTO.getRuleName().equals("")) {
            throw new ApiBadRequestException("Rule name must not be empty");
        }

        Optional<Rule> optionalRule = ruleRepository.findByRuleName(ruleDTO.getRuleName());
        if (!optionalRule.isPresent()) {
            String rulePath = kjarRulesPath + ruleDTO.getRuleName().trim().replaceAll("\\s+","-") + ".drl";
            Rule rule = new Rule(ruleDTO.getRuleContent(), ruleDTO.getRuleName());

            kieSessionService.addRule(ruleDTO.getRuleContent(), rulePath);
            ruleRepository.save(rule);
          } else {
            throw new ApiBadRequestException("Rule name is already in use.");
        }
    }

    private String extractRuleName(String ruleContent) {

        int ruleStart = ruleContent.indexOf("rule");
        int ruleNameStart = ruleContent.indexOf("\"", ruleStart);
        int ruleNameEnd = ruleContent.indexOf("\"", ruleNameStart+1);

        try {
            return ruleContent.substring(ruleNameStart+1, ruleNameEnd).trim();
        } catch (IndexOutOfBoundsException ex) {
            throw new ApiBadRequestException("Failed to extract rule name");
        }

    }

    public void remove(String ruleName) {
        Optional<Rule> optionalRule = ruleRepository.findByRuleName(ruleName);
        if (optionalRule.isPresent()) {
            String rulePath = kjarRulesPath + ruleName + ".drl";
            kieSessionService.removeRule(rulePath);
            ruleRepository.delete(optionalRule.get());
        } else {
            throw new ApiBadRequestException("Rule not found.");
        }
    }

    public List<RuleDTO> getAllRules() {
        return ruleRepository
                .findAll()
                .stream()
                .map(rule -> new RuleDTO(rule.getId(), rule.getRuleName(), rule.getRuleContent()))
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
}
