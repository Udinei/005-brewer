package com.algaworks.brewer.aceitacao;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.service.CadastroClienteService;
import com.algaworks.brewer.base.BaseTest;
import com.algaworks.brewer.base.DbUnitHelper;


public class CadastroClienteDbUtTest extends BaseTest { 
	
	private static DbUnitHelper dbUnitHelper;
	private static EntityManagerFactory factory;
	private EntityManager manager;
		
	@Autowired
	private Clientes clientes;

	@Autowired
	CadastroClienteService cadastroClienteService;

	@BeforeClass
	public static void initClass() {
		dbUnitHelper = new DbUnitHelper("META-INF");
		factory = Persistence.createEntityManagerFactory("IntegracaoDbunitPU");
		
		driver.manage().timeouts().implicitlyWait(12, TimeUnit.SECONDS);
		//  entra na tela de pesquisa
		driver.get("http://localhost:8080/clientes");
	}

	
	@Before
	public void init() {
		// Os dados utilizados nos testes, como salvar serão removidos apos os testes pela linha de codigo abaixo 
		dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "BrewerXmlDBData.xml");
		manager = factory.createEntityManager();
		
	}
	
	
	@Test
	public void fluxoPrincipal() throws Exception {
		
		/** A sequencia de chamada dos metodos abaixo, parte do principio de que todos os comandos serão executados 
		 a partir da  tela de cadastro */
		deveExibirMsgDePrenchimentoCamposObrigatorio();
		deveCadastrarUmNovoCliente();
		deveValidarExecaoClienteJaCadastrado();
				
		/** A sequencia de chamada dos metodos abaixo, parte do principio de que todos os comandos serão executados 
		 a partir da  tela de pesquisa*/
         devePesquisarRegistroCadastrado();
         deveEditarRegistroCadastradoPesquisado();
         deveExibirMsgNenhumaEntidadePesquisadaEncontrada();
         deveCancelarExclusaoRegistroPesquisadoEncontrado();
	     deveExcluirRegistroPesquisadoEncontradoNaoUsadoEmOutroCadastro();
				
	}
	
	public void deveExibirMsgDePrenchimentoCamposObrigatorio() throws InterruptedException{
		clickButtonAcessaFormularioDeCadastro("Novo cliente");
		clickButtonSalvarClassName("btn-primary");
		List<String> validCampos = Arrays.asList("Nome", "Tipo pessoa",	"CPF/CNPJ");
		validaCamposObrigatorios(validCampos);
		clickButtonSairFormularioCadastro("btn-default");   
	}

	
	public void deveCadastrarUmNovoCliente() throws InterruptedException {
		clickButtonAcessaFormularioDeCadastro("Novo cliente");
		preencheFormularioDadosCliente();
		clickButtonSalvarClassName("btn-primary");

        // valida msg exibida usuario, apos salvar formulario de cadastro   
		Thread.sleep(4000);
		validaMsgSucessWithKeyInSpanText("msg.salvo.sucesso", "Cliente", "Cliente salvo com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");   
	}
	
	public void deveValidarExecaoClienteJaCadastrado() throws InterruptedException {
		clickButtonAcessaFormularioDeCadastro("Novo cliente");
		preencheFormularioDadosCliente();
		clickButtonSalvarClassName("btn-primary");
		
		// valida msg de execao exibida ao usuario, apos tentar salvar registro ja cadastrado   
		validaMsgErrorWithKeyInTextContains("msg.error.atrib.ent.ja.cadastrado", "CPF/CNPJ", "cliente", "CPF/CNPJ do cliente já cadastrado!");
		clickButtonSairFormularioCadastro("btn-default");  
	}
	
	public void devePesquisarRegistroCadastrado(){
	   // recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisa("nome","Juliana dos Santos");
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("Juliana dos Santos");
		
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
		validaMsgSucessWithKeyInSpanText("msg.salvo.sucesso", "Cliente", "Cliente salvo com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");  
		
	}
	
	
	public void deveExibirMsgNenhumaEntidadePesquisadaEncontrada(){
		// recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisa("nome","Juliana dos SantosI");
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("Nenhum cliente encontrado");
		
	}
	
	
	public void deveCancelarExclusaoRegistroPesquisadoEncontrado() throws InterruptedException{
		driver.get("http://localhost:8080/clientes");
		Thread.sleep(1000);
		preencheCamposDePesquisaParaExclusao("nome","Juliana dos Santos");
		clickButtonPesquisar("btn-primary");
		clickButtonExcluirRegistroPesquisadoComSubString("Juliana dos Santos");
		clickButtomCancelAlertExcluirRegistro(); 
		validaExclusaoRegistroFoiCancelada("Juliana dos Santos");
	}

	
	public void deveExcluirRegistroPesquisadoEncontradoNaoUsadoEmOutroCadastro() throws InterruptedException{
 			Thread.sleep(1000);
			clickButtonExcluirRegistroPesquisadoComSubString("Juliana dos Santos");
			clickButtonOkAlertExcluirRegistro();
	}

	
	public void preencheFormularioDadosCliente() throws InterruptedException {
		// na tela de cadastro informa registro de teste 
		driver.findElement(By.name("nome")).sendKeys("Juliana dos Santos");
		// seleciona tipoPessoa Fisica
		selectRadioButtonValue("tipoPessoa", "FISICA");
		        
		// preenche cpf
		preencheCampoComMascara("cpfOuCnpj", "54305209187"); 
		preencheCampoComMascara("telefone", "67998291452");	
		
		driver.findElement(By.name("email")).sendKeys("udineisilva@gmail.com");
		driver.findElement(By.name("endereco.logradouro")).sendKeys("Rua Dorotheia de Oliveira");
		driver.findElement(By.name("endereco.numero")).sendKeys("625");
		driver.findElement(By.name("endereco.complemento")).sendKeys("Prox. Bar do Nantes");
		
		Thread.sleep(1000);
		preencheCampoComMascara("endereco.cep", "79091720");
                                        
	    // seleciona estado
		selectComboxVisibleValue("endereco.cidade.estado", "Goias");
    
    	// seleciona cidade
		Thread.sleep(1000);
    	selectComboxVisibleValue("endereco.cidade", "Goiânia");
	}

	
	public void preencheCamposDePesquisaParaExclusao(String campo, String valor){
		// seta campo de pesquisa, que sera usado para retornar e não retornar registros pesquisado
		driver.findElement(By.name(campo)).sendKeys(valor);
		// preenche cpf
		preencheCampoComMascara("cpfOuCnpj", "62785712000110"); 
	}
		
	public void preencheCamposDaEdicao(){
		// altera tipo pessoa
		selectRadioButtonValue("tipoPessoa", "JURIDICA");
		// altera cnpj
		preencheCampoComMascara("cpfOuCnpj", "62785712000110"); 
	}
	
	public void preencheCamposDePesquisa(String campo, String valor){
		// seta campo de pesquisa, que sera usado para retornar e não retornar registros pesquisado
		driver.findElement(By.name(campo)).sendKeys(valor);
		// preenche cpf
		preencheCampoComMascara("cpfOuCnpj", "54305209187"); 
	}
	
	
	@After
	public void end() {
		this.manager.close();
	}
}
