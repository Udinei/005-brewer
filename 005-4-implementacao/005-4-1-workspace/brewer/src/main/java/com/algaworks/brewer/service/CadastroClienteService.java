package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.service.exception.CpfCnpjClienteJaCadastradoException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.util.MessagesUtil;

@Service
public class CadastroClienteService {

	@Autowired
	private Clientes clientes;
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Transactional
	public void salvar(Cliente cliente){
		Optional<Cliente> clienteExistente = clientes.findByCpfOuCnpj(cliente.getCpfOuCnpjSemFormatacao());
				
		if(clienteExistente.isPresent() && !clienteExistente.get().equals(cliente)){
			throw new CpfCnpjClienteJaCadastradoException(messagesUtil.getMessage("msg.error.atrib.ent.ja.cadastrado","CPF/CNPJ", "cliente"));
		}
		
		isEnderecoSemCidadeInformada(cliente);
		
		clientes.saveAndFlush(cliente);
	}


	private void isEnderecoSemCidadeInformada(Cliente cliente) {
		if(cliente.getEndereco().getCidade().getCodigo() == null){
			cliente.getEndereco().setCidade(null);
		}
	}
	
	

	@Transactional
	public void excluir(Cliente cliente){
		try{
			
			clientes.delete(cliente);
			clientes.flush();
						
		}catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cliente. Esta sendo usado em alguma venda.");
			
		}
	}
}
