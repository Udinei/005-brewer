package com.algaworks.brewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/** Essa classe deve ser executada para subir aplicacao com spring boot
 *  Utiliza o tomcat embeded 
 *  comando para executar a aplicacao apos o empacotamento:
 *  java -jar nomeApp args - args opcional */

@SpringBootApplication
@EnableAutoConfiguration
public class BrewerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BrewerApplication.class, args);
	}
	
// 	Nao precisa esta sendo configurado no application.properties
//	@Bean
//	public LocaleResolver localeResolver() {
//		return new FixedLocaleResolver(new Locale("pt", "BR"));
//	}
	
}
