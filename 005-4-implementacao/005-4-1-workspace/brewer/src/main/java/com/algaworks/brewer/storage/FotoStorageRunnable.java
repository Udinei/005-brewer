package com.algaworks.brewer.storage;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;


/**
   Classe na Thread que encaminha o arquivo para ser renomeado e salvo, e necessario implementar Runnable */
public class FotoStorageRunnable implements Runnable {
	
	private MultipartFile[] files;
	private DeferredResult<FotoDTO> resultado;
	private FotoStorage fotoStorage;
	
	public FotoStorageRunnable(MultipartFile[] files, DeferredResult<FotoDTO> resultado, FotoStorage fotoStorage) {
		this.files = files;
		this.resultado = resultado;
		this.fotoStorage = fotoStorage;
			
	}
	
	@Override
	public void run() {
			
		String nomeFoto = this.fotoStorage.salvar(files);
		String contentType = files[0].getContentType();
		// retorna o novo nome do arquivo, sua localizacao e tipo para a Thread resultado
		resultado.setResult(new FotoDTO(nomeFoto, contentType, fotoStorage.getUrl(nomeFoto)));
		
	}

}
