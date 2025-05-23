package com.crosscompare.playstationscraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class PlaystationScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaystationScraperApplication.class, args);
    }

}
