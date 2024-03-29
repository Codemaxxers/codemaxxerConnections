package com.nighthawk.spring_portfolio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {

    @GetMapping("/")
    public String index() {
        return "index"; // Refers to src/main/resources/templates/index.html
    }
}
