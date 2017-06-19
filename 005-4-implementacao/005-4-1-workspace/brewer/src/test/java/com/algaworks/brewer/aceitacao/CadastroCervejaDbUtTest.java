package com.algaworks.brewer.aceitacao;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import com.algaworks.brewer.base.BaseTest;
import com.algaworks.brewer.base.DbUnitHelper;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.service.CadastroCervejaService;


public class CadastroCervejaDbUtTest extends BaseTest { 
	
	@Autowired
	private Cervejas cervejas;

	@Autowired
	CadastroCervejaService cadastroCervejaService;

	@BeforeClass
	public static void initClass() {
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		//  entra na tela de pesquisa
		driver.get("http://localhost:8080/cervejas");
	}

	
	@Before
	public void init() {
         // passa informações de conexao de banco para o DBUnit e pasta de acesso do .xml de controle do BD
		dbUnitHelper = new DbUnitHelper(setBaseBD(), "META-INF", "");
		
		// executa a conexao
		dbUnitHelper.conectaBD();	
		
		// Controla os dados inseridos no banco, que serão removidos apos os testes 
		dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "BrewerXmlDBData.xml");
		
	}	
	@Test
	public void fluxoPrincipal() throws Exception {
		
		/** A sequencia de chamada dos metodos abaixo, parte do principio de que todos os comandos serão executados 
		 a partir da  tela de cadastro */
		  deveExibirMsgDePrenchimentoCamposObrigatorio();
		  deveCadastrarUmNovoRegistro();
		  deveValidarExecaoRegistroJaCadastrado();
				
		/** A sequencia de chamada dos metodos abaixo, parte do principio de que todos os comandos serão executados 
		 a partir da  tela de pesquisa*/
          devePesquisarRegistroCadastrado();
          deveEditarRegistroCadastradoPesquisado();
          deveExibirMsgNenhumaEntidadePesquisadaEncontrada();
          deveCancelarExclusaoRegistroPesquisadoEncontrado();
	      deveExcluirRegistroPesquisadoEncontradoNaoUsadoEmOutroCadastro();
	}
	
	
	public void deveExibirMsgDePrenchimentoCamposObrigatorio() throws InterruptedException{
		clickButtonAcessaFormularioDeCadastro("Nova Cerveja");
		clickButtonSalvarClassName("btn-primary");
		List<String> validCampos = Arrays.asList("origem, F",
												 "quantidade, M",
												 "descricao, F",
												 "sabor, M",
												 "comissao, F",
												 "sku, M",
												 "teor, M",
												 "valor, M",
												 "nome, M",
												 "estilo, M");

		validaCamposObrigatorios(validCampos);
		
		clickButtonSairFormularioCadastro("btn-default");   
	}

	public void deveCadastrarUmNovoRegistro() throws InterruptedException {
		clickButtonAcessaFormularioDeCadastro("Nova Cerveja");
		preencheFormularioDeDados();
		clickButtonSalvarClassName("btn-primary");

        // valida msg exibida usuario, apos salvar formulario de cadastro   
		validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Cerveja", "Cerveja salva com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");   
	}
	
	public void deveCadastrarUmNovoRegistroNovaCerveja() throws InterruptedException {
		clickButtonAcessaFormularioDeCadastro("Nova Cerveja");
		preencheFormularioDeDadosNovaCerveja();
		clickButtonSalvarClassName("btn-primary");

        // valida msg exibida usuario, apos salvar formulario de cadastro   
		validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Cerveja", "Cerveja salva com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");   
	}
	
	public void deveValidarExecaoRegistroJaCadastrado() throws InterruptedException {
		clickButtonAcessaFormularioDeCadastro("Nova Cerveja");
		preencheFormularioDeDados();
		clickButtonSalvarClassName("btn-primary");
		Thread.sleep(5000);
		//validaMsgSemChaveExibidaFoiHaMensagem("SKU da cerveja já cadastrado!");
		validaMsgErrorInTextContains("SKU da cerveja já cadastrado!");
                                       
		clickButtonSairFormularioCadastro("btn-default");  
	}
	
	public void devePesquisarRegistroCadastrado(){
	   // recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisa();
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("Becks Long Neck");
		
	}
	
	/** A alteração realizada por esse metodo sera desfeita pelo dbunit, dispensado a necessidade reeditar o registro
	 *  para o estado antes da edição (alteracao) 
	 * @throws InterruptedException 
	 *  
	 */
	public void deveEditarRegistroCadastradoPesquisado() throws InterruptedException{
		clickButtonEditar();
		preencheCamposDaEdicao();
    	clickButtonSalvarClassName("btn-primary");
    	validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Cerveja", "Cerveja salva com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");  
		
	}
	
	
	public void deveExibirMsgNenhumaEntidadePesquisadaEncontrada(){
		// recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisa();
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("Nenhuma cerveja encontrada");
		
	}
	
	
	public void deveCancelarExclusaoRegistroPesquisadoEncontrado() throws InterruptedException{
		driver.get("http://localhost:8080/cervejas");
		Thread.sleep(1000);
		preencheCamposDePesquisaParaExclusao();
		clickButtonPesquisar("btn-primary");
		clickButtonExcluirRegistroPesquisadoComSubString("Cerveja");
		clickButtomCancelAlertExcluirRegistro(); 
		validaExclusaoRegistroFoiCancelada("Cerveja");
	}

	
	public void deveExcluirRegistroPesquisadoEncontradoNaoUsadoEmOutroCadastro() throws InterruptedException{
 			Thread.sleep(1000);
			clickButtonExcluirRegistroPesquisadoComSubString("Cerveja");
			clickButtonOkAlertExcluirRegistro();
	}

		
	public void preencheFormularioDeDados() throws InterruptedException {
		// na tela de cadastro informa registro de teste 
		driver.findElement(By.name("sku")).sendKeys("AA1234");
		driver.findElement(By.name("nome")).sendKeys("Becks Long Neck");
		driver.findElement(By.name("descricao")).sendKeys("Boa para churrascos");
		selectComboxVisibleValue("estilo","Amber Lager");
		selectComboxVisibleValue("sabor","Amarga");
		driver.findElement(By.name("teorAlcoolico")).sendKeys("10");
		driver.findElement(By.name("valor")).sendKeys("199");
		selectRadioButtonValue("origem", "Internacional");
		driver.findElement(By.name("quantidadeEstoque")).sendKeys("12");
		driver.findElement(By.name("comissao")).sendKeys("5");
		Thread.sleep(100);
		uploadFileDiskFromWebserve("upload-select", "E://projetos_sistemas//005-brewer//005-5-teste//005-5-1-fotos_cerveja//beck-long-neck-275ml.png");
		                                                                                                                   	    
	}
	
	public void preencheFormularioDeDadosNovaCerveja() throws InterruptedException {
		// na tela de cadastro informa registro de teste 
		driver.findElement(By.name("sku")).sendKeys("BB1234");
		driver.findElement(By.name("nome")).sendKeys("Cerva Negra");
		driver.findElement(By.name("descricao")).sendKeys("Boa para churrascos");
		selectComboxVisibleValue("estilo","Dark Lager");
		selectComboxVisibleValue("sabor","Adocicada");
		driver.findElement(By.name("teorAlcoolico")).sendKeys("20");
		driver.findElement(By.name("valor")).sendKeys("250");
		selectRadioButtonValue("origem", "Nacional");
		driver.findElement(By.name("quantidadeEstoque")).sendKeys("100");
		driver.findElement(By.name("comissao")).sendKeys("3");
		uploadFileDiskFromWebserve("upload-select", "E://projetos_sistemas//005-brewer//005-5-teste//005-5-1-fotos_cerveja//negra-modelo-long-neck-355ml.png");
		    
	}

	public void preencheCamposDePesquisa(){
		// na tela de cadastro informa registro de pesquisa 
		driver.findElement(By.name("sku")).sendKeys("AA1234");
		driver.findElement(By.name("nome")).sendKeys("Becks Long Neck");
		selectComboxVisibleValue("estilo","Amber Lager");
		selectComboxVisibleValue("sabor","Amarga");
		selectRadioButtonValue("origem", "Internacional");
		driver.findElement(By.name("valorDe")).sendKeys("1");
		driver.findElement(By.name("valorAte")).sendKeys("199");
		
	}
	
	public void preencheCamposDaEdicao() throws InterruptedException{
		driver.findElement(By.name("nome")).clear();
		driver.findElement(By.name("nome")).sendKeys("Cerveja");
		selectComboxVisibleValue("sabor","Suave");
		selectRadioButtonValue("origem", "Nacional");

	}

	// sera utilizado o registro anteriormente editado
	public void preencheCamposDePesquisaParaExclusao(){
			driver.findElement(By.name("nome")).sendKeys("Cerveja");
			selectComboxVisibleValue("sabor","Suave");
			selectRadioButtonValue("origem", "Nacional");		
		}
		

	
	
	@After
	public void end() {
	

	}
}
