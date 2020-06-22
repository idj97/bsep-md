package bsep.sc.SiemCenter.controller;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/test")
@CrossOrigin("*")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello operator from secured";
    }
}
