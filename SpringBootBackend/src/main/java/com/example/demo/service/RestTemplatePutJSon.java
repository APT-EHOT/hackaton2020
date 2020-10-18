package com.example.demo.service;



import com.example.demo.controllers.MainController;
import com.example.demo.utils.Magic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class RestTemplatePutJSon {
    @Autowired
    Magic magic;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    RestTemplateGetJson restTemplateGetJson;
    @Autowired
    MainController mainController;

    public String pytJson() {
        Gson gson = new GsonBuilder().create();
        String payloadStr = gson.toJson(restTemplateGetJson.getJsonTyPy(mainController.getCordStart(),mainController.getCordEnding()));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("huita", payloadStr);


        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers, headers);
        ResponseEntity<String> responseEntityStr = restTemplate.exchange("http://127.0.0.1:5000/foo",
                HttpMethod.POST,
                entity,
                String.class
        );

        MediaType contentType = responseEntityStr.getHeaders().getContentType();
        HttpStatus statusCode = responseEntityStr.getStatusCode();

return responseEntityStr.getBody();
    }
}
