package com.algaworks.brewer.config.init;

import java.util.EnumSet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

// AbstractSecurityWebApplicationInitializer, implementa os filtros necessarios para  interceptar as classse de segurança
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer{

			
		@Override
		protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
			
			//controla a sessao independente do usuario estar usando ou não por tempo determinado em segundos 
			//servletContext.getSessionCookieConfig().setMaxAge(20); 
			
			// nao mostra mais o sessionID na url, o mesmo passa a ser controlado via cookie
			servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
			
			FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter()); 
			characterEncodingFilter.setInitParameter("encoding","UTF-8");
			characterEncodingFilter.setInitParameter("forceEncoding","true");
			characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
			
		}
}
