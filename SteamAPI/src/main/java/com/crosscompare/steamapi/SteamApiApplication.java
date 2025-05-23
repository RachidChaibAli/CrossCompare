package com.crosscompare.steamapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
public class SteamApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteamApiApplication.class, args);
    }

}
