package bsep.pki.PublicKeyInfrastructure.service;

import bsep.pki.PublicKeyInfrastructure.dto.TemplateDto;
import bsep.pki.PublicKeyInfrastructure.exception.ApiNotFoundException;
import bsep.pki.PublicKeyInfrastructure.model.Template;
import bsep.pki.PublicKeyInfrastructure.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    public List<TemplateDto> create(TemplateDto dto) {
        Optional<Template> optTemplate = templateRepository.findByName(dto.getName());
        Template template;
        if (optTemplate.isPresent()) {
            template = optTemplate.get();
            template.setExtensions(dto.getExtensions());
        } else {
            template = new Template(null, dto.getName(), dto.getExtensions());
        }
        templateRepository.save(template);
        return getAll();
    }

    public List<TemplateDto> getAll() {
        return templateRepository.findAll()
                .stream()
                .map(t -> new TemplateDto(t.getName(), t.getExtensions()))
                .collect(Collectors.toList());
    }

    public void delete(String name) {
        Optional<Template> optTemplate = templateRepository.findByName(name);
        if (optTemplate.isPresent()) {
            templateRepository.delete(optTemplate.get());
        } else {
            throw new ApiNotFoundException("Template not found");
        }
    }
}
