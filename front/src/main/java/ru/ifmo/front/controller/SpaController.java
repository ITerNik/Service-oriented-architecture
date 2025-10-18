package ru.ifmo.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {
    
    // Все не-API запросы перенаправляем на index.html (для SPA)
    @RequestMapping(value = {"/{path:[^\\.]*}", "/{path:^(?!api).*}/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
