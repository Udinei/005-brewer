package com.algaworks.brewer.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {

	public final String THUMBNAIL_PREFIX = "thumbnail.";
	public final String URL = "http://localhost:8080/fotos/";
	
	public String salvar(MultipartFile[] files);

	public byte[] recuperar(String foto);
	
	public byte[] recuperarThumbnail(String fotoCerveja) ;

	public void excluir(String foto);

	public String getUrl(String foto);
	
	/** Metodo da interface implementado no java 1.8 - utiliza googleID, para gerar um sequencia de caracter que serão usados
	    para renomear as fotos enviadas ao servidor, afim de evitar duplicação de nomes das fotos ao salvar */ 
	default String renomearArquivo(String nomeOriginal) {
		return UUID.randomUUID().toString() + "_" + nomeOriginal;
	}
}
