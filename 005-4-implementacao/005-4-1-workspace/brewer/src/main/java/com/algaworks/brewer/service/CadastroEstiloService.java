package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeEstiloJaCadastradoException;

@Service
public class CadastroEstiloService {
	
	@Autowired
	private Estilos estilos;
	
	/** Salva ou altera o objeto informado
	    controle de transação manual, necessario para evitar que o spring faça begin end nas consultas tambem
	    retorna estilo com o codigo, para preenchimento da combobox apos o cadastro rapido
	 */
	@Transactional 
	public Estilo salvar(Estilo estilo){
		Optional<Estilo> estiloExistente = estilos.findByNomeIgnoreCase(estilo.getNome());
		
		// objeto com o atributo pesquisado existe, e seu identificador é igual ao do objeto a salvar   
		if(estiloExistente.isPresent() && !estiloExistente.get().equals(estilo)){
			throw new NomeEstiloJaCadastradoException("Nome do estilo já cadastrado"); // entao lanca exceptiono
		}
		
		// salva um novo objeto, ou altera caso identificador do objeto ja existir 
		return estilos.saveAndFlush(estilo);
	}

	
	@Transactional
	public void excluir(Estilo estilo){
		try{
			
			estilos.delete(estilo);
			estilos.flush();
						
		}catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível apagar estilo. Esta sendo usado em alguma cerveja.");
			
		}
		
	}

	
}
