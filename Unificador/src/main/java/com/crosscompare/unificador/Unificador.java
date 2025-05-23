package com.crosscompare.unificador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class Unificador {

	public static void main(String[] args) {

		try{
		SpringApplication.run(Unificador.class, args);
		}catch(Exception e){
			System.out.println("Error en la API Gateway");
			e.printStackTrace();
		}
	}

}
