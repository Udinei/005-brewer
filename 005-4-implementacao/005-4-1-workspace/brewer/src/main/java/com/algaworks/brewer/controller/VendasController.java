package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.session.TabelasItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private CadastroVendaService cadastroVendaService; 
	
	@Autowired
	private TabelasItensSession tabelaItens;
	
	// componente de validacao para a venda
	@Autowired
	private VendaValidator vendaValidator;
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Mailer mailer;
	
	/** Adiciona validade ao controller.
	 *  Ao encontrar um @Valid em qualquer metodo dessa classe, usa esse validador para validar os atributos
	 *  o parametro passado ao @InitBinder "venda" informa que esse validador é somente para classe Venda, caso
	 *  esse parametro nao seja passado o Spring tentara Validar a classe VendaFilter também provocando um erro */
	@InitBinder("venda")
	public void inicializarValidador(WebDataBinder binder){
	   binder.setValidator(vendaValidator);	
	}
	
	
	
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda){
		 ModelAndView  mv = new  ModelAndView("venda/CadastroVenda");
		 
		 
		 // cria uuid de controle da sessão de visao do usuario
		 setUuid(venda);
		 
		 mv.addObject("itens", venda.getItens());
		 mv.addObject("valorFrete", venda.getValorFrete());
		 mv.addObject("valorDesconto", venda.getValorDesconto());
		 mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
				 
		return mv;
	}


	/** Executa esse metodo se tiver na url o paramento salvar */
	@PostMapping(value ="/nova", params="salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		
		// adiciona primeiro os itens na venda para depois validar a venda
		validarVenda(venda);
		
		// A anotation @Valid foi removido dos parametros do metodo, para facilitar a validação dos itens da venda
		// passando diretamente o resulta para ser validado na classe VendaValidator
		vendaValidator.validate(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva com sucesso" );
		return new ModelAndView("redirect:/vendas/nova");
	}


	/** Executa esse metodo se tiver na url o paramento emitir */
	@PostMapping(value = "/nova", params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		validarVenda(venda);
        
		// aplicando o validador "@Valid" apos inserir os itens 
		vendaValidator.validate(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva e emitida com sucesso" );
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	/** Executa esse metodo se tiver na url o paramento enviarEmail */
	@PostMapping(value="/nova", params="enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes
			, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		validarVenda(venda);
        
		// aplicando o validador "@Valid" apos inserir os itens 
		vendaValidator.validate(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		venda = cadastroVendaService.salvar(venda);
		mailer.enviar(venda); // envia email de forma assincrona
				
		attributes.addFlashAttribute("mensagem", String.format("Venda nº %d salva com sucesso e e-mail enviado", venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid){
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
		
		return mvTabelaItensVenda(uuid);
	}
	

	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoCerveja") Cerveja cerveja, @RequestParam Integer quantidade, @RequestParam String uuid){
		System.out.println(">>>>> UUid "+ uuid +"  Quantidade "+ quantidade + " cerveja " + cerveja);
		tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);
		
		return mvTabelaItensVenda(uuid);
	}

	
	/**  Esse metodo demonstra a forma de integração do sprig-data com o spring-jpa no acesso a dados
	  *  fazendo uma pesquisa findOne direta, isso é possivel porque esta sendo usado na interface da classe "Cerveja" 
	  *  o jpaRepository que converte a string "codigoCerveja" passada como parametro junto com @PathVariable para o
	  *  objeto Cerveja. Nota: Primeiro deve ser configurado "criar" no Webconfig o Bean DomainClassConverter */
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable String uuid){
		tabelaItens.excluirItem(uuid, cerveja);
		
		return mvTabelaItensVenda(uuid);
		
	}
	
	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}

	private void validarVenda(Venda venda) {
			// recupera os itens da venda da tabela de intens, e seta a venda nos itens, ao salvar a venda 
			venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
			
			// calcula o valor total da venda
			venda.calcularValorTotal();
					
	}
	
		
	@GetMapping
	private ModelAndView pesquisar(VendaFilter vendaFilter, BindingResult result, @PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("venda/PesquisaVendas");
		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());

		
	    PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable)
	    		, httpServletRequest);	
		
	    mv.addObject("pagina", paginaWrapper);
	    
		return mv;
	}	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo){
		// recupera a venda com os itens
		Venda venda = vendas.buscarComItens(codigo);
		
		// gera um uuid de sessao do usuario, para a venda recuperada caso nao exista
		setUuid(venda);
		
		// para cada item da venda, adiciona na Tabelas Itens da Session, o uuid, o item e a quantidade
		for(ItemVenda item : venda.getItens()){
			tabelaItens.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
			
		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		return mv;
		
	}


	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result
			,RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		
		try {
			cadastroVendaService.cancelar(venda);
			
		} catch (AccessDeniedException e) {
			return new ModelAndView("/403"); //poderia enviar para outro pagina 
		}
		
		//cadastroVendaService.cancelar(venda);
		attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso");
		
		return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
				
	}
	
	/** @ResponseBody Habilita o retorno, de uma lista de objetos no formato Json*/
	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalPorMes(){
		return vendas.totalPorMes();
	}
	
	@GetMapping("/porOrigem")
	public @ResponseBody List<VendaOrigem> vendasPorNacionalidade() {
		return this.vendas.totalPorOrigem();
	}
	
	/**Gera um uuid de controle de sessão de visao do usuario, ou seja para cada nova aba aberta o usuario tera
	   um identificador de sessao */
	private void setUuid(Venda venda) {
		// se um uuid de sessao do usuario não foi criado quando da criacao ou edicao de uma venda   
		if(StringUtils.isEmpty(venda.getUuid())){
			 // entao gera aleatoreamente um uuid usando API UUID do javaUtil e seta na venda  
			 venda.setUuid(UUID.randomUUID().toString()); 
		
		 }
	}
			
}