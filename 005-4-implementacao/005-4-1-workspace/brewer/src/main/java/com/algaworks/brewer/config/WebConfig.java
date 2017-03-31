package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;

import com.algaworks.brewer.thymeleaf.BrewerDialect;


/** classe de configuração do spring a anotação @Bean deve ser usada nos metodos que serão acessados no contexto 
 *  do sistema */
@EnableSpringDataWebSupport 
@Configuration
@EnableCaching
@EnableAsync
public class WebConfig extends WebMvcConfigurerAdapter {
		

	/** Esse metodo e ViewResolver para tratar relatorios .jasper informando o local onde se encontra
	 * os arquivos de relatorios - JasperReportsViewResolver e uma integração do spring com jasperreports */
	@Bean
	public  ViewResolver JasperReportsViewResolver(DataSource datasource){
		JasperReportsViewResolver resolver = new JasperReportsViewResolver();
		resolver.setPrefix("classpath:/relatorios/");   // local onde esta os arquivos de relatorios
		resolver.setSuffix(".jasper");      // extensao do arquivo
		resolver.setViewNames("relatorio_*");   // todos os arquivlos que comeca com relatorio_
		resolver.setViewClass(JasperReportsMultiFormatView.class);
		resolver.setJdbcDataSource(datasource);  // setando o dataSource
		resolver.setOrder(0);  // ordem de verificacao do local onde se encotra o arquivo porque tem ha mais de um veiw resolver
		return resolver;
	}
	
	@Bean
	public BrewerDialect brewerDialect() {
		return new BrewerDialect();
	}
	
		
	/** este metodo orienta o spring a procurar qualquer recursos staticos a partir do "/" de classpath:/static/ 
	    os recursos estaticos compreende images, javascripts, stylessheets etc.. 
	  */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}
	
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		
		//	implementacao padrao do spring, NumberStyleFormatter pega o locale da instancia do browser
		NumberStyleFormatter bigDecimalFormatter = new NumberStyleFormatter("#,##0.00");
		registry.addFormatterForFieldType(BigDecimal.class, bigDecimalFormatter);
		
		//	API de Datas do Java 8 - para utilizar o tipo LocalDate
		DateTimeFormatterRegistrar dateTimeFormatter = new DateTimeFormatterRegistrar();
		dateTimeFormatter.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		// Habilita informar em um campo LocalTime somente hora e minuto
		dateTimeFormatter.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));
		dateTimeFormatter.registerFormatters(registry);
	}
	

	
	// tambem usado em: internacionalizacao, informa onde estão os arquivos de mensagens
	@Bean
	public MessageSource messageSource(){
		ReloadableResourceBundleMessageSource bundle = new ReloadableResourceBundleMessageSource();
		bundle.setBasename("classpath:/messages");
		bundle.setDefaultEncoding("UTF-8"); 
		return bundle;
	}

	// usado em: intercionalizacao, seta o validator da aplicacao
	@Bean
	public LocalValidatorFactoryBean validator() {
	    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
	    validatorFactoryBean.setValidationMessageSource(messageSource());
		    
	    return validatorFactoryBean;
	}

	
	// sobreescrevendo o validator da classe WebMvcConfigurerAdapter pelo novo validador validator()
	@Override
	public Validator getValidator() {
		return validator();
	}
	
	



}
