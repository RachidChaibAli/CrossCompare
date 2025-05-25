package com.crosscompare.playstationscraper;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class PlaystationScraperApplication {

    @Autowired
    private BusquedaStreamListener busquedaStreamListener;

    public static void main(String[] args) {
        SpringApplication.run(PlaystationScraperApplication.class, args);
    }

    @PostConstruct
    public void startBusquedaListener() throws IOException {
        busquedaStreamListener.escucharBusquedaStream();
    }
}
