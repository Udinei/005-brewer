package com.algaworks.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class VendasImpl implements VendasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
				
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	
	}

	@Transactional(readOnly = true)
	@Override
	public Venda buscarComItens(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN); // Fazendo relacionamento com a tabela grupos
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); // Agrupa por usuario e remove
		return (Venda) criteria.uniqueResult();
		
	}
	

	private Long total(VendaFilter filtro){
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	@Override
	public BigDecimal valorTotalNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
				.setParameter("ano", Year.now().getValue())
				.setParameter("status", StatusVenda.EMITIDA)
				.getSingleResult());
		
		return optional.orElse(BigDecimal.ZERO);
	}

	@Override
	public BigDecimal valorTotalNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and month(dataCriacao) = :mes and status = :status", BigDecimal.class)
				.setParameter("ano", Year.now().getValue())
				.setParameter("mes", MonthDay.now().getMonthValue())
				.setParameter("status", StatusVenda.EMITIDA)
				.getSingleResult());
		
		return optional.orElse(BigDecimal.ZERO);
		
	}

	@Override
	public BigDecimal valorTicketMedioNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano  and status = :status", BigDecimal.class)
				.setParameter("ano", Year.now().getValue())
				.setParameter("status", StatusVenda.EMITIDA)
				.getSingleResult());
		
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMes> totalPorMes() {
		 List<VendaMes> vendasMes = manager.createNamedQuery("Vendas.totalPorMes").getResultList();
		 
		 // Meses que não tiveram vendas serão inseridos no grafico com valor igual a zero
		 InsereMesSemVendasComValorIgualZero(vendasMes);
		 
		 return vendasMes; 
	}

	
	private void InsereMesSemVendasComValorIgualZero(List<VendaMes> vendasMes) {
		LocalDate hoje = LocalDate.now();
		 
		 // verifica se tem algum mes faltante, caso tiver inserir o mes com valor 0
		 for(int i = 1; i <= 6; i++){
			 // iniciar com o ano e mes atual,  ex: 2017/02, que sera retrocedido ate 6 meses
			 String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());
			 
			 // na lista de vendas existe o Mes existe mesId
			 boolean possuiMes = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			 
			 // se o mes nao foi encontrado na lista de venda do mes 
			 if(!possuiMes){
				 // adiciona o mes faltante com venda = 0
				 vendasMes.add(i -1, new VendaMes(mesIdeal, 0));
			 }
			 
			 // retrocede um mes, até 6 meses
			 hoje = hoje.minusMonths(1);
		 }
		 
		if(vendasMes.size() > 6){
			vendasMes.remove(vendasMes.size()-1);
		}
			
	}
	
	@Override
	public List<VendaOrigem> totalPorOrigem() {
		List<VendaOrigem> vendasNacionalidade = manager.createNamedQuery("Vendas.porOrigem", VendaOrigem.class).getResultList();
		
		// insere na lista mes sem vendas com valor igual a zero
		LocalDate now = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			String mesIdeal = String.format("%d/%02d", now.getYear(), now.getMonth().getValue());
			
			boolean possuiMes = vendasNacionalidade.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasNacionalidade.add(i - 1, new VendaOrigem(mesIdeal, 0, 0));
			}
			
			now = now.minusMonths(1);
		}
		
		if(vendasNacionalidade.size() > 6){
			vendasNacionalidade.remove(vendasNacionalidade.size()-1);
		}
			
		
		return vendasNacionalidade;
	}

	
	private void adicionarFiltro(VendaFilter filtro, Criteria criteria) {
		criteria.createAlias("cliente","c");
		
		if(filtro != null){
				
			if (!StringUtils.isEmpty(filtro.getCodigo())) {
				criteria.add(Restrictions.eq("codigo", filtro.getCodigo()));
			}
			
			if (filtro.getStatus() != null) {
				criteria.add(Restrictions.eq("status", filtro.getStatus()));
			}

			// ge - maior e igual
			if (filtro.getDesde() != null) {
				LocalDateTime desde = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0,0));
				criteria.add(Restrictions.ge("dataCriacao", desde));
			}
			
			//le - menor e igual
			if (filtro.getAte() != null) {
				LocalDateTime ate = LocalDateTime.of(filtro.getAte(), LocalTime.of(23, 59));
				criteria.add(Restrictions.le("dataCriacao", ate));
			}
		
		
			// ge - maior e igual
			if (filtro.getValorMinimo() != null) {
				criteria.add(Restrictions.ge("valorTotal", filtro.getValorMinimo()));
			}
			
			//le - menor e igual
			if (filtro.getValorMaximo() != null) {
				criteria.add(Restrictions.le("valorTotal", filtro.getValorMaximo()));
			}
									
			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				criteria.add(Restrictions.ilike("c.nome", filtro.getNomeCliente(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
				criteria.add(Restrictions.eq("c.cpfOuCnpj", TipoPessoa.removerFormatacao(filtro.getCpfOuCnpjCliente())));
			}
		}
		
	}

	
	

	
}
