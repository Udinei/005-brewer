Brewer = Brewer || {};

Brewer.MultiSelecao = (function() {
	
	// metodo construtor acessa os componentes da tela, via atributo pelo nome dado no atributo classe do componente
	function MultiSelecao(){
		this.statusBtn = $('.js-status-btn');
		this.selecaoCheckbox = $('.js-selecao'); // seleciona todos botões que tem essa classe js-selecao
		this.selecaoTodosCheckBox = $('.js-selecao-todos');
	}
	
	// função que captura a acao realizada no componete da tela pelo usuario ou sistema e define qual funcao
	// ira tratar o evento ocorrido
	MultiSelecao.prototype.iniciar = function (){
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
		this.selecaoTodosCheckBox.on('click', onSelecaoTodosClicados.bind(this));
		this.selecaoCheckbox.on('click', onSelecaoClicado.bind(this));
		
	}
	
	// funcao que trata o evento ocorrido no componente da tela
	function onStatusBtnClicado(event){
		var botaoClicado = $(event.currentTarget); // qual botao foi clicado na tela
		var status = botaoClicado.data('status') // pega a informacao do atributo data:status  
		var url = botaoClicado.data('url'); // pega a informacao do  atributo data:url 
		
		var checkBoxSelecionados = this.selecaoCheckbox.filter(':checked'); // filter retorna um arrray com os ckeckbox selecionados "ticados"
		// cria map com todo os codigos dos usuarios no checkbox
		var codigos = $.map(checkBoxSelecionados, function(c){  // Retorna em "c" cada um dos checkbox selecionados  
			return $(c).data('codigo'); //  obtem o codigo do usuario contido no elemento checkbox no atributo data:codigo
		});
		
		if(codigos.length > 0 ){ // submet Ajax somente se tiver algum checkbox selecionado
			$.ajax({
				   url: url,
				method: 'PUT',  // para enviar como parametros "dados" com PUT deve ser configurado em AppInitializer o filtro httpPutFormContentFilter no metodo getServletFilters
				  data: {     
							codigos: codigos,
							status: status
					    },
			    success: function(){ // executa funcao apos execução do ajax com sucesso
			    		  window.location.reload();  // recarrega a pagina e mantendo o filtro
			            }
					    
			});
		}
	}
	
	// aplica uma acao no componente da tela, capturada no metodo construtor, 
	// quando ocorre uma acao do usuario ou sistema, pode ser passado event como parametro ou não
	function onSelecaoTodosClicados(){
		var status = this.selecaoTodosCheckBox.prop('checked'); // coluna selecionar todos chekbox esta selecionada
		this.selecaoCheckbox.prop('checked', status);  // seleciona todos os checkbox
		
		statusBotaoAcao.call(this, status);
		
	}
	
	function onSelecaoClicado(){
		var selecaoCheckboxChecados = this.selecaoCheckbox.filter(':checked'); // filtra os componentes e retorna os componentes selecionados com filter
	    
		// se quantidade de checkbox selecionados for maior igual que a quantidade de checkbox que existem, seleciona checkbox selecionar todos
		this.selecaoTodosCheckBox.prop('checked', selecaoCheckboxChecados.length >= this.selecaoCheckbox.length); 
		
		// desabilita os botoes Ativar/Desativar caso nenhum checkbox estiver selecionado
		statusBotaoAcao.call(this, selecaoCheckboxChecados.length);
	}
	
	
	function statusBotaoAcao(ativar){
		// remove a classe disabled dos botões caso algum checkbox esteja selecionado 
		ativar ? this.statusBtn.removeClass('disabled') : this.statusBtn.addClass('disabled');
	}
	
	return MultiSelecao;
		
		
}());


$(function(){
	
	var multiselecao = new Brewer.MultiSelecao();
	multiselecao.iniciar();
	
}());

