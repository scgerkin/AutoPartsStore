package com.javaiii.groupproject.AutoPartsStore.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;

public class IndexController {
    @RequestMapping("index")
    public String index() {
        return "/index";
    }
}
