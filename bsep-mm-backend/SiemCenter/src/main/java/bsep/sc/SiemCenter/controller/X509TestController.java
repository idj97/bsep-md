package bsep.sc.SiemCenter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agents/api/test")
public class X509TestController {

    @GetMapping
    public String getHello() {
        return "Hello";
    }
}
