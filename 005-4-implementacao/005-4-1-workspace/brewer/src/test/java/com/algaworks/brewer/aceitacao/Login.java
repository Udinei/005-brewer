package com.algaworks.brewer.aceitacao;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Login {
	
	public static WebDriver driver;
   
    
    @BeforeClass
    public static void setUp() throws InterruptedException{
    	System.setProperty("webdriver.chrome.driver", "E:\\Selenium\\chromedriver.exe");
    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("disabled-popup-blocking");
    	options.addArguments("start-maximized");
    	
    	Map<String, Object> prefs = new HashMap<String, Object>();
    	prefs.put("credentials_enable_service", false); // desabilita solicitacao para salvar senha
    	prefs.put("profile.password_manager_enabled", false); // desabilita uso de profile do usuario
    	options.setExperimentalOption("prefs", prefs);
    	
    	
        driver = new ChromeDriver(options);
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        
        driver.get("http://localhost:8080");
		
    }
    
    /** Loga no sistema */
    @Test
    public void loginFluxoPrincipal(String user, String password) throws InterruptedException{
		

    	if(user.equals("") && password.equals("") ){
			driver.findElement(By.name("username")).sendKeys("admin@brewer.com");
		    driver.findElement(By.name("password")).sendKeys("admin");
		    
    	}else{
    		driver.findElement(By.name("username")).sendKeys(user);
    	    driver.findElement(By.name("password")).sendKeys(password);
    	}
    	
    	driver.findElement(By.className("btn-primary")).click();
    
    }
    
	  
	  
	    @AfterClass
	    public static void cleanUp() throws InterruptedException{
	        if (driver != null) {
        	//Thread.sleep(5000);
        	//	driver.close();
        	//	driver.quit();
	        }
	    }
	    
	    public WebDriver getWebdriver(){
	    	return driver;
	    }
	    
	    public boolean isElementPresent(By by) {
	        try {
	          driver.findElement(by);
	          return true;
	        } catch (NoSuchElementException e) {
	          return false;
	        }
	      }

}
