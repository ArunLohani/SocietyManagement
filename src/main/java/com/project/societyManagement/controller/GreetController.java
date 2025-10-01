package com.project.societyManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class GreetController {

    @GetMapping("/")
    public String greet(){

        return "Hey , Your Backend is Working...";


    }

}
