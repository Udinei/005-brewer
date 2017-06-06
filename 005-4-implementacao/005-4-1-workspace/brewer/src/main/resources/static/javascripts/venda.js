Brewer.Venda = (function(){
		
	function Venda(tabelaItens){
		this.tabelasItens = tabelaItens;
		this.valorTotalBox = $('.js-valor-total-box');
		this.valorFreteInput = $('#valorFrete');
		this.valorDescontoInput = $('#valorDesconto');
		this.valorTotalBoxContainer = $('.js-valor-total-box-container');
		
		// usando a function TabelaItens.prototype.valorTotal, declarada no js venda.tabela-itens.js 
		this.valorTotalItens = this.tabelasItens.valorTotal();
		
		this.valorFrete = this.valorFreteInput.data('valor');
		this.valorDesconto = this.valorDescontoInput.data('valor');
		}
	
	
	Venda.prototype.iniciar = function (){
		this.tabelasItens.on('tabela-itens-atualizada', onTabelaItensAtualizada.bind(this));
		this.valorFreteInput.on('keyup', onValorFreteAlterado.bind(this));
		this.valorDescontoInput.on('keyup', onValorDescontoAlterado.bind(this));
		
		this.tabelasItens.on('tabela-itens-atualizada', onValoresAlterados.bind(this));
		this.valorFreteInput.on('keyup', onValoresAlterados.bind(this));
		this.valorDescontoInput.on('keyup', onValoresAlterados.bind(this));
		
		onValoresAlterados.call(this);
	}

	// obtem o valorTotal retornado pelo triguer'tabela-itens-atualizada', disparado no js venda.tabela-itens.js e o evento 
	function onTabelaItensAtualizada(evento, valorTotalItens){
		this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens;
	}
	
	// ao digitar no input frete
	function onValorFreteAlterado(evento){
		var frete = $(evento.target).val();
		this.valorFrete = Brewer.recuperarValor(frete == null ? 0 : frete );
		
	}
	
	function onValorDescontoAlterado(evento){
		var desconto = $(evento.target).val();
		this.valorDesconto = Brewer.recuperarValor(desconto == null ? 0 : desconto);
		
		
	}
	
	function onValoresAlterados(){
        var valorTotal = parseFloat(this.valorTotalItens) + parseFloat(this.valorFrete) - parseFloat(this.valorDesconto);
		this.valorTotalBox.html(Brewer.formatarMoeda(valorTotal));
		
		// quando for negativo o valor total, altera a cor do mesmo para vermelho 
		this.valorTotalBoxContainer.toggleClass('negativo', valorTotal < 0);
	}
	
	return Venda;
	
	
}());

$(function(){
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();
	
	
	var venda = new Brewer.Venda(tabelaItens)
	venda.iniciar();
	
	
});