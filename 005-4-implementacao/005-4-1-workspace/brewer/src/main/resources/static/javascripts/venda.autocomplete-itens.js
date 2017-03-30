Brewer = Brewer || {};


Brewer.Autocomplete = (function(){
	
	function Autocomplete(){
		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
		var htmTemplateAutocomplete = $('#template-autocomplete-cerveja').html(); // obtem html da div com tags handlebars
		this.template = Handlebars.compile(htmTemplateAutocomplete)  // compila tags handlebars, e retorna html
		this.emitter = $({});  // criando um emissor de eventos
		this.on = this.emitter.on.bind(this.emitter); // 
		
	}
	
	Autocomplete.prototype.iniciar = function (){
		var options = {
					    	url: function(skuOuNome) { // para url: enviar um parametro de consulta a mesma deve receber uma funcao
					    			return this.skuOuNomeInput.data('url') + '?skuOuNome=' + skuOuNome;
					 }.bind(this),
				     getValue: 'nome',    // abrituto que sera exibido no input do autocomplete
			  	     minCharNumber: 3,    // inicia a consulta apos 3 caracteres digitados
				     requestDelay: 300,   // somente paos 300 milsec. apos a digitacao faz a busca, evitar muitos request quando usuario digitam rapido demais
				     ajaxSettings: {
				                   contentType: 'application/json' // informando para o controller, que o esta sendo passado é Json 
				     },
				     template: {           // recebe o html customizado (com img) que sera apresentado no input
					             type: 'custom',              
					             method: template.bind(this)  // html retornado pelo handlebars ja com os dados das tags handlebar compilados
				         },
				     	 list: {    // se o evento "item-selecionado" for disparado em qualquer parte do sistema, chama funcao onItemSelecionado 
				        	   		onChooseEvent: onItemSelecionado.bind(this) 
				      		}		
		};
		
		 	this.skuOuNomeInput.easyAutocomplete(options);
	}
	
	
	function onItemSelecionado(){
		this.emitter.trigger('item-selecionado',this.skuOuNomeInput.getSelectedItemData());  // getSelectedItemData() metodo do easyautocomplete, retorna o objeto selecionado, para quem chamou o evento item-selecionado  
		this.skuOuNomeInput.val('');  // limpa a lista do autocomplete
		this.skuOuNomeInput.focus();   // foca no campo input do autocomplete
	}
	
	function template(nome, cerveja){
   	 	cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
   	 	return this.template(cerveja)
   	 
    }
	
	return Autocomplete
	
	
}());


