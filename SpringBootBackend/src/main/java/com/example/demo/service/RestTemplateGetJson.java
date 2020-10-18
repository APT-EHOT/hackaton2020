package com.example.demo.service;

import com.example.demo.config.dto.Huita;
import com.example.demo.config.dto.HuitaTwo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Service
public class RestTemplateGetJson {


    @Autowired
    RestTemplate restTemplate;

    HashMap<String, String> shifts = new HashMap<String,String>();


    public Huita getJsonTyPy(String latLonStart,String latLonEnd) {


        ResponseEntity<Huita> response = restTemplate.exchange(
                //"https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ latLonStart + latLonEnd + "&destinations=" + latLonStart + latLonEnd +"&mode=walking&language=ru-RU&key=AIzaSyCGxM42XnoNjyTCoSTKwMXeCvR8rp7Tt7Q",
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=55.67568833333333,37.49945|55.666795099999995,37.4824931|55.6629927,37.4815724|55.6143727,37.2035786&destinations=55.67568833333333,37.49945|55.666795099999995,37.4824931|55.6629927,37.4815724|55.6143727,37.2035786&mode=car&language=ru-RU&key=AIzaSyCGxM42XnoNjyTCoSTKwMXeCvR8rp7Tt7Q",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Huita>() {
                }
        );
        return response.getBody();
    }
    public HuitaTwo getJson(String latLonStart,String latLonEnd) {


        ResponseEntity<HuitaTwo> response = restTemplate.exchange(
                //"https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ latLonStart + latLonEnd + "&destinations=" + latLonStart + latLonEnd +"&mode=walking&language=ru-RU&key=AIzaSyCGxM42XnoNjyTCoSTKwMXeCvR8rp7Tt7Q",
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=55.67568833333333,37.49945|55.666795099999995,37.4824931|55.6629927,37.4815724|55.6143727,37.2035786&destinations=55.67568833333333,37.49945|55.666795099999995,37.4824931|55.6629927,37.4815724|55.6143727,37.2035786&mode=car&language=ru-RU&key=AIzaSyCGxM42XnoNjyTCoSTKwMXeCvR8rp7Tt7Q",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<HuitaTwo>() {
                }
        );
        return response.getBody();
    }
}



