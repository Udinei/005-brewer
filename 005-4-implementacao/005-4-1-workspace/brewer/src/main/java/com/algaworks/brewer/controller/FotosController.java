package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.storage.FotoStorageRunnable;

/** A vantagem ao anotar a classe com @RestController é que e a classe continua sendo tambem um @Controller
    e nao é necessario utilizar @ResponseBoy no retorno dos metodos para trabalhar com Ajax e Json,  
    ex: public @ResponseBoy String upload (){..} - não seria necessario o @ResponseBody, já esta lá mas nao de forma explita  */
@RestController   
@RequestMapping("/fotos")
public class FotosController {

	@Autowired
	private FotoStorage fotoStorage;
	
	
	/** Para o servidor receber requisição tipo MultpartFile (arquivos) de ser configurado 
	    na classe AppInitializer.java no metodo customizeRegistration 
	    DeferredResult<FotoDTO> aguarda até que o retorno esteja disponivel
	    utilizado para melhorar a disponibilidade da aplicacao via Thread assincrona, liberando 
	    o metodo para receber novas requisições enquanto processa outras requisoes anteriores 
	    */
	@PostMapping
	public DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile[] files){
		DeferredResult<FotoDTO> resultado = new DeferredResult<>();
		
		// cria thread para a classe FotoStorageRunnable que executa o processo de renomeacao salvamento da foto
		Thread thread = new Thread(new FotoStorageRunnable(files, resultado, fotoStorage));
		thread.start();
		
		return resultado;
		
	}
	
	
	/** o nome no parametro do metodo deve ser o mesmo enviado na url, por isso e utilizado @PathVariable
	    sem paramentro ex: recuperar (@PathVariable ("nome") String nomeX) dai o nome do paramentro do metodo poderia ser outro */
	@GetMapping("/{nome:.*}") // usando expressao regular"{nome:.*}" para obter arquvivo . qualquer extensao
	public byte[] recuperar(@PathVariable String nome){
		return fotoStorage.recuperar(nome);
	}
	
}
