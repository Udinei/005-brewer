package com.algaworks.brewer.aceitacao;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LoginItTest {
	
	 	public static WebDriver driver;
	    public static WebElement query;
	    
	    
	    @BeforeClass
	    public static void setUp() throws InterruptedException{
	    	// TODO mudar o driver para dentro de uma pasta do projeto
	    	System.setProperty("webdriver.chrome.driver", "E:\\Selenium\\chromedriver.exe");
	    	ChromeOptions options = new ChromeOptions();
	    	options.addArguments("disabled-popup-blocking");
	    	options.addArguments("start-maximized");
	    	
	    	Map<String, Object> prefs = new HashMap<String, Object>();
	    	prefs.put("credentials_enable_service", false); // desabilita solicitacao para salvar senha
	    	prefs.put("profile.password_manager_enabled", false); // desabilita uso de profile do usuario
	    	options.setExperimentalOption("prefs", prefs);
	    	
	        driver = new ChromeDriver(options);

	        driver.get("http://localhost:8080");
			
	    }
	    
	    /** Entra com login e senha correta log e desloga */
	    @Test
	    public void loginFluxoPrincipal() throws InterruptedException{
			
			Thread.sleep(2000);
			driver.findElement(By.name("username")).sendKeys("admin@brewer.com");
		    driver.findElement(By.name("password")).sendKeys("admin");
		    				
		    driver.findElement(By.className("btn-primary")).click();
		    
		    Thread.sleep(2000);
		    assertEquals(isElementPresent(By.className("fa-sign-out")), true);
		    
		    driver.findElement(By.className("fa-sign-out")).click(); // faz logout
		    
	    }
	    
	    /** Valida o login e email, com exibicao de mensagens para entradas nulas e invalidas  */
	    @Test
	    public void loginFluxoExcecao() throws InterruptedException{
	    	validaSoEmailLogin();
	    	validaSoSenhaLogin();
	    
	    }
	
	    /** verifica se a tag passada no parametro esta presente no html */	
		private boolean isElementPresent(By by) {
			    try {
			      driver.findElement(by);
			      return true;
			    } catch (NoSuchElementException e) {
			      return false;
			    }
		  }
		  
		  
		    @AfterClass
		    public static void cleanUp() throws InterruptedException{
		        if (driver != null) {
		        	Thread.sleep(10000);
		            driver.close();
		            driver.quit();
		        }
		    }

			private void validaSoEmailLogin() throws InterruptedException {

				// entrada errada email
				Thread.sleep(2000);
				driver.findElement(By.name("username")).sendKeys("email@errado.com"); 
				
			    // tenta acessar sistema
				Thread.sleep(2000);
			    driver.findElement(By.className("btn-primary")).click();
				
				// exibe mensagem de validacao
				Thread.sleep(2000);
				assertEquals("O E-mail e/ou a senha não conferem.", driver.findElement(By.xpath("//div[contains(text(),'O E-mail e/ou a senha não conferem.')]")).getText());
				
				/** entrada vazia ou nullo */
				Thread.sleep(2000);
				driver.findElement(By.name("username")).sendKeys(""); 
				
			    // tenta acessar sistema
				Thread.sleep(2000);
			    driver.findElement(By.className("btn-primary")).click();
				
				// exibe mensagem de validacao
				Thread.sleep(2000);
				assertEquals("O E-mail e/ou a senha não conferem.", driver.findElement(By.xpath("//div[contains(text(),'O E-mail e/ou a senha não conferem.')]")).getText());

			}
		    
			
			private void validaSoSenhaLogin() throws InterruptedException {
				// entrada errada email
				Thread.sleep(2000);
				driver.findElement(By.name("password")).sendKeys("senhaErrada"); 
				
			    // tenta acessar sistema
				Thread.sleep(2000);
			    driver.findElement(By.className("btn-primary")).click();
				
				// validacao email
				Thread.sleep(2000);
				assertEquals("O E-mail e/ou a senha não conferem.", driver.findElement(By.xpath("//div[contains(text(),'O E-mail e/ou a senha não conferem.')]")).getText());
				
				/** entrada vazia ou nullo */
				Thread.sleep(2000);
				driver.findElement(By.name("password")).sendKeys(""); 
				
			    // tenta acessar sistema
				Thread.sleep(2000);
			    driver.findElement(By.className("btn-primary")).click();
				
				// validacao email
				Thread.sleep(2000);
				assertEquals("O E-mail e/ou a senha não conferem.", driver.findElement(By.xpath("//div[contains(text(),'O E-mail e/ou a senha não conferem.')]")).getText());
				
			}
}
