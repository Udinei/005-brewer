package com.algaworks.brewer.aceitacao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import com.algaworks.brewer.base.BaseTest;
import com.algaworks.brewer.base.DbUnitHelper;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.service.CadastroEstadoService;


public class CadastroEstadoDbUtTest extends BaseTest { 
	
	private static DbUnitHelper dbUnitHelper;
	private static EntityManagerFactory factory;
	private EntityManager manager;
		
	@Autowired
	private Estados estados;

	@Autowired
	CadastroEstadoService cadastroEstadoService;

	@BeforeClass
	public static void initClass() {
		dbUnitHelper = new DbUnitHelper("META-INF");
		factory = Persistence.createEntityManagerFactory("IntegracaoDbunitPU");
		
		driver.manage().timeouts().implicitlyWait(12, TimeUnit.SECONDS);
		//  entra na tela de pesquisa
		driver.get("http://localhost:8080/cidades");
	}

	
	@Before
	public void init() {
		// Os dados utilizados nos testes, como salvar serão removidos apos os testes pela linha de codigo abaixo 
		dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "BrewerXmlDBData.xml");
		manager = factory.createEntityManager();
		
	}
	
	
	@Test
	public void fluxoPrincipal() throws Exception {
		
		// A sequencia de chamada dos metodos abaixo, parte do principio de que todos os comandos serão executados 
		// a partir da  tela de cadastro
		deveExibirMsgDePrenchimentoCampoObrigatorio();
		deveCadastrarUmaNovaCidade();
		deveValidarExecaoCidadeJaCadastrada();
				
		// A sequencia de chamada dos metodos abaixo, parte do principio de que todos os comandos serão executados 
		// a partir da  tela de pesquisa
        devePesquisarCidadeCadastrada();
        deveEditarUmaCidadeCadastrada();
        deveExibirMsgSeCidadePesquisadaNaoExistir();
        deveCancelarHaExclusaoDaCidadeCadastradaPesquisada();
		deveExcluirCidadeCadastradaPesquisada();
				
	}
	
	public void deveExibirMsgDePrenchimentoCampoObrigatorio() throws InterruptedException{
		// entra na tela de cadastro 
		driver.findElement(By.xpath("//span[text()='Nova cidade']")).click();
		
		// click no botao salvar sem preencher nenhum campo
		driver.findElement(By.className("btn-primary")).click();
		Thread.sleep(200);
		
		// valida campos obrigatorios Estado e Nome
		List<String> validaCampos = Arrays.asList("estado, M", "nome, M");
		validaCamposObrigatorios(validaCampos);
	}
	

	//@Test
	public void deveCadastrarUmaNovaCidade() {
		// seleciona estado Goias
		WebElement estado = driver.findElement(By.xpath("//select[@name='estado']"));
    	new Select(estado).selectByVisibleText("Goias");
		// na tela de cadastro informa registro de teste 
		driver.findElement(By.name("nome")).sendKeys("CadastroCidadeTeste");
		// click no botao salvar
		driver.findElement(By.className("btn-primary")).click();
		// valida se msg de sucesso ao salvar foi exibida		
		validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Cidade","Cidade salva com sucesso!");

	}
	
	
	public void deveValidarExecaoCidadeJaCadastrada() throws InterruptedException {
		//Thread.sleep(2000);
		// selecina na combobox o estado 
    	WebElement estado = driver.findElement(By.xpath("//select[@name='estado']"));
    	new Select(estado).selectByVisibleText("Goias");
    	// limpa campo
		driver.findElement(By.name("nome")).sendKeys("");
		// informa registro ja cadastrado para teste
		driver.findElement(By.name("nome")).sendKeys("Goiânia");
		// click para salvar
		driver.findElement(By.className("btn-primary")).click();
		// A cidade Goiânia ja existe cadastrada, exibe msg que a entidade ja esta cadastrada	
		validaMsgErrorWithKeyInTextContains("msg.error.atrib.ent.ja.cadastrada", "Nome", "cidade","Nome da cidade já cadastrada!");	
				
		// clica no botao "+ Pesquisar" e volta para tela de pesquisa
		driver.findElement(By.className("btn-default")).click();
	}
	
	
	public void devePesquisarCidadeCadastrada(){
		// drop down
    	WebElement estado = driver.findElement(By.xpath("//select[@name='estado']"));
    	new Select(estado).selectByVisibleText("Goias");
    	// limpa campo
    	driver.findElement(By.name("nome")).sendKeys("");
    	// busca a cidade Goiânia
    	driver.findElement(By.name("nome")).sendKeys("Goiânia");
		// click no botao para realizar a pesquisar
		driver.findElement(By.className("btn-primary")).click();
		
	}
    	
	
	/** A alteração realizada por esse metodo sera desfeita pelo dbunit, dispensado a necessidade reeditar o registro
	 *  para o estado antes da edição (alteracao) */
	public void deveEditarUmaCidadeCadastrada(){
		// click no icone "X" (link) para excluir regristro de teste   		
		//Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@class='glyphicon glyphicon-pencil']")).click();
     	// altera o nome da ciade para Goiânia I
    	driver.findElement(By.name("nome")).sendKeys(" I");
		// click no botao salvar
		driver.findElement(By.className("btn-primary")).click();
		// valida se msg de sucesso ao salvar foi exibida		
		//assertEquals(messagesUtil.getMessage("msg.salva.sucesso", "Cidade"), driver.findElement(By.xpath("//span[text()='Cidade salva com sucesso!']")).getText());
		validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Cidade", "Cidade salva com sucesso!");
		// clica no botao "+ Pesquisar" e volta para tela de pesquisa
		driver.findElement(By.className("btn-default")).click();
		
	}
	
	public void deveExibirMsgSeCidadePesquisadaNaoExistir(){
		// drop down
    	WebElement estado = driver.findElement(By.xpath("//select[@name='estado']"));
    	new Select(estado).selectByVisibleText("Goias");
    	// limpa campo
    	driver.findElement(By.name("nome")).sendKeys("");
    	// busca a cidade Goiânia
    	driver.findElement(By.name("nome")).sendKeys("CidadeInexistente");
		// click no botao pesquisar
		driver.findElement(By.className("btn-primary")).click();
		
		assertTrue(isElementPresent(By.xpath("//td[text()='Nenhuma cidade encontrada']")));
		
	}
	
	public void deveCancelarHaExclusaoDaCidadeCadastradaPesquisada() throws InterruptedException{
		driver.get("http://localhost:8080/cidades");
		Thread.sleep(1000);
		// drop down
    	WebElement estado = driver.findElement(By.xpath("//select[@name='estado']"));
    	new Select(estado).selectByVisibleText("Goias");
    	// limpa campo
    	driver.findElement(By.name("nome")).sendKeys("");
    	// busca a cidade Goiânia
    	driver.findElement(By.name("nome")).sendKeys("Goiânia");
		// click no botao pesquisar
		driver.findElement(By.className("btn-primary")).click();
		
		// click no icone "X" (link) para excluir regristro de teste   		
		driver.findElement(By.xpath("//*[@data-objeto='Goiânia I']")).click();
		// alert da msg de exclusao do registro foi exibida
		assertTrue(login.isElementPresent(By.xpath("//button[text()='Cancel']"))); 
        // click no botao cancel
		driver.findElement(By.xpath("//button[text()='Cancel']")).click(); 
		// verifica se o elemento ainda esta na listagem
		assertTrue(login.isElementPresent(By.xpath("//*[@data-objeto='Goiânia I']")));
	}
	
	
	public void deveExcluirCidadeCadastradaPesquisada() throws InterruptedException{
		
			// click no icone "X" (link) para excluir regristro de teste   		
			Thread.sleep(1000);
			driver.findElement(By.xpath("//*[@data-objeto='Goiânia I']")).click();
			// alert da msg de exclusao do registro foi exibida
			//Thread.sleep(1000);
			assertTrue(login.isElementPresent(By.xpath("//button[text()='Sim, exclua agora!']"))); 
	        // click no botao sim 
			driver.findElement(By.xpath("//button[text()='Sim, exclua agora!']")).click(); 
			// alert da msg de confirma a exclusao
			assertTrue(login.isElementPresent(By.xpath("//button[text()='OK']")));
			// alert de exclusao com sucesso e botao ok
			//Thread.sleep(1000);
			driver.findElement(By.xpath("//button[text()='OK']")).click();
		}
		

	
	@Test
	public void deveInserirUmNovoEstado() throws Exception {
		Estado estado = new Estado();
		estado.setNome("EstadoTeste");
		estado.setSigla("TE");
		
		estado = cadastroEstadoService.salvar(estado);
		assertNotNull(estado.getCodigo());
	
	}

	
	@After
	public void end() {
		this.manager.close();
	}
}
