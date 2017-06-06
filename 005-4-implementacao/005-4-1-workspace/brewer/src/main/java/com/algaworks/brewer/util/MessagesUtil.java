package com.algaworks.brewer.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.ibm.icu.text.MessageFormat;

@Component
public class MessagesUtil {
	
	//Locale locale = new Locale("pt", "BR"); 
	//static ResourceBundle bundle = ResourceBundle.getBundle("messages",  Locale.getDefault());
	 private static final String BUNDLE_NAME = "messages";
	 private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	 
	 public MessagesUtil() {	    }
	 
	 public String getString(String key) {
	        try {
	            return RESOURCE_BUNDLE.getString(key);
	        } catch (MissingResourceException e) {
	            return '!' + key + '!';
	        }
	 }
	 
	  public String getMessage(String key, Object... params  ) {
	        try {
	            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
	        } catch (MissingResourceException e) {
	            return '!' + key + '!';
	        }
	    }
	

	
	
//	public static void main(String[] args) {
//		System.out.println(">>> " + getMessage("msg.error.atrib.ent.ja.cadastrado", "Nome", "Cliente"));
//	}
	
	
//	public String getMessage(){
//		return bundle.getString("")
//	}

}
