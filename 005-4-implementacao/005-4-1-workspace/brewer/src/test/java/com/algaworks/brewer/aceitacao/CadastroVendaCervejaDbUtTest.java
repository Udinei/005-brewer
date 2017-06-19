package com.algaworks.brewer.aceitacao;

import java.util.concurrent.TimeUnit;

import org.dbunit.operation.CompositeOperation;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;

import com.algaworks.brewer.base.BaseTest;
import com.algaworks.brewer.base.DbUnitHelper;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.service.CadastroCervejaService;


public class CadastroVendaCervejaDbUtTest extends BaseTest { 

	private String querySQL = "";
	
	@Autowired
	private Cervejas cervejas;

	@Autowired
	CadastroCervejaService cadastroCervejaService;

	@BeforeClass
	public static void initClass() {
				
		// Valido para todo o testes, aguarda até trinta segundos por um determinado
		// elemento acessivel, legivel ou clicavel no DOM 
		driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
				
	}

	
	
	@Before
	public void init() {
		
		  // zera o autoincremento da tabela venda, para ser consistente com as msg exibidas na tela de venda  
		  querySQL = "ALTER TABLE venda AUTO_INCREMENT = 0";
		  
         // passa informações de conexao de banco para o DBUnit e pasta de acesso do .xml de controle do BD
		dbUnitHelper = new DbUnitHelper(setBaseBD(), "META-INF", querySQL);
		
		// executa a conexao
		dbUnitHelper.conectaBD();	

		// Controla os dados inseridos no banco, que serão removidos apos os testes 
		//dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "BrewerXmlDBData.xml");
		dbUnitHelper.execute(DatabaseOperation.CLEAN_INSERT, "BrewerXmlDBData.xml");
		
	}
	
	
	@Test
	public void fluxoPrincipal() throws Exception {
		
		/** A sequencia de chamada dos metodos abaixo, parte do principio de que todos os comandos serão executados 
		 a partir da  tela de cadastro */
		  // preparando cliente e itens que serão usados na venda
		  deveCadastrarUmClienteParaVenda();  
		  deveCadastrarUmaCervejaParaVenda();
		  deveCadastrarOutraCervejaSeraUsadaNaEdicao(); // cadastra uma nova cerveja que sera usada na edicao da venda
		  
		  // na venda
		  deveExibirMsgDePrenchimentoCamposObrigatorio();
		  deveCadastrarUmaNovaVenda();
		  deveSalvarEmitirUmaVendaCadastrada();
		  deveSalvarEnviarVendaPorEmail();
			
		  devePesquisarRegistroCadastrado();
          deveEditarRegistroCadastradoPesquisado();
          deveExcluirUmItemDaVenda();
          deveExibirMsgNenhumaEntidadePesquisadaEncontrada();
          naoDevePermitirVendedorCancelarVendaEmitida();
          devePermitirVendedorAdminCancelarVendaEmitida();
	     
	}
	
	
	public void deveExibirMsgDePrenchimentoCamposObrigatorio() throws InterruptedException{
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Cadastro de venda");
		clickButtonSalvarClassName("btn-primary");
		Thread.sleep(5000);
		validaMsgErrorInTextContains("Adicione pelo menos uma cerveja na venda");
		validaMsgErrorInTextContains("Selecione um cliente na pesquisa rápida");
		 
		clickButtonSairFormularioCadastro("btn-default");   
	}

	public void deveCadastrarUmaNovaVenda() throws InterruptedException {
		driver.get("http://localhost:8080");
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Cadastro de venda");
		preencheFormularioDeDados();
		clickButtonSalvar();
        // valida msg exibida usuario, apos salvar formulario de cadastro   
		validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Venda", "Venda salva com sucesso!");
		clickButtonPesquisa();
		  
	}


	private void deveSalvarEnviarVendaPorEmail() throws InterruptedException {
		clickButtonEditar();
		clickLinkButtonSetaCaret("Salvar e enviar por e-mail");
		validaMsgSemChaveExibidaFoiHaMensagem("Venda nº 1 salva com sucesso e e-mail enviado");
		clickButtonPesquisa();
	
	}


	private void deveSalvarEmitirUmaVendaCadastrada() throws InterruptedException {
		clickButtonEditar();
		Thread.sleep(500);
		clickLinkButtonSetaCaret("Salvar e emitir");
		validaMsgSemChaveExibidaFoiHaMensagem("Venda salva e emitida com sucesso");
		clickButtonPesquisa();
	}
	
	public void deveCadastrarUmaCervejaParaVenda() throws InterruptedException {
		driver.get("http://localhost:8080/cervejas");
		CadastroCervejaDbUtTest cerveja = new CadastroCervejaDbUtTest();
		cerveja.deveCadastrarUmNovoRegistro();
	}	
	
	
	
	public void deveCadastrarUmClienteParaVenda() throws InterruptedException{
		driver.get("http://localhost:8080/clientes");
		CadastroClienteDbUtTest cliente = new CadastroClienteDbUtTest();
		cliente.deveCadastrarUmNovoCliente();
	}
	
	
	public void devePesquisarRegistroCadastrado() throws InterruptedException{
	   // recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisa();
		Thread.sleep(300);
		clickButtonSalvarClassName("btn-primary");
		Thread.sleep(200);
		validaSeExibidaEmTabelaFoiHaMensagem("Juliana dos Santos");
		Thread.sleep(5000);
		
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
    	validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Venda", "Venda salva com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");  
		
	}
	
	public void deveExcluirUmItemDaVenda() throws InterruptedException{
		driver.get("http://localhost:8080");
		clickLinkMenu("Vendas");
		Thread.sleep(100);
		clickLinkItemMenuOrLinkAba("Pesquisa venda", wait, action);
		clickButtonEditar();
		
 
		 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//text()[contains(.,'Becks Long Neck')]/ancestor::div[1]")));
		 if(element.isDisplayed()){
				//Double click
				action.doubleClick(element).perform();
		 }
		 
		 WebElement excluir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Sim, excluir!']")));
		 if(element.isDisplayed()){
				//Double click
				action.doubleClick(excluir).perform();
		 }
		 
		Thread.sleep(5000);
    	clickButtonSalvarClassName("btn-primary");
		validaMsgSucessWithKeyInSpanText("msg.salva.sucesso", "Venda", "Venda salva com sucesso!");
		clickButtonSairFormularioCadastro("btn-default");
	}
	
	
	public void deveExibirMsgNenhumaEntidadePesquisadaEncontrada(){
		// recebe como parametro campo e valor retornado na pesquisa
		preencheCamposDePesquisaNenhumaEntidadeEncontrada();
		clickButtonSalvarClassName("btn-primary");
		validaSeExibidaEmTabelaFoiHaMensagem("Nenhuma venda encontrada");
		
	}
	
	
	public void naoDevePermitirVendedorCancelarVendaEmitida() throws InterruptedException{
		// desloga do sistema
		clickLogOut(); 
		// loga no sistema como usario vendedor
		driver.get("http://localhost:8080");
		login.loginFluxoPrincipal("miguel@nn.com","miguel");
		
		// Tenta cancelar venda feita pelo usuario admin
		Thread.sleep(5000);
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Pesquisa venda");
		Thread.sleep(1000);
		preencheCamposDePesquisa();
		clickButtonPesquisar("btn-primary");
		clickButtonEditar();
		Thread.sleep(1000);
		clickButtonCancelar();
		Thread.sleep(6000);
		validaMsgSemChaveH2ExibidaFoiHaMensagem("Acesso negado"); // exibem mensagem 403 acesso negado
		Thread.sleep(3000);
    	clickButtonClassName("btn-primary"); // volta para pagina de venda
    	
	}
	
	public void devePermitirVendedorAdminCancelarVendaEmitida() throws InterruptedException{
		// desloga do sistema
		clickLogOut(); 
		// loga no sistema como usario vendedor
		driver.get("http://localhost:8080");
		login.loginFluxoPrincipal("admin@brewer.com","admin");
		
		// Tenta cancelar venda feita pelo usuario admin
		clickLinkMenu("Vendas");
		clickLinkItemMenuOrLinkAba("Pesquisa venda");
		Thread.sleep(1000);
		preencheCamposDePesquisa();
		clickButtonPesquisar("btn-primary");
		clickButtonEditar();

		Thread.sleep(1000);
		clickButtonCancelar();
		Thread.sleep(2000);
		validaMsgSemChaveExibidaFoiHaMensagem("Venda cancelada com sucesso!");
    	clickLogOut(); // sai do sistema
	}

	
	public void deveExcluirRegistroPesquisadoEncontradoNaoUsadoEmOutroCadastro() throws InterruptedException{
 			Thread.sleep(1000);
			clickButtonExcluirRegistroPesquisadoComSubString("Cerveja");
			clickButtonOkAlertExcluirRegistro();
	}

		
	public void preencheFormularioDeDados() throws InterruptedException {
		// na tela de cadastro informa registro de teste 
		// obtem o identificador da janela mestre antes de clicar no link de chamada da janela modal/popup
		String janelaMaster = driver.getWindowHandle();
		
		driver.findElement(By.xpath("//*[@class='glyphicon  glyphicon-search']")).click();
		
		driver.findElement(By.id("nomeClienteModal")).sendKeys("Juliana");
		clickButtonPesquisar("js-pesquisa-rapida-clientes-btn");
		
		Thread.sleep(100);
		driver.findElement(By.xpath("//td[text()='Juliana dos Santos']")).click();
		
		Thread.sleep(200);
		preencheCampoComMascaraSemClick("valorFrete", "200");
		preencheCampoComMascaraSemClick("valorDesconto", "100");
		clickLinkItemMenuOrLinkAba("Entrega");
		
		Thread.sleep(100);
		driver.findElement(By.xpath("//input[@type='text'][@name='dataEntrega']")).sendKeys("25/10/2020");
		preencheCampoComMascara("horarioEntrega", "1945");
		driver.findElement(By.xpath("//textarea[@name='observacao']")).sendKeys("Entregar somente após as 09:00");
		driver.findElement(By.cssSelector("a[href='#cervejas']")).click();
		driver.findElement(By.id("cerveja")).sendKeys("AA1234");
		Thread.sleep(100);
		driver.findElement(By.xpath("//text()[contains(.,'Becks Long Neck')]/ancestor::div[1]")).click();
		
		Thread.sleep(100);
		driver.findElement(By.name("valorUnitario")).clear();
		driver.findElement(By.name("valorUnitario")).sendKeys("2");
	
		driver.findElement(By.id("cerveja")).click();
		
		mantemJanelaMestreAguardaReturnModalOrPopup(janelaMaster);
		    
	}
	
	
	public void preencheCamposDePesquisa(){
		String[] dataCriacao = retornaDataHojeHeAmanha();
			
		// na tela de cadastro informa registro de pesquisa 
		driver.findElement(By.name("codigo")).sendKeys("1");
		selectComboxVisibleValue("status","Orçamento");
		preencheCampoComMascara("desde", dataCriacao[0]);
		preencheCampoComMascara("ate", dataCriacao[1]);
		driver.findElement(By.name("valorMinimo")).sendKeys("1");
		driver.findElement(By.name("valorMaximo")).sendKeys("2000");
		driver.findElement(By.name("nomeCliente")).sendKeys("Juliana dos Santos");
		preencheCampoComMascara("cpfOuCnpjCliente", "54305209187");
	}

	public void preencheCamposDePesquisaNenhumaEntidadeEncontrada(){
		driver.findElement(By.name("codigo")).sendKeys("10");
		selectComboxVisibleValue("status","Emitida");
	}
	
	public void preencheCamposDaEdicao() throws InterruptedException{
		
		driver.findElement(By.cssSelector("a[href='#cervejas']")).click();
		driver.findElement(By.id("cerveja")).sendKeys("BB1234");
		driver.findElement(By.xpath("//text()[contains(.,'Cerva Negra')]/ancestor::div[1]")).click();
		
		Thread.sleep(100);
		driver.findElement(By.name("valorUnitario")).clear();
		driver.findElement(By.name("valorUnitario")).sendKeys("3");
	
	}
	
	private void deveCadastrarOutraCervejaSeraUsadaNaEdicao() throws InterruptedException{
		driver.get("http://localhost:8080/cervejas");
		CadastroCervejaDbUtTest cerveja = new CadastroCervejaDbUtTest();
		
		cerveja.deveCadastrarUmNovoRegistroNovaCerveja();
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
