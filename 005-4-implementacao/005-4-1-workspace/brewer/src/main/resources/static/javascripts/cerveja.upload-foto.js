var Brewer = Brewer || {};

Brewer.UploadFoto = (function(){
		
	function UploadFoto(){   // funcao construtora inicializacao de variaveis
		this.inputNomeFoto = $('input[name=foto]');  // acessando elementos hidden do html
		this.inputContentType = $('input[name=contentType]');
		this.novaFoto = $('input[name=novaFoto]');
		this.inputUrlFoto = $('input[name=urlFoto]');
		

		this.htmlFotoCervejaTemplate = $('#foto-cerveja').html(); 		// obtendo html template com codigo do handlebars
		this.template = Handlebars.compile(this.htmlFotoCervejaTemplate); // compilando template handlebar
		
		this.containerFotoCerveja = $('.js-container-foto-cerveja');
		
		this.uploadDrop = $('#upload-drop');
		this.imgLoading = $('.js-img-loading');
		
	}
	
	
	// essa funcao sera executada quando voltar da validacao no servidor
	UploadFoto.prototype.iniciar = function (){  // funcao de execucao do comportamento
	
			var settings = {
				type: 'json',
				filelimit: 1,   // quantidade de arquivos que sera enviados de uma vez
				allow: '*.(jpg|jpeg|png)',   // tipos de arquivos que esta sendo enviado contentType
				action: this.containerFotoCerveja.data('url-fotos'),  // a url de action a ser enviada, vem do atributo data-url-fotos adicionada na div 
				complete: onUploadCompleto.bind(this), // o atributo complete, recebera o retorno que vira do servidor, .bind(this) da acesso a todas var. declarada na funcao contrutora
				beforeSend: adicionarCsrfToken,    // antes de executar o envio do ajax
				loadstart: onLoadStart.bind(this)
	        }
	
			UIkit.uploadSelect($('#upload-select'), settings);
			UIkit.uploadDrop(this.uploadDrop, settings);
			
			// Apos a validacao dos campos da tela, se usuario já selecionou uma foto, o campo urlFoto (hidden) foi preenchido 
			// entao carrega a foto novamente usando a url contida no campo urlFoto 
			if(this.inputNomeFoto.val()){
				renderizarFoto.call(this, {  // call e this, forca para que funcao seja executada no context da funcao
						nome: this.inputNomeFoto.val(),
						contentType: this.inputContentType.val(),
						url: this.inputUrlFoto.val()});  // obtem a url da foto, carreganda anteriormente
			}
			
	}
	 
	function onLoadStart(){
		this.imgLoading.removeClass('hidden');
		
	}
	
	
	// faz upload de uma nova foto
	function onUploadCompleto(resposta){  
		this.novaFoto.val('true');
		this.inputUrlFoto.val(resposta.url); // obtem a url da foto
		this.imgLoading.addClass('hidden');
		renderizarFoto.call(this,resposta);  
		
	}
	
	// carrega a foto para tela de cadastro de cerveja
	function renderizarFoto(resposta){
		this.inputNomeFoto.val(resposta.nome)
		this.inputContentType.val(resposta.contentType);
			
		this.uploadDrop.addClass('hidden');  // esconde a div upload-drop que contem o link de selecao da foto
		
		var htmlFotoCerveja = this.template({url: resposta.url}); // seta a url da foto na variavel {{url}} do handlebar, que esta no hmtl fotoCerveja   
		this.containerFotoCerveja.append(htmlFotoCerveja);        // insere o html fotoCerveja no lugar da div upload-drop que esta hidden, que fica
		                                                          // dentro da div com a classe .js-container-foto-cerveja 
				
		$('.js-remove-foto').on('click', onRemoverFoto.bind(this));
	}
	
	function onRemoverFoto(){
		$('.js-foto-cerveja').remove();
		this.uploadDrop.removeClass('hidden') // exibe novamente a div upload-drop que contem o link de selecao da foto
		this.inputNomeFoto.val('');  
		this.inputContentType.val('');
		this.novaFoto.val('false');
	}
	
	// adicionando o token do csrf na requisicao
	function adicionarCsrfToken(xhr){
		var token = $('input[name=_csrf]').val();  // pegando o input pelo atributo name
		var header = $('input[name=_csrf_header').val();
		xhr.setRequestHeader(header, token);
	}
	
	return UploadFoto;
	
})();


 $(function(){  // executa uma funcao "metodo" da classe Brewer
	var uploadFoto = new Brewer.UploadFoto();
	uploadFoto.iniciar();
 });
