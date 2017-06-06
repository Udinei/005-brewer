package com.algaworks.brewer.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.algaworks.brewer.aceitacao.CadastroCervejaDbUtTest;
import com.algaworks.brewer.aceitacao.CadastroClienteDbUtTest;
import com.algaworks.brewer.aceitacao.CadastroEstadoDbUtTest;
import com.algaworks.brewer.aceitacao.CadastroEstiloItTest;
import com.algaworks.brewer.aceitacao.CadastroUsuarioDbUtTest;
import com.algaworks.brewer.aceitacao.CadastroVendaCervejaDbUtTest;
import com.algaworks.brewer.aceitacao.LoginItTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   CadastroEstadoDbUtTest.class,
   CadastroEstiloItTest.class,
   CadastroClienteDbUtTest.class,
   CadastroUsuarioDbUtTest.class,
   CadastroCervejaDbUtTest.class,
   CadastroVendaCervejaDbUtTest.class,
   LoginItTest.class   
})

public class JunitTestSuite {   
	
} 