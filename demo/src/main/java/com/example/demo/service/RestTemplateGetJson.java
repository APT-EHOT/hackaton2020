package com.example.demo.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


@Service
public class RestTemplateGetJson {

  @Autowired
  RestTemplate restTemplate;


    public JSONPObject getJson() {


        ResponseEntity<JSONPObject> response = restTemplate.exchange(
                "https://maps.googleapis.com/maps/api/distancematrix/json?" +"origin=" + "55.788094" + "," + "37.965630"+ "&" +"destination=" + "55.804382" + "," + "37.953592" +"&sensor=false&units=metric&mode=driving" + "&" + "AIzaSyAjhIxwUYOslALbhsbdg3uo1GkXlNfiD-k",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<JSONPObject>() {
                }

        );
        return response.getBody();
    }
}
