Brewer.TabelaItens = (function(){
	
	function TabelaItens(autocomplete){
		this.autocomplete = autocomplete;
		this.tabelaCervejasContainer =$('.js-tabela-cervejas-container');
		this.uuid = $('#uuid').val(); //usado para controlar a sessão do usuario caso abra duas abas
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
		
	}
	
	TabelaItens.prototype.iniciar = function (){
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this)); // dispara o evento item-selecionado, e onItemSelecionado recebe seu retorno 
		
		bindQuantidade.call(this);
		bindTabelaItem.call(this);
	}
	
	// novo metodo prototype, para essa funcao, poder ser acessada de fora desse javascript
	TabelaItens.prototype.valorTotal = function(){
		return this.tabelaCervejasContainer.data('valor');
	}
	
	// enviado para o servidor o  item selecionado na lista do easycomplete
	function onItemSelecionado(evento, item){
		var resposta = $.ajax({
			url: 'item',  
			method: 'POST',
			data: {
				codigoCerveja: item.codigo,
				uuid: this.uuid
			}
			
		});
		
		// renderiza na tela o html passado por onItemAtualizadoNoServidor
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onItemAtualizadoNoServidor(html){
		this.tabelaCervejasContainer.html(html);  // renderiza o html "TabelaIntesVenda - carrinho de compras" no elemento div "tabelaCervejasContainer" com os dados vindo do servidor
		
		bindQuantidade.call(this); // aplica mascara e diponibiliza funcionalidade ao sair do campo quantidade
		var tabelaItem = bindTabelaItem.call(this); // diponibiliza funcionalidade de exclusao, um duplo click no item da tabela
		
		this.emitter.trigger('tabela-itens-atualizada', tabelaItem.data('valor-total')); // dispara evento tabela-itens-atualizada
		                                                                                 // e retorna o valorTotal atualizado, o javascrit venda.js
		                                                                                 // escuta esse evento. na tela o valor total apos a exclusao de um item
	}
	
	
	function bindQuantidade(){
		var quantidadeItemInput = $('.js-tabela-cerveja-quantidade-item');
		quantidadeItemInput.on('blur', onQuantidadeItemAlterado.bind(this)); // ao sair do campo, submete ao servidor para atualiza os dados da tela "quantidade" 
		quantidadeItemInput.maskNumber({ integer: true, thousands: '' });  // aplica a mascara
		//quantidadeItemInput.maskMoney({ precision: 0, thousands: '' });  // antes de maskNumber
	}
	
	
	function bindTabelaItem(){
		var tabelaItem = $('.js-tabela-item');
		tabelaItem.on('dblclick', onDoubleClick);
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));  // ao clicar no botao de exclusao
		
		return tabelaItem;
	}
	
	function onQuantidadeItemAlterado(evento){
		var input = $(evento.target); // obtem elemento html em que ocorreu o  evento  blur campo "quantidade"
		var quantidade = input.val(); // obtem o valor do elemento 
		
		// não permite quantidade ser menor ou igual a zero
		if(quantidade <= 0){
			input.val(1);
			quantidade =1
		}
		
		// obtem o codigo do item
		var codigoCerveja = input.data('codigo-cerveja');
		
		// submete ao servidor a requisicao via ajax
		var resposta = $.ajax({
			url: 'item/'+ codigoCerveja,   // passa o codigo do item    
			method: 'PUT',                 // requisicao de alteracao de dados
			data: {
				quantidade: quantidade,    // envia dados - quantidade e uuid  
				uuid: this.uuid
			}
			
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));  // ao receber a resposta do servidor, atualiza na tela o html do carrinho de compras "TabelaItensVenda"  
	}
	
	function onDoubleClick(evento){
		$(this).toggleClass('solicitando-exclusao'); // $(this) retorna o currentTarget
		
		/** mesma forma de trabalhar acima, so que expandida  
		var item = $(evento.currentTarget); // para pegar a div e não elemento html
		item.toggClass('solicitando-exclusao');
		*/
		
	}
	
	
	// deleta um item da tabela via ajax
	function onExclusaoItemClick(evento){
		var codigoCerveja = $(evento.target).data('codigo-cerveja');
		var resposta = $.ajax({
			url: 'item/' + this.uuid + '/' + codigoCerveja,
			method: 'DELETE'
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this)); // atualiza a tabela com retorno vindo do servidor
	}
	
	
	return TabelaItens;  // retorna a tabela de itens
	
}());

