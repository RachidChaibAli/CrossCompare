package com.crosscompare.unificador;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@EnableAsync
public class UnificadorApplication {

    @Autowired
    private UnificadorStreamListener unificadorStreamListener;

    public static void main(String[] args) {

        try{
            SpringApplication.run(UnificadorApplication.class, args);
        }catch(Exception e){
            System.out.println("Error en la API Gateway");
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void startUnificadorListener() throws Exception {
        unificadorStreamListener.escucharUnificadorStream();
    }
}
