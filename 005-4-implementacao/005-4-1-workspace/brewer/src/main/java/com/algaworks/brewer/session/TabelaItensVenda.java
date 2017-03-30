package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;


class TabelaItensVenda {

	private String uuid;
	private List<ItemVenda> itens = new ArrayList<>();

	public TabelaItensVenda(String uuid) {
		this.uuid = uuid;
	
	}
	
	/** calcula o valor total da venda */
	public BigDecimal getValorTotal(){
	
		// percorre a lista de ItemVenda - stream especie de interator do java 8
		return itens.stream()
	               .map(ItemVenda::getValorTotal) // para cada um dos itens, obtem o valor total do item	
	               .reduce(BigDecimal::add)       // soma todos os valores total 
	               .orElse(BigDecimal.ZERO);      // caso não tenha nenhum item retorna zeo
	}
	
	/** adicionar item da venda no carrinho de compras */
	public void adicionarItem(Cerveja cerveja, Integer quantidade){
		
		// filtra  o item adicionado
		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);
		
		ItemVenda itemVenda = null;
		
		//  caso o item ja exista no carrinho de compras altere somente a quantidade
		if(itemVendaOptional.isPresent()){
			itemVenda = itemVendaOptional.get();
			
			//altera a quantidade somando a quantidade atual + a nova quantidade informada 
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
			
		// 	adicione um novo item no carrinho
		}else{
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());
			
			// possiciona o novo item criado na primeira posicao da lista
			itens.add(0, itemVenda);
			
		}
	}

	/** filtra lista de intens */
	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		// retorna somente ItemVenda iguais ao passado no parametro 
		return itens.stream()
				.filter(i -> i.getCerveja().equals(cerveja))
				.findAny();
		
	}
	
	
	public void alterarQuantidadeItens(Cerveja cerveja, Integer quantidade){
		// filtra e acessa o item
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
		itemVenda.setQuantidade(quantidade);
		
		
	}
	
	public void excluirItem(Cerveja cerveja){
		
		/** IntStream - java 8 gera uma sequencia numerica
		    percorre a lista de itens e retorna o indice, do item igual ao passado no parametro do metodo */
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getCerveja().equals(cerveja)) 
		        .findAny().getAsInt(); // retorna o valor do indice
		
		// remove o item da lista, pelo indice do item passado como parametro
		itens.remove(indice);
	}
	
	public int total(){
		return itens.size();
	}

	public List<ItemVenda> getItens() {
		return itens;
	}

	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	
	public String getUuid() {
		return uuid;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabelaItensVenda other = (TabelaItensVenda) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	
	
}
