package bsep.sc.SiemCenter.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agents/api/test")
public class X509TestController {

    @GetMapping
    //@PreAuthorize("hasRole('ROLE_AGENT')")
    public String getHello() {
        return "Hello";
    }
}
