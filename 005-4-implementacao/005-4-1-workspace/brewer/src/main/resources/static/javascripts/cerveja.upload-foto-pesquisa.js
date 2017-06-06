var Brewer = Brewer || {};

Brewer.UploadFotoPesquisa = (function(){
		
	function UploadFotoPesquisa(){   // funcao construtora inicializacao de variaveis
		this.urlThumbnailFoto = $("#idThumbnailFoto");
		//console.log('urlThumbnailFoto..', this.urlThumbnailFoto);
	}	
		
	UploadFotoPesquisa.prototype.iniciar = function (){  // funcao de execucao do comportamento
	
		//if(this.urlThumbnailFoto.get(0) !== undefined){
			console.log('urlThumbnailFoto..', this.urlThumbnailFoto);
		
			
			//this.urlNomeFoto = this.urlThumbnailFoto.get(0).currentSrc; // pega src corrente completo  da imagem
			this.urlNomeFoto = this.urlThumbnailFoto.val();
			console.log('urlnomefoto...', this.urlNomeFoto);
			this.url =  window.location.protocol + "//" + window.location.host;		// monta url ex: http://ukalix.myddns.me:8080
			trocaUrlFoto.call(this); // chama a funcao para rodar nesse contexto	
		//}
	}
	
	// monta nova Url e substitui no src da imagem
	function trocaUrlFoto(){
		//var pathName = this.urlNomeFoto.split('/').pop(); // extrai da url somente da imagem
		var pathName = this.urlNomeFoto;
		var newURL = this.url + "/fotos/" + pathName;  // monta a nova url
		this.urlThumbnailFoto.attr('src', newURL);
	}
	
	return UploadFotoPesquisa
		

})();	
	
$(function(){  // executa uma funcao "metodo" da classe Brewer
	var uploadFotoPesquisa = new Brewer.UploadFotoPesquisa();
	uploadFotoPesquisa.iniciar();
});