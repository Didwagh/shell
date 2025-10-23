package com.project.ai.shell;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HappyController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping("/second")
    public String secondWorld() {
        return "2nd World";
    }

    @GetMapping("/third")
    public String thirdWorldWar() {
        return "3rd World War"; // Let's hope not!
    }
}
