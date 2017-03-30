// classe utilizada para enviar via parametro, o nome do metodo a ser executado no controler  

Brewer = Brewer || {};

Brewer.BotaoSubmit = (function(){
  
	function BotaoSubmit(){
		this.submitBtn = $('.js-submit-btn') // obtem o nome da classe nos botoes que sera clicado
		this.formulario = $('.js-formulario-principal') //obtem o nome do <form> principal
	}
		
	BotaoSubmit.prototype.iniciar = function(){
		this.submitBtn.on('click', onSubmit.bind(this)); // ao clicar no botao executa a funcao onSubmit
	}
	
	function onSubmit(evento){
			evento.preventDefault(); // desativa a execução padrão do componente clicado "submit"
			
			var botaoClicado = $(evento.target); //retorna o componente clicado "botao"
			var acao = botaoClicado.data('acao'); // obtem o nome da acao contida no atributo data:acao do botao pode ser "emitir, salvar, enviarEmail"
			var acaoInput = $('<input>'); //cria uma tag <input>
			acaoInput.attr('name',acao); // insere na tag <input> criada acima, o atributo nome da acao no botao pode ser "emitir, salvar, enviarEmail"
		
			this.formulario.append(acaoInput); // insere a tag no formulario, que sera enviada como parametro na url
			this.formulario.submit(); // submet o formulario ao servidor
		}
	
		return BotaoSubmit;
	
}());

$(function(){
	var botaoSubmit = new Brewer.BotaoSubmit();
	botaoSubmit.iniciar();
		
});