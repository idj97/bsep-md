package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.dto.TemplateDto;
import bsep.pki.PublicKeyInfrastructure.dto.extensions.AbstractExtensionDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiBadRequestException;
import bsep.pki.PublicKeyInfrastructure.exception.ApiInternalServerErrorException;
import bsep.pki.PublicKeyInfrastructure.model.Template;
import bsep.pki.PublicKeyInfrastructure.repository.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    public void create(TemplateDto dto) {
        Optional<Template> optTemplate = templateRepository.findByName(dto.getName());
        Template template;
        if (optTemplate.isPresent()) {
            template = optTemplate.get();
            template.setExtensions(dto.getExtensions());
        } else {
            template = new Template(null, dto.getName(), dto.getExtensions());
        }
        templateRepository.save(template);
    }

    public List<TemplateDto> getAll() {
        return templateRepository.findAll()
                .stream()
                .map(t -> new TemplateDto(t.getName(), t.getExtensions()))
                .collect(Collectors.toList());
    }
}
