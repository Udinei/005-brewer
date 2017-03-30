package com.algaworks.brewer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.service.CadastroEstiloService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeEstiloJaCadastradoException;

@Controller
@RequestMapping("/estilos")  // mapeamento do controller para evitar ter que colocar em cada metodo "/estilos"
public class EstilosController {
	
	@Autowired
	CadastroEstiloService cadastroEstiloService;
	
	
	@Autowired
	private Estilos estilos;
	
	
	@RequestMapping("/novo")
	public ModelAndView novo(Estilo estilo){
		return new ModelAndView("estilo/CadastroEstilo");
	}
	

	@PostMapping({ "/novo", "{\\+d}" }) 
	public ModelAndView salvar(@Valid Estilo estilo, BindingResult result,  RedirectAttributes attributes){
		// validacao do bean validation
		if(result.hasErrors()){
			return novo(estilo);
		}
		
		try{
			cadastroEstiloService.salvar(estilo);	
			
		} catch (NomeEstiloJaCadastradoException e){ //captura a exception
            // atributo que deu problema na regra de negocio
			result.rejectValue("nome", e.getMessage(), e.getMessage()); // injeta mensagem na view do objeto
			return novo(estilo);  // retorna objeto na view e exibe mensagem de dados duplicados
		}
		
	        
		attributes.addFlashAttribute("mensagem", "Estilo salvo com sucesso!");
		return new ModelAndView("redirect:/estilos/novo");
	}
	
	/** @RequestBody - Necessario para receber um objeto via json e converter em objeto jav, usa a API do Jackson
	    @ResponseEntity<?> - Ajuda a definir status de erro para quem chamou o metodo erros: 400 , 200, 500 etc */
	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Estilo estilo, BindingResult result){
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());	// envia msg de erros para a view
		}
		
		// caso o estilo ja exista cadastrado o exception sera tratado pela classe ControllerAdviceExceptionHandler
		estilo = cadastroEstiloService.salvar(estilo);

		return ResponseEntity.ok(estilo);
		
	}
	
	@GetMapping
	public ModelAndView pesquisar(EstiloFilter estiloFilter, BindingResult result, @PageableDefault(size = 2) Pageable pageable, HttpServletRequest httpServletRequest	){
		ModelAndView mv = new ModelAndView("estilo/PesquisaEstilos");
		 
		PageWrapper<Estilo> paginaWrapper = new PageWrapper<>(estilos.filtrar(estiloFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
		
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo){
		Estilo estilo = estilos.findOne(codigo);
		
		ModelAndView mv = novo(estilo);
		mv.addObject(estilo);
		return mv;
		
	}
	
	// Esse metodo demonstra a forma de integração do sprig-data com o spring-jpa no acesso a dados
	// esta sendo  usado na interface da classe "Estilos" o jpaRepository.
	// Para converter o "codigo" passado no PathVariable para o objeto Estilo,
	// para tanto deve primeiro ser configurado "criar" no Webconfig o Bean DomainClassConverter
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Estilo estilo){

		try {
			cadastroEstiloService.excluir(estilo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok().build(); //retorna 200 ao cliente browser - de tudo ocorreu como esperado 
	}
	
}
