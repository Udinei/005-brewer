package com.algaworks.brewer.aceitacao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.service.CadastroEstadoService;
//import com.algaworks.brewer.test.base.BrewerApplicationDBunitTest;

//public class CadastroEstadoTestandoTest extends BrewerApplicationDBunitTest {
public class CadastroEstadoTestandoTest { 

	
	@Autowired
	CadastroEstadoService cadastroEstadoService;

	@Test
	public void deveInserirUmNovoEstado() throws Exception {
			
		Estado estado =  new Estado();
		estado.setNome("EstadoTeste");
		estado.setSigla("ET");
		
		estado = cadastroEstadoService.salvar(estado);
		
		System.out.println(">>> " + estado.getCodigo());
		
	}

}
