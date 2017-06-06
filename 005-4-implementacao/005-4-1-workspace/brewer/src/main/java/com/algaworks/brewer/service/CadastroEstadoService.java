package com.algaworks.brewer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.Estados;

@Service
public class CadastroEstadoService {
	
	@Autowired
	Estados estados;
	
	@Transactional
	public Estado salvar(Estado estado){
		  return estados.save(estado);
		 
	}

}
