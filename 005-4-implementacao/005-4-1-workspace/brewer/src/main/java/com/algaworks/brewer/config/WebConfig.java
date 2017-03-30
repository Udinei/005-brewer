package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.algaworks.brewer.controller.CervejasController;
import com.algaworks.brewer.controller.converter.CidadeConverter;
import com.algaworks.brewer.controller.converter.EstadoConverter;
import com.algaworks.brewer.controller.converter.EstiloConverter;
import com.algaworks.brewer.controller.converter.GrupoConverter;
import com.algaworks.brewer.session.TabelasItensSession;
import com.algaworks.brewer.thymeleaf.BrewerDialect;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import com.google.common.cache.CacheBuilder;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/** classe de configuração do spring a anotação @Bean deve ser usada nos metodos que serão acessados no contexto 
 *  do sistema */
@Configuration
@ComponentScan(basePackageClasses = { CervejasController.class, TabelasItensSession.class }) // onde encontrar os controllers
@EnableWebMvc
@EnableSpringDataWebSupport  // insere intergracao e suporte a paginacao com uso Pageable do springData
@EnableCaching
@EnableAsync
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {
		
	/** classe do spring */
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	
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
	
	/** resolve as chamada da camada view vindas do controler
	   em conjunto com os metodos templateEngine e templateResolver */
	@Bean
	public ViewResolver viewResolver(){
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		resolver.setOrder(1);
		return resolver;
	}
	
	
	@Bean
	public TemplateEngine templateEngine(){
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(true);
		engine.setTemplateResolver(templateResolver());
		
		engine.addDialect(new LayoutDialect());  //para definição do layoutpadrao
		engine.addDialect(new BrewerDialect()); // dialect brewer para uso do atributo classforerror
		
		engine.addDialect(new DataAttributeDialect());
		engine.addDialect(new SpringSecurityDialect());
		
		return engine;
	}
	
	
	/** esse metodo informa a pasta raiz onde estão localizados dentro da aplicação
	    o tipo de arquivos utilizado na view, sua extensão (html) bem como seu encode (UTF-8) */ 
	private ITemplateResolver templateResolver(){
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("classpath:/templates/"); // local onde se deve procurar os arquivos de view
		resolver.setSuffix(".html"); // Evita ter que colocar a extenção do arquivo na chamada 
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding("UTF-8"); // necessario para rodar no servidor de producao, sem problemas de acentuacao
		return resolver;
	}

	
	/** este metodo orienta o spring a procurar qualquer recursos staticos a partir do "/" de classpath:/static/ 
	    os recursos estaticos compreende images, javascripts, stylessheets etc.. 
	  */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}
	
	
	
	@Bean
	public FormattingConversionService mvcConversionService (){
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
		conversionService.addConverter(new EstiloConverter());
		conversionService.addConverter(new CidadeConverter());
		conversionService.addConverter(new EstadoConverter());
		conversionService.addConverter(new GrupoConverter());
		
//		implementacao padrao do spring, NumberStyleFormatter pega o locale da instancia do browser
//		NumberStyleFormatter bigdecimalFormatter = new NumberStyleFormatter("#,##0.00");
		
		// BigDecimalFormatter implementacao customizada para conversao de valores, em função da intercionalizacao
		BigDecimalFormatter bigdecimalFormatter = new BigDecimalFormatter("#,##0.00"); 
		conversionService.addFormatterForFieldType(BigDecimal.class, bigdecimalFormatter);

//		implementacao padrao do spring, NumberStyleFormatter pega o locale da instancia do browser		
//		NumberStyleFormatter integerFormatter = new NumberStyleFormatter("#,##0");
		BigDecimalFormatter integerFormatter = new BigDecimalFormatter("#,##0");
		conversionService.addFormatterForFieldType(Integer.class, integerFormatter);
		
		//API de Datas do Java 8 - para utilizar o tipo LocalDate
		DateTimeFormatterRegistrar datatTimeFormatter = new DateTimeFormatterRegistrar();
		datatTimeFormatter.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		// Habilita informar em um campo LocalTime somente hora e minuto
		datatTimeFormatter.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));
		datatTimeFormatter.registerFormatters(conversionService);
		
		return conversionService;
	}
	


	
	@Bean
	public CacheManager cacheManager(){
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
				.maximumSize(3)  // permite ate tres itens em cache, se mais um item for selecionado, um item que esta no cache sera tirado, e incluido o novo 
				.expireAfterAccess(20, TimeUnit.SECONDS); // após 20 segundos os itens no cache se não tiver sendo usado seram removidos 
		
		GuavaCacheManager cacheManager = new GuavaCacheManager();
		cacheManager.setCacheBuilder(cacheBuilder);
		return cacheManager;
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
	
	
	/** Esse metodo habilita a conversão de string passadas com @PathVariable junto com parametros nos metodo
	    de classes Controller, transformando as strings em objetos (findOne), esses objetos que devem ser 
	    entidades gerenciada pelo Spring para que a conversao aconteça ex: No controller
	    @DeleteMapping("/item/{uuid}/{codigoCerveja}")
	    public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja){...} */ 
	@Bean
	public DomainClassConverter<FormattingConversionService> domainClassConverter(){
		return new DomainClassConverter<FormattingConversionService>(mvcConversionService());
	}
	
	

}
