package com.algaworks.brewer.model;

public enum Origem {
	
	// sera salvo no BD: O texto em maiusculo o texto entre aspas sera exibido na tela 
	NACIONAL("Nacional"),
	INTERNACIONAL("Internacional");
	
	private String descricao;
	
	Origem(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return descricao;
	}

}
