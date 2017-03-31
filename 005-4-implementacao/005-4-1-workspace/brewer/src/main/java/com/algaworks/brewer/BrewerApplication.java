package com.algaworks.brewer;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;



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
