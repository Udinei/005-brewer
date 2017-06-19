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
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.service.CadastroUsuarioService;


public class CadastroUsuarioDbUtTest extends BaseTest { 
		
	@Autowired
	private Usuarios usuarios;

	@Autowired
	CadastroUsuarioService cadastroUsuarioService;

	@BeforeClass
	public static void initClass() {
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		//  entra na tela de pesquisa
		driver.get("http://localhost:8080/usuarios");
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
		clickButtonAcessaFormularioDeCadastro("Novo Usuário");
		clickButtonSalvarClassName("btn-primary");
		List<String> validCampos = Arrays.asList("Nome", "E-mail");
		validaCamposObrigatorios(validCampos);
		validaMsgErrorInTextContains("Selecione pelo menos um grupo");
		//validaMsgDeExecaoExibidaHaoUsuario("Selecione pelo menos um grupo");
		clickButtonSairFormularioCadastro("btn-default");   
	}

	public void deveCadastrarUmNovoRegistro() throws InterruptedException {
		clickButtonAcessaFormularioDeCadastro("Novo Usuário");
		preencheFormularioDeDados();
		clickButtonSalvarClassName("btn-primary");

        // valida msg exibida usuario, apos salvar formulario de cadastro   
		validaMsgSucessWithKeyInSpanText("msg.salvo.sucesso", "Usuário", "Usuário salvo com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");   
	}
	
	public void deveValidarExecaoRegistroJaCadastrado() throws InterruptedException {
		clickButtonAcessaFormularioDeCadastro("Novo Usuário");
		preencheFormularioDeDados();
		clickButtonSalvarClassName("btn-primary");
		validaMsgErrorInTextContains("E-mail já cadastrado");
		//validaMsgSemChaveExibidaFoiHaMensagem("E-mail já cadastrado");
		clickButtonSairFormularioCadastro("btn-default");  
	}
	
	public void devePesquisarRegistroCadastrado(){
	   // recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisa();
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
		validaMsgSucessWithKeyInSpanText("msg.salvo.sucesso", "Usuário", "Usuário salvo com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");  
		
	}
	
	
	public void deveExibirMsgNenhumaEntidadePesquisadaEncontrada(){
		// recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisa();
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("Nenhum usuário encontrado");
		
	}
	
	
	public void deveCancelarExclusaoRegistroPesquisadoEncontrado() throws InterruptedException{
		driver.get("http://localhost:8080/usuarios");
		Thread.sleep(1000);
		preencheCamposDePesquisaParaExclusao();
		clickButtonPesquisar("btn-primary");
		clickButtonExcluirRegistroPesquisadoComSubString("Maria das dores");
		clickButtomCancelAlertExcluirRegistro(); 
		validaExclusaoRegistroFoiCancelada("Maria das dores");
	}

	
	public void deveExcluirRegistroPesquisadoEncontradoNaoUsadoEmOutroCadastro() throws InterruptedException{
 			Thread.sleep(1000);
			clickButtonExcluirRegistroPesquisadoComSubString("Maria das dores");
			clickButtonOkAlertExcluirRegistro();
	}

	
	public void preencheFormularioDeDados() throws InterruptedException {
		// na tela de cadastro informa registro de teste 
		driver.findElement(By.name("nome")).sendKeys("Juliana dos Santos");
		driver.findElement(By.name("email")).sendKeys("juliana@gmail.com");
		preencheCampoComMascara("dataNascimento", "17031972");
		driver.findElement(By.xpath("//input[@type='password'][@name='senha']")).click();
		driver.findElement(By.xpath("//input[@type='password'][@name='senha']")).sendKeys("juliana");
		driver.findElement(By.xpath("//input[@type='password'][@name='confirmacaoSenha']")).sendKeys("juliana");
		// seleciona status do usuario
		selectInputBootstrapSwitchLabel("bootstrap-switch-label");
		selectCheckBoxItemValue("Vendedor"); 
	}

	public void preencheCamposDePesquisa(){
		driver.findElement(By.name("nome")).sendKeys("Juliana dos Santos");
		driver.findElement(By.name("email")).sendKeys("juliana@gmail.com");	
	}
	
	public void preencheCamposDaEdicao() throws InterruptedException{
		driver.findElement(By.name("nome")).clear();
		driver.findElement(By.name("nome")).sendKeys("Maria das dores");
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("maria@gmail.com");
		Thread.sleep(1000);
		selectCheckBoxItemValue("Administrador"); 
	}

	// sera utilizado o registro editado
	public void preencheCamposDePesquisaParaExclusao(){
			driver.findElement(By.name("nome")).sendKeys("Maria das dores");
			driver.findElement(By.name("email")).sendKeys("maria@gmail.com");		
	}
			
	
	@After
	public void end() {

	}
}
