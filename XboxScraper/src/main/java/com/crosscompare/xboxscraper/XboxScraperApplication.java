package com.crosscompare.xboxscraper;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class XboxScraperApplication {

    @Autowired
    private BusquedaStreamListener busquedaStreamListener;

    public static void main(String[] args) {
        SpringApplication.run(XboxScraperApplication.class, args);
    }

    @PostConstruct
    public void startBusquedaListener() throws Exception {
        busquedaStreamListener.escucharBusquedaStream();
    }
}
