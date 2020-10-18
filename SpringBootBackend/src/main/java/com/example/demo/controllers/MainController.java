package com.example.demo.controllers;


import com.example.demo.service.RestTemplateGetJson;
import com.example.demo.service.RestTemplatePutJSon;
import com.example.demo.utils.Magic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@Log4j2
@RequiredArgsConstructor
@RestController
public class MainController {

    @Autowired
    Magic magic;
    @Autowired
    RestTemplateGetJson restTemplateGetJson;
    @Autowired
    RestTemplatePutJSon restTemplateputJson;

    public String getCordStart() {
        return cordStart;
    }

    public void setCordStart(String cordStart) {
        this.cordStart = cordStart;
    }

    public String getCordEnding() {
        return cordEnding;
    }

    public void setCordEnding(String cordEnding) {
        this.cordEnding = cordEnding;
    }

    String cordStart;
    String cordEnding = "";
    @GetMapping("/")
        public com.example.demo.config.dto.Huita govno(){
       return restTemplateGetJson.getJsonTyPy(cordStart,cordEnding);

    }

    /*@PostMapping ("/addAddress")
    public @ResponseBody
    ResponseEntity<String> addAddress(HttpServletRequest request) {
        if(request.getParameter("LatLong")!=null){
         cordStart = request.getParameter("LatLong");
        }
         cordEnding = request.getParameter("Goods");
         System.out.println(cordStart);
        System.out.println(cordEnding);
        return new ResponseEntity<String>(HttpStatus.OK);
    }*/
    @PostMapping("/api")
    public @ResponseBody
    ResponseEntity<String> patch(HttpServletRequest request) {
        Gson gson = new GsonBuilder().create();
        cordStart = request.getParameter("LatLong");
        System.out.println(cordStart);
        restTemplateGetJson.getJson(cordStart, cordEnding);
        cordEnding = "";
        String payloadStr = gson.toJson(magic.jep(restTemplateputJson.pytJson()));
        return new ResponseEntity<String>(payloadStr, HttpStatus.OK);
    }

    @PostMapping("/api/desctop")
    public @ResponseBody ResponseEntity<String> apiDesctop(HttpServletRequest request) throws Exception {
//            restTemplateGetJson.getJson(cordStart, cordEnding);
//            restTemplateputJson.pytJson();
        cordEnding += "|"+request.getParameter("Goods");
        System.out.println(cordEnding);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
