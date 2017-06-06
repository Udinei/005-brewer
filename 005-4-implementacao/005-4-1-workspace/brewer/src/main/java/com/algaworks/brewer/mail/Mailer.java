package com.algaworks.brewer.mail;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.service.exception.EnderecoEmailClienteException;
import com.algaworks.brewer.storage.local.FotoStorageLocal;

@Component
public class Mailer {

	private static Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired 
	FotoStorageLocal fotoStorage;
	
	@Async
	public void enviar(Venda venda){
		
		Context context = new Context(new Locale("pt","BR"));
		
		// insere no context as variaveis com os dados que serão, recuperadas no email pelo thymeleaf
		context.setVariable("venda", venda);
		context.setVariable("logo","logo");
		
		// esse Map sera utilizado para recuperar os thumbmail das fotos		
		Map<String, String > fotos = new HashMap<>();
		
		boolean adicionarMockCerveja = false;
		
			for(ItemVenda item : venda.getItens()){
				Cerveja cerveja = item.getCerveja();
				
				if(cerveja.temFoto()){
					// criando chave do Map para recuperar foto
					String cid = "foto-" + cerveja.getCodigo();
					// seta no contexto as foto da cerveja, a variavel e seu conteudo ambos tem os mesmos nomes
					context.setVariable(cid, cid);
					
					// coloca no Map a foto da cerveja e seu contentType 
					fotos.put(cid, cerveja.getFoto() + "|" + cerveja.getContentType());
				
				// caso não tenha foto usa imagem mockCerveja
				}else{
					adicionarMockCerveja = true;
					context.setVariable("mockCerveja", "mockCerveja");
				}
			}
			
			
			try {
						String email = thymeleaf.process("mail/ResumoVenda", context);
						
						// criando objeto para enviar mensagem com html no email
						MimeMessage mimeMessage = mailSender.createMimeMessage();
						
						// true porque sera adicionado imagem no email, e enconding UTF-8
						MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
						
						helper.setFrom("udineisilva@gmail.com");      // quem esta enviando o email
						helper.setTo(venda.getCliente().getEmail());  // para que sera enviado o email
						helper.setSubject(String.format("Brewer - Venda nº %d", venda.getCodigo()));  // assunto
						helper.setText(email, true); // TRUE - para email que contem html
						
						// insere a imagem do logo na variavel logo, que sera recuperada no html			
						helper.addInline("logo", new ClassPathResource("static/images/logo-gray-brewer.png"));
						
						// se nao tiver foto adiciona na variavel mockCerveja a img mock da cerveja
						if(adicionarMockCerveja){
							helper.addInline("mockCerveja", new ClassPathResource("static/images/cerveja-mock.png"));
						}
						
						// recuperando o thumbnail da fogo 
						for (String cid: fotos.keySet()){             // percorre todos os itens do Map
							String[] fotoContentType = fotos.get(cid).split("\\|"); // retorna  para dentro do array a foto e o contentType
							String foto = fotoContentType[0];                       // obtem a foto 
							String contentType = fotoContentType[1];                  // obtem o contentType
							byte[] arrayFoto = fotoStorage.recuperarThumbnail(foto);  // recupera thumbnail da foto para um array de byte
							helper.addInline(cid,  new ByteArrayResource(arrayFoto), contentType);  // coloca na variavel cid o thumbnail e o contentType da foto
						}
					
					mailSender.send(mimeMessage);
			
		}catch (AddressException e) {
			  // ver depois como tratar essa mensagem 
			  throw new EnderecoEmailClienteException("Endereço de e-mail do cliente Inválido");
			  
		}catch (MessagingException e) {
             logger.error("erro enviando e-mail", e); 
		}
		
		
	}
}
