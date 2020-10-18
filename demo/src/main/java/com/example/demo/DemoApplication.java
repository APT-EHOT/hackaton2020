package com.example.demo;

import com.example.demo.pyInterpritor.HelloService;
import com.example.demo.pyInterpritor.HelloServiceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class DemoApplication  {


    public static void main(String[] args)  {

        SpringApplication.run(DemoApplication.class, args);

    }

    @Bean(name = "helloServiceFactory")
    public HelloServiceFactory helloFactory() {
        return new HelloServiceFactory();
    }

    @Bean(name = "helloServicePython")
    public HelloService helloServicePython() throws Exception {
        return helloFactory().getObject();


    }
}
