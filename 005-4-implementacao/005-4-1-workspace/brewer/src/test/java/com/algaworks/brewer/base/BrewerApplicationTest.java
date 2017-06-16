package com.algaworks.brewer.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/** Essa classe e utilizada para carregar o contexto da aplicação para os testes, alem de integrar dbunit e o junit. 
 * disponibilizando diversas funcionalidades do spring boot, bem como injecao de dependencia de
 *  qualquer classe da aplicacao nos testes, com uso da anotation  @Autowire por exemplo.
 * */
@WebAppConfiguration
@EnableTransactionManagement
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
@RunWith(SpringRunner.class)
@SpringBootTest
public class BrewerApplicationTest {
	

	
	@Test
	public void contextLoads() {
		
	}

}
