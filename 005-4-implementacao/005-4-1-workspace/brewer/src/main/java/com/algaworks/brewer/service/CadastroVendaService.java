package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.service.event.venda.VendaCanceladaListener;
import com.algaworks.brewer.service.event.venda.VendaEmitidaListener;

@Service
public class CadastroVendaService {

	@Autowired
	private Vendas vendas;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	
	@Transactional
	public Venda salvar(Venda venda){
		if(venda.isSalvarProibido()){
			throw new RuntimeException("Usuário tentando salvar uma venda proibida");
			
		}
		
		// se for uma nova venda
		if(venda.isNova()){
			// seta data e hora atual da venda
			venda.setDataCriacao(LocalDateTime.now());
		
		// se estiver editanto usa a data de criacao da venda recuperada do banco
		}else{
			Venda vendaExistente = vendas.findOne(venda.getCodigo());
			venda.setDataCriacao(vendaExistente.getDataCriacao());
		}
				
		
		if(venda.getDataEntrega() != null){
			// formando a DataHoraEntrega, caso não tenha horario entrega, passe o horario do sistema 12:00  
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega()
					, venda.getHorarioEntrega() != null ? venda.getHorarioEntrega() : LocalTime.NOON));
			}
		
		 return vendas.saveAndFlush(venda);
				
	}
	
	

	@Transactional
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		salvar(venda);
		
		// Dispara o evento para as classes listener que escutam esse evento, 
		// atualizar o estoque apos emitir uma venda
		publisher.publishEvent(new VendaEmitidaListener(venda)); 
				
	}
	
	/** A anotacao "@PreAuthorize" permite fazer referencia ao objeto venda sendo passado no metodo.
	    "#venda.usuario == principal.usuario" - somente o usuario que criou a venda pode cancelar ou 
	    ou quem tem o papel - "or hasRole('CANCELAR_VENDA')". Esse controle faz parte do spring security 
	    e e necessario ter anotation @EnableGlobalMethodSecurity(prePostEnabled = true)
	    no arquivo SecurityConfig do spring, somente usuario com a role CANCELAR_VENDA pode cancelar uma venda */
	@PreAuthorize("#venda.usuario == principal.usuario or hasRole('CANCELAR_VENDA')") 
	@Transactional
	public void cancelar(Venda venda) {
		Venda vendaExistente = vendas.findOne(venda.getCodigo());
				
		// retorna produto para estoque, essa tecnica permite o desacomplemento do software 
		publisher.publishEvent(new VendaCanceladaListener(vendaExistente));
		
		vendaExistente.setStatus(StatusVenda.CANCELADA);
		vendas.save(vendaExistente);
	}

	
}
