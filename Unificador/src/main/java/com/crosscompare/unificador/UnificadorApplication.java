package com.crosscompare.unificador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class UnificadorApplication {

	public static void main(String[] args) {

		try{
		SpringApplication.run(UnificadorApplication.class, args);
		}catch(Exception e){
			System.out.println("Error en la API Gateway");
			e.printStackTrace();
		}
	}

}
