package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.SkuCervejaJaCadastradaException;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.util.MessagesUtil;

@Service
public class CadastroCervejaService {
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Transactional
	public void salvar(Cerveja cerveja){
		
	Optional<Cerveja> cervejaExistente = cervejas.findBySkuIgnoreCase(cerveja.getSku());
		
		// objeto com o atributo pesquisado existe, e seu identificador é igual ao do objeto a salvar   
		if(cervejaExistente.isPresent() && !cervejaExistente.get().equals(cerveja)){
			throw new SkuCervejaJaCadastradaException(messagesUtil.getMessage("msg.error.atrib.ent.ja.cadastrada.m", "SKU", "cerveja"));
		}
		
		cervejas.save(cerveja);
	}
	
	
	@Transactional
	public void excluir(Cerveja cerveja){
		try{
			String foto = cerveja.getFoto();
			cervejas.delete(cerveja);
			
			cervejas.flush(); // executa a exclusao no banco 
			fotoStorage.excluir(foto); // apagando a foto e thumbnail da foto
			
		}catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Esta sendo usada em alguma venda.");
			
		}
		
	}
}
