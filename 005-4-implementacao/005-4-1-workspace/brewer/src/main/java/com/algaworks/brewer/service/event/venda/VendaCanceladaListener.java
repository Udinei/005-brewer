package com.algaworks.brewer.service.event.venda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;

@Component
public class VendaCanceladaListener extends VendaEvent {

	@Autowired
	Cervejas cervejas;
	
	public VendaCanceladaListener() {
		
	}

	public VendaCanceladaListener(Venda venda) {
		super(venda);
	}
	
	@EventListener //caso essa anotação não for usada o metodo não sera executado
	public void vendaCanceladaAtualizaEstoque(VendaCanceladaListener vendaEvent){
	    
		// se status da venda = EMITIDA
		if(vendaEvent.getVenda().getStatus().equals(StatusVenda.EMITIDA)){

			// percorre os itens da venda
			for(ItemVenda item : vendaEvent.getVenda().getItens()){
					Cerveja cerveja = cervejas.findOne(item.getCerveja().getCodigo());
					
					// para cada item soma ao estoque a quantidade da venda cancelada
					cerveja.setQuantidadeEstoque(cerveja.getQuantidadeEstoque() + item.getQuantidade());
					
					cervejas.save(cerveja);
			}
		}
	}

}
