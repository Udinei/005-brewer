package com.algaworks.brewer.aceitacao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.algaworks.brewer.base.BaseTest;


public class CadastroEstiloItTest extends BaseTest {


	@Test
	public void setUrlCaseTest() throws Exception {
		//Thread.sleep(1000);
		driver.get("http://localhost:8080/estilos");
	}
	
	
	@Test
	public void fluxoPrincipal() throws Exception {
		// na tela de cadastro
		deveCadastrarNovoEstilo();
		deveValidarExecaoEstiloJaCadastrado();
		
		// na tela de pesquisa
        devePesquisarEstiloCadastrado();
        deveCancelarExclusaoEstiloCadastradoPesquisado();
		deveExcluirEstiloCadastradoPesquisado();
				
	}

	//@Test
	public void deveCadastrarNovoEstilo() {
		// a partir da tela de pesquisa click no  botao Novo
		driver.findElement(By.xpath("//span[text()='Novo Estilo']")).click();
		// na tela de cadastro informa registro de teste 
		driver.findElement(By.name("nome")).sendKeys("CadastroEstiloTeste");
		// click no botao salvar
		driver.findElement(By.className("btn-primary")).click();
		// valida se msg de sucesso ao salvar foi exibida		
		validaMsgSucessWithKeyInSpanText("msg.salvo.sucesso", "Estilo","Estilo salvo com sucesso!");
		
	}
	
	
	public void deveValidarExecaoEstiloJaCadastrado() throws InterruptedException {
		// na tela de cadastro limpa campo nome
		//Thread.sleep(2000);
		driver.findElement(By.name("nome")).sendKeys("");
		// informa registro de teste
		driver.findElement(By.name("nome")).sendKeys("CadastroEstiloTeste");
		// click para salvar
		driver.findElement(By.className("btn-primary")).click();
		// valida se msg de entidade ja cadastrada foi exibida	
		//assertEquals(messagesUtil.getMessage("msg.error.atrib.ent.ja.cadastrado", "Nome", "estilo"), retornaTextContains("Nome do estilo já cadastrado!"));
		validaMsgErrorWithKeyInTextContains("msg.error.atrib.ent.ja.cadastrado", "Nome", "estilo", "Nome do estilo já cadastrado!");
		// clica no botao pesquisar e volta para tela de pesquisa
		driver.findElement(By.className("btn-default")).click();
	}
	
	
	public void devePesquisarEstiloCadastrado() throws InterruptedException {
		// na tela de pesquisa, limpa campo estilo
		driver.findElement(By.id("estilo")).sendKeys("");
		// entra com registro a ser pesquisado 
		driver.findElement(By.id("estilo")).sendKeys("CadastroEstiloTeste");
		// click no botao pesquisar
		driver.findElement(By.className("btn-primary")).click();
	}
		
	
	public void deveCancelarExclusaoEstiloCadastradoPesquisado() throws InterruptedException {
		// click no icone "X" (link) para excluir regristro de teste   		
		driver.findElement(By.xpath("//*[@data-objeto='CadastroEstiloTeste']")).click();
		// alert da msg de exclusao do registro foi exibida
		Thread.sleep(1000);
		assertTrue(login.isElementPresent(By.xpath("//button[text()='Cancel']"))); 
        // click no botao cancel
		driver.findElement(By.xpath("//button[text()='Cancel']")).click(); 
		assertTrue(login.isElementPresent(By.xpath("//*[@data-objeto='CadastroEstiloTeste']")));
	}

	
	
	public void deveExcluirEstiloCadastradoPesquisado() throws InterruptedException {
		// click no icone "X" (link) para excluir regristro de teste   		
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@data-objeto='CadastroEstiloTeste']")).click();
		// alert da msg de exclusao do registro foi exibida
		Thread.sleep(1000);
		assertTrue(login.isElementPresent(By.xpath("//button[text()='Sim, exclua agora!']"))); 
        // click no botao sim 
		driver.findElement(By.xpath("//button[text()='Sim, exclua agora!']")).click(); 
		// alert da msg de confirma a exclusao
		assertTrue(login.isElementPresent(By.xpath("//button[text()='OK']")));
		// alert de exclusao com sucesso e botao ok
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[text()='OK']")).click();
	}
	
	
}
