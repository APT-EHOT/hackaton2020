package com.example.demo.controllers;

import com.example.demo.service.RestTemplateGetJson;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RequiredArgsConstructor
@RestController
public class MainController {
    @Autowired
    RestTemplateGetJson restTemplateGetJson;
    @GetMapping("/api/docsgen/downloadxls")
    public JSONPObject downloadTemplate() throws Exception {
     JSONPObject a =    restTemplateGetJson.getJson();
        return a;
    }
    }
