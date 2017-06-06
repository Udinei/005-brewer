var Brewer = Brewer || {};  // use o namespace Brewer ou  caso não exista crie 

Brewer.EstiloCadastroRapido = (function(){
	
	function EstiloCadastroRapido(){
		// declarando variaveis e atribuindo, a seus valores os componentes html declarado, ids, classes jscript etc...
		this.modal = $('#modalCadastroRapidoEstilo');
		this.botaoSalvar = this.modal.find('.js-modal-cadastro-estilo-salvar-btn');
		this.form = this.modal.find('form');
		this.url = this.form.attr('action');
		this.inputNomeEstilo = $('#nomeEstilo');
		this.containerMensagemErro = $('.js-mensagem-cadastro-rapido-estilo');
	}
	
	EstiloCadastroRapido.prototype.iniciar = function (){
		//executando, inicializando componentes declarados em classe no html etc 
		this.form.on('submit', function(event){ event.preventDefault() }); // cancela o submit do form ao precionar enter
		this.modal.on('shown.bs.modal', onModalShow.bind(this)); // evento lancado ao carregar o modal - .bind(this) sempre para usar a function no contexto 
		this.modal.on('hide.bs.modal', onModalClose.bind(this)); // envento lancado ao fechar ou cancelar o modal
		this.botaoSalvar.on('click', onBotaoSalvarClick.bind(this));
	}
	
	// foca no campo do modal
	function onModalShow(){
		this.inputNomeEstilo.focus();  // this. para utilizar a variavel do contexto
	}
	
	// ao fechar o modal remove classe de erro da div
	function onModalClose(){
		this.inputNomeEstilo.val(''); // limpa campo nome do modal
		this.containerMensagemErro.addClass('hidden');
		this.form.find('.form-group').removeClass('has-error');
	}
	
	// envia o formulario para o controller
	function onBotaoSalvarClick(){
	 var nomeEstilo = this.inputNomeEstilo.val().trim();
	    $.ajax({
		   url: this.url,
		   method:'POST',
		   contentType: 'application/json',
		   data: JSON.stringify({ nome: nomeEstilo }), //enviando um objeto em Json com JSON.stringify({}) 
		   error: onErroSalvandoEstilo.bind(this),
		   success: onEstiloSalvo.bind(this)
		   
	   	});
	}

	// exibir mensagem de erro
	function onErroSalvandoEstilo(obj){
		var mensagemErro = obj.responseText; // pegando msg de erro enviada pelo servidor
		this.containerMensagemErro.removeClass('hidden');
		this.containerMensagemErro.html('<span>' + mensagemErro + '</span>'); // enviando msg de erro para modal
		this.form.find('.form-group').addClass('has-error');  // inserindo classe de erro para destacar o texto e o campo nome
	}

 
	function onEstiloSalvo(estilo){
		var comboEstilo = $('#estilo');
		comboEstilo.append('<option value=' + estilo.codigo +'>' + estilo.nome + '</option>'); 	// adiciona na comboBox estilo o codigo e o nome do estilo
		comboEstilo.val(estilo.codigo); // seta o codigo do estilo salvo, como atual 
		this.modal.modal('hide'); // esconde o modal
		
	}
	
	return EstiloCadastroRapido;
	
}());


$(function(){

	var estiloCadastroRapido = new Brewer.EstiloCadastroRapido();
	estiloCadastroRapido.iniciar();
	
// Abaixo o mesmo codigo javaScript antes da modularização para uso de objeto javaScript (EstiloCadastroRapido)
// para servir de exmplo
//	form.on('submit', function(event){ event.preventDefault() });
//	var modal = $('#modalCadastroRapidoEstilo');
//	var botaoSalvar = modal.find('.js-modal-cadastro-estilo-salvar-btn');
//	var form = modal.find('form');
//	form.on('submit', function(event){ event.preventDefault() });
//	var url = form.attr('action');
//	var inputNomeEstilo = $('#nomeEstilo');
//	var containerMensagemErro = $('.js-mensagem-cadastro-rapido-estilo');
//	
//	modal.on('shown.bs.modal', onModalShow);
//	modal.on('hide.bs.modal', onModalClose)
//	botaoSalvar.on('click', onBotaoSalvarClick)
//	
//	function onModalShow(){
//		inputNomeEstilo.focus();
//	}
	
	
//	function onModalClose(){
//		inputNomeEstilo.val('');
//		containerMensagemErro.addClass('hidden');
//		form.find('.form-group').removeClass('has-error');
//	}
//	
//	function onBotaoSalvarClick(){
//		var nomeEstilo = inputNomeEstilo.val().trim();
//	   $.ajax({
//		   url: url,
//		   method:'POST',
//		   contentType: 'application/json',
//		   data: JSON.stringify({ nome: nomeEstilo }),
//		   error: onErroSalvandoEstilo,
//		   success: onEstiloSalvo
//		   
//	   });
//	}
//	
//	function onErroSalvandoEstilo(obj){
//		var mensagemErro = obj.responseText;
//		containerMensagemErro.removeClass('hidden');
//		containerMensagemErro.html('<span>' + mensagemErro + '</span>');
//		form.find('.form-group').addClass('has-error');
//	}
//	
//	function onEstiloSalvo(estilo){
//		var comboEstilo = $('#estilo');
//		comboEstilo.append('<option value=' + estilo.codigo +'>' + estilo.nome + '</option>');
//		comboEstilo.val(estilo.codigo);
//		modal.modal('hide');
//		
//	}
	
});

