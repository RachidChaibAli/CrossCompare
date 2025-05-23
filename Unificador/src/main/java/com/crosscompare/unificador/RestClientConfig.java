package com.crosscompare.unificador;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced // Habilita el balanceo de carga y descubrimiento de servicios
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
