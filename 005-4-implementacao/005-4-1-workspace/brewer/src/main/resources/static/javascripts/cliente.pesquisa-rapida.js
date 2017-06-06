Brewer = Brewer || {}

Brewer.PesquisaRapidaCliente = (function () {
	
	function PesquisaRapidaCliente(){
		this.pesquisaRapidaClientesModal = $('#pesquisaRapidaClientes');
		this.nomeInput = $('#nomeClienteModal');
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-clientes-btn');
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaclientes');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-cliente').html(); // pega pagina html que contem as tags do rendlebars
		this.template = Handlebars.compile(this.htmlTabelaPesquisa); // compila html com as tags do rendlebars
		this.mensagemErro = $('.js-mensagem-erro');
	}
	
	PesquisaRapidaCliente.prototype.iniciar = function () {
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
		this.pesquisaRapidaClientesModal.on('shown.bs.modal', onModalShow.bind(this));
	}
	
	function onModalShow(){
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClicado (event){
		
		    // desabilitar o evento padrao de submissao automatica do formulario via Ajax ao carregar a pagina  
			event.preventDefault();
			
			$.ajax({
				url: this.pesquisaRapidaClientesModal.find('form').attr('action'), // pegando url do formulario de pesquisa rapida
				method: 'GET',
				contentType: 'application/json',
				data: {
					nome: this.nomeInput.val() // "nome:" e o nome do parametro do metodo que recebera a informacao enviada  
				},
				
				success: onPesquisaConcluida.bind(this),
				error: onErroPesquisa.bind(this)
				
			});
	}
		
	function onErroPesquisa(){
		this.mensagemErro.removeClass('hidden');
				
	}
	
	
	function onPesquisaConcluida(resultado){
		this.mensagemErro.addClass('hidden');  // esconde div de mensagem de erros

		var html = this.template(resultado); // passa resultado "array de objetos" para a template do rendlebar fazer o parse retornar os dados e o html
		this.containerTabelaPesquisa.html(html); // retorna ao browser dentro da div containerTabelaPesquisaRapidaclientes o html gerado pelo rendlebar 
		
		
		// Cria o objeto da classe TabelaClientePesquisaRapida, e passa o formulario modal como parametro
		var tabelaClientePesquisaRapida = new Brewer.TabelaClientePesquisaRapida(this.pesquisaRapidaClientesModal);
		tabelaClientePesquisaRapida.iniciar();
	}
	
	return PesquisaRapidaCliente;
	
}());

// obtem o cliente selecionado, seu nome e codigo e seta os campos nomeCliente e codigoCliente (hidden) do formulario de vendas
Brewer.TabelaClientePesquisaRapida = (function() {

	function TabelaClientePesquisaRapida(modal){
		this.modalCliente = modal;
		this.cliente = $('.js-cliente-pesquisa-rapida');
		
	}
	
		
	TabelaClientePesquisaRapida.prototype.iniciar = function(){
		this.cliente.on('click', onClienteSelecionado.bind(this)); 	// ao selecionar um cliente retornado na tabela
	}
	

	function onClienteSelecionado(evento){
		this.modalCliente.modal('hide');  // remove o modal
		var clienteSelecionado = $(evento.currentTarget); // retorna exatamento cliente que foi clicado na tabela
		
		$('#nomeCliente').val(clienteSelecionado.data('nome')); // seta o nome no campo
		$('#codigoCliente').val(clienteSelecionado.data('codigo'));  // seta o campo "hidden" codigoCliente
	}
	
	return TabelaClientePesquisaRapida;
	
}());



$(function(){
	var pesquisaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesquisaRapidaCliente.iniciar();
	
});
