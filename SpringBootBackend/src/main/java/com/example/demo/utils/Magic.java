package com.example.demo.utils;

import com.example.demo.config.dto.HuitaTwo;
import com.example.demo.controllers.MainController;
import com.example.demo.service.RestTemplateGetJson;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Magic {

    HuitaTwo huitaTwo;
    @Autowired
    RestTemplateGetJson restTemplateGetJson;
    @Autowired
    MainController mainController;
private HuitaTwo ResourceAccessException;

    public List<String> jep(String object) {

       HuitaTwo json = restTemplateGetJson.getJson(mainController.getCordStart(), mainController.getCordEnding());
        Pattern aye = Pattern.compile("\\d(.*?)");
        Matcher ayf = aye.matcher(object);
        ArrayList<Integer> number = new ArrayList<>();
        while (ayf.find()) {
            number.add(Integer.parseInt(object.substring(ayf.start(), ayf.end())));
        }
List<String> addresses =json.getDestination_addresses();
        ArrayList<String> sortAddress = new ArrayList<>();
        int i = 0;
        sortAddress.add(addresses.get(0));
        while(i <number.size()){
sortAddress.add(addresses.get(number.get(i)));
            i++;
        }
   return sortAddress; }


    }

