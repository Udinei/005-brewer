var Brewer = Brewer || {};

Brewer.ComboEstado = (function() {
   	
	function ComboEstado(){
		this.combo = $('#estado');
		this.emitter = $({}); // cria um objeto jquery
		this.on = this.emitter.on.bind(this.emitter);  // abilita no objeto emitter o evento "on" do javascritp
		
	}
	
	ComboEstado.prototype.iniciar = function(){
		this.combo.on('change', onEstadoAlterado.bind(this)) // quando o componente for alterando "change" executa a funcao onEstadoAlterado
	}
	
	function onEstadoAlterado(){
		this.emitter.trigger('alterado', this.combo.val()); // criando trigger com o nome de vento 'alterado' que pode ser usado em outra funcao
		console.log('estado selecionado', this.combo.val());// para informar que o objeto foi alterado em outro contexto
	}
	
	return ComboEstado;
	
	
}());


Brewer.ComboCidade = (function(){
	
	function ComboCidade(comboEstado){
		this.comboEstado = comboEstado;
		this.combo = $('#cidade');
		this.imgLoading = $('.js-img-loading')
		this.inputHiddenCidadeSelecionada = $('#inputHiddenCidadeSelecionada');		
			
		
	}
	
	ComboCidade.prototype.iniciar = function(){
		reset.call(this);
		this.comboEstado.on('alterado', onEstadoAlterado.bind(this));
		var codigoEstado = this.comboEstado.combo.val();
		inicializarCidades.call(this, codigoEstado); // chama ajax para executar a consulta de cidade por codigo do estado
				
	}
	
	
	function onEstadoAlterado(evento, codigoEstado){
		this.inputHiddenCidadeSelecionada.val(''); // limpa variavel que informa qual cidade esta selecinada no momento
		 inicializarCidades.call(this, codigoEstado); // executa consulta ajax
	}

	
	// executa consulta ajax
	function inicializarCidades(codigoEstado){
		if (codigoEstado){ // executa ajax somente se o codigo do estado for passado "selecionado"
			var resposta = $.ajax({
			  	url: this.combo.data('url'),  // url definida na combo cidade em  data:url="@{/cidades}" 
			  	method: 'GET',
			  	contentType: 'application/json',
			  	data: { 'estado': codigoEstado},    // valor que sera passado na url codigoEstado na variavel estado ex: estado=2
				beforeSend: iniciarRequisicao.bind(this), // executa funcao a iniciarRequisicao, antes de enviar requisição 
				complete: finalizarRequisicao.bind(this)  // apos finalizar requisição retorna para complete o resultado
			});
			
			resposta.done(onBuscarCidadesFinalizado.bind(this)); // pega resposta do ajax e executa a funcao onBuscarCidadesFinalizado
			
		}else {
			reset.call(this); // recria itens da combo cidade 
		}		
	}
	
	// retorna dentro do parametro cidades a resposta do ajax
	// para gerar uma nova combo
	function onBuscarCidadesFinalizado(cidades){
		
		var options = [];
		// para cada cidade retornada no json monta um option html, e coloca no array options
		cidades.forEach(function(cidade){
			options.push('<option value="' + cidade.codigo + '">' + cidade.nome + '</option>');
			
		});
		

		this.combo.html(options.join()); 	// junta todos option da combo sem virgula
		this.combo.removeAttr('disabled');  // remove o atributo que desabilita a combo para seleçao
		
		var codigoCidadeSelecionada = this.inputHiddenCidadeSelecionada.val(); 
		
		if(codigoCidadeSelecionada){
			this.combo.val(codigoCidadeSelecionada);
		}
	}
	
    // reinicia a combo cidades
	function reset(){
		this.combo.html('<option value="">Selecione a cidade</option>'); // adiciona a combo um item, msg "Selecione a etc.." 
		this.combo.val(''); // coloca " " como primeiro item de seleção da combo
		this.combo.attr('disabled', 'disabled'); // desabilita a combo
	}
	
	// exibe icone de animação de carregamento da combo
	function iniciarRequisicao(){
		reset.call(this);
		this.imgLoading.show();
	}
	
	// esconde icone de animação do carregamento
	function finalizarRequisicao(){
		this.imgLoading.hide();
	}
	
	return ComboCidade;
	
}());



$(function(){
	var comboEstado = new Brewer.ComboEstado();
	comboEstado.iniciar();
	
	var comboCidade = new Brewer.ComboCidade(comboEstado);
	comboCidade.iniciar();
});
