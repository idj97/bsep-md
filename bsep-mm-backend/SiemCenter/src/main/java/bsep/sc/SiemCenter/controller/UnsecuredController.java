package bsep.sc.SiemCenter.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/unsecured")
public class UnsecuredController {

    /** In application properties we specified the following:
     * keycloak.securityConstraints[0].securityCollections[0].patterns[0] = /api/*
     * It means to secure all endpoints with 'api' in the path
     * Therefore, this controller is unsecured */

    @GetMapping("/hello")
    // @PreAuthorize("hasRole('operator')") // uncomment to secure
    public String hello() {
        return "Hello from unsecured";
    }
}
