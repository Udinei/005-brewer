package com.algaworks.brewer.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class MakeUrl {
	
	public URL makeUrl()	{
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
	     URL url = null;
		try {
			url = new URL(request.getRequestURL().toString());
						
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	     return url;
	}

	
	public String urlBrowser(){
	    return  makeUrl().getProtocol() + "://" + makeUrl().getHost() + ":" + makeUrl().getPort();
	}

}
