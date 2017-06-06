package com.algaworks.brewer.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.algaworks.brewer.mail.Mailer;

/** A primeira anotation @PropertySource tenta carregar o arquivo mail-ambiente.properties, "ambiente" e
 *  uma variavel do tomcat, caso nao encontre essa variavel será usado no nome do arquivo 
 *  que e  a string "local" que esta depois dos dois pontos do nome da variavel, e caso não encontre no
 *  arquivo mail-local as configuração, vai executando na sequencia os outros anottation @PropertySource. 
 *  Caso as propriedades nos arquivos tiverem os mesmos nomes esses serão sobreescritos. Caso a propriedade
 *  nao exista pode ser usada uma propriedade username por exemplo de um arquivo e password de outro.
 *  ignoreResourceNotFound - caso não encontre o arquivo, sube a aplicacao sem erros 
 *  Uso de arquivo externo "brewer-mail.properties" evita que dados reais ou temporarios fiquem expostos 
 *  em producao ou no github.*/ 

@Configuration
@ComponentScan(basePackageClasses = Mailer.class)
@PropertySource({ "classpath:env/mail-${ambiente:local}.properties" })
@PropertySource( value = { "file:${USERPROFILE}/.mail.brewer/brewer-mail.properties"}, ignoreResourceNotFound = true)
public class MailConfig {

	// injeta objeto que acessa o classpath da aplicacao env
	@Autowired
	private Environment env;
	
	@Bean
	public JavaMailSender mailSender(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		mailSender.setHost("smtp.gmail.com"); // servidor de email sendgrid.net
		mailSender.setPort(587);
		mailSender.setUsername(env.getProperty("username.email"));
		mailSender.setPassword(env.getProperty("password"));
		
		System.out.println(">>>> username: " + env.getProperty("username.email"));
		System.out.println(">>>> password: " + env.getProperty("password"));
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth",true);
		props.put("mail.smtp.starttls.enable" ,true);
		props.put("mail.debug", false);
		props.put("mail.smtp.connectiontimeout", 10000); // miliseconds
		
		mailSender.setJavaMailProperties(props);
		
		return mailSender;
		
	}
}
