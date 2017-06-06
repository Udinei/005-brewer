package com.algaworks.brewer.repository.listener;

import java.net.URI;

import javax.persistence.PostLoad;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.storage.FotoStorage;

/** Classe que fica escutando a entidade Cerveja, para quando toda vez que essa entidade for carregada
 *  do banco de dados tambem recupere a url do thumbnail da foto, que sera utilizada ao pesquisar uma 
 *  cerveja;
 *  Essa classe nao e iniciado pelo Spring mas pelo JPA  */
@Component
public class CervejaEntityListener {

//	@Autowired
//	private FotoStorage fotoStorage;
	
	
	/** 
	 * Esse metodo exibe o thumbnail da foto ou Mock da foto na listagem, para tanto as imagem devem estar gravadas
	 * na pasta do sistema .fotobrewer inclusive a imagem do mock cerveja-mock.png 
	 *  Nota: A expressao final no parametro do metodo, nao permite implementar a expressao - cerveja = null;
	 * */
	@PostLoad
	public void postLoad(final Cerveja cerveja){
		// resolve no contexto corrente as injeção de dependencia, para todos os @Autowired da classe 
		//SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        
		// TODO: foi removido da url da foto  FotoStorage.getURL, que apontava para localhost  
		// recupera a foto da cerveja. A url sera concatenada dinamicamente com uso de javascript, ao ser exibida a imagem 
		cerveja.setUrlFoto(cerveja.getFotoOuMock());
		cerveja.setUrlThumbnailFoto(FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock());

	}
}
