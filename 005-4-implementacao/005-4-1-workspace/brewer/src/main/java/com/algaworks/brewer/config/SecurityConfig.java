package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.algaworks.brewer.security.AppUserDetailsService;


@EnableWebSecurity
@ComponentScan(basePackageClasses = AppUserDetailsService.class)
@EnableGlobalMethodSecurity(prePostEnabled = true) //permite que a anotacao PreAuthorize seja utilizada no service
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailsService userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
// 		Codigo para Autenticacao padrao em memoria, usar usuario e senha admin,admin que estao configurados
//		auth.inMemoryAuthentication()
//		.withUser("admin").password("admin").roles("CADASTRO_CLIENTE");
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		
	}
	    
    // configurar nesse metodo urls que podem ser acessadas sem autenticação
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/layout/**")
			.antMatchers("/images/**");
		
			
		
	};
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/** Nota: Para usar a sessao "Dev" COMENTAR a sessao "Prod1" dessa classe, a sessao "A" no arquivo BarraNavegacao,
		  *       e a sessao "A csrf" no arquivo LayoutPadrao.html e descomentar essa sessao "Dev" dessa classe.
		  */
		// [ Sessao Dev ] - Desabilita o Spring Security para desenvolvimento (sem tela de login)
		// http.authorizeRequests().anyRequest().permitAll().and().csrf().csrfTokenRepository(new CookieCsrfTokenRepository()).disable();


		
		/** Nota: Para usar a sessao "Prod1" DESCOMENTAR a sessao "Prod1" dessa classe, a sessao "A" no arquivo BarraNavegacao.html,
		  *       e a sessao "A csrf" no arquivo LayoutPadrao.html e comentar a sessao DEV dessa classe.
		  */
		// [ Sessao Prod1 ] - Habilitar o Spring Security para produção (com tela de login)
		http
		.authorizeRequests()      // abaixo do authorizeRequests incluir urls que precisam de permissao e autenticacao		
			.antMatchers("/cidades/nova").hasRole("CADASTRAR_CIDADE")  // acessa somente url /cidades/nova - no BD deve estar cadastro ROLE_CADASTRAR_CIDADE para usar hasRole 
			.antMatchers("/usuarios/**").hasRole("CADASTRAR_USUARIO") // acessa todas pastas e arquivos da url usuario pra frnte
			.anyRequest().authenticated() // daqui pra baixo acessa tudo se tiver autenticado, urls acima somente se tiver permissao
			.and()
		.formLogin()
			.loginPage("/login")  // url da pagina de login, caso não tenha o spring gera uma padrao
			.permitAll()          // qualquer um pode acessar a pagina de login
			.and()
		.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // autorizando e evitando erro csrf
			.and()
		.exceptionHandling().accessDeniedPage("/403") // vai para essa url para acesso negado, essa url deve ser mapeada no controller
			.and()
		.sessionManagement()
			.invalidSessionUrl("/login");  // sessao invalida, redireciona para a pagina de login

		
		// O codigo abaixo controla a quantidade de acesso que um
		// usuario pode ter ao mesmo tempo 
		/**
		   .sessionManagement()
			.maximumSessions(1)
			.expiredUrl("/login");
		*/	
		
	} 
	
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
		
	}
	
	
}
