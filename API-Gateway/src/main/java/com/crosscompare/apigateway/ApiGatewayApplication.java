package com.crosscompare.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {

		try{
		SpringApplication.run(ApiGatewayApplication.class, args);
		}catch(Exception e){
			System.out.println("Error en la API Gateway");
			e.printStackTrace();
		}
	}

}
