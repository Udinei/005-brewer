package com.algaworks.brewer.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.storage.FotoStorage;

/** Classe que fica escutando a entidade Cerveja, para quando toda vez que essa entidade for carregada do banco de dados
    tambem recupere a url do thumbnail da foto, que sera utilizada ao pesquisar uma cerveja */
public class CervejaEntityListener {

	@Autowired
	private FotoStorage fotoStorage;
	
	// final no parametro do metodo nao permite implementar a expressao - cerveja = null;
	@PostLoad
	public void postLoad(final Cerveja cerveja){
		// resolve no contexto corrente as injeção de dependencia, para todos os @Autowired da classe 
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		
		cerveja.setUrlFoto(fotoStorage.getUrl(cerveja.getFotoOrMock()));
		cerveja.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock()));
	}
}