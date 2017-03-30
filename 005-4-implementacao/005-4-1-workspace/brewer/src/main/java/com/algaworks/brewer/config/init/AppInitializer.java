package com.algaworks.brewer.config.init;

import javax.servlet.Filter;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.algaworks.brewer.config.JPAConfig;
import com.algaworks.brewer.config.MailConfig;
import com.algaworks.brewer.config.S3Config;
import com.algaworks.brewer.config.SecurityConfig;
import com.algaworks.brewer.config.ServiceConfig;
import com.algaworks.brewer.config.WebConfig;

/**
 * Todas configuração em classes que utilizam a anotation @Configuration
 * devem ser informadas nessa classe pra o spring gerencia-las
 * 
 * */
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	
	// configuração que envolve toda a aplicação servicos etc..
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] {JPAConfig.class, ServiceConfig.class, SecurityConfig.class, S3Config.class} ;
	}

	// configuração que envolve camada Web
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class, MailConfig.class };
	}

	/** configura DispathServlet do spring para tratar toda url a partir de "/" digitado no browser e 
	 enviar a algum frontcontroller da aplicacao */
	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}
	
	
	/** Nesse metodo pode ser implementado filtros de encoding para forcar UTF-8 
	 * HttpPutFormContentFilter - foi usado para permitir enviar parametros via PUT com Ajax ao controller */
	@Override
	protected Filter[] getServletFilters() {
		HttpPutFormContentFilter httpPutFormContentFilter = new HttpPutFormContentFilter();
		return new Filter[] { httpPutFormContentFilter };
	}
	
	
	/** informa ao servidor que sera enviada resquicoes de MultiPartFile ou seja arquivos de texto, imagens etc..*/ 
	@Override
	protected void customizeRegistration(Dynamic registration){
		registration.setMultipartConfig(new MultipartConfigElement(""));
		
	}

	// Metodo utilizado na configuracao do profile de carregamento de imagens da nuvem ou local
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		servletContext.setInitParameter("spring.profiles.default","local"); // profile default é local, e sera mudado em tempo de execução para profile desejado
	}
}
