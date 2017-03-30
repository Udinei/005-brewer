package com.algaworks.brewer.repository.helper.cerveja;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.dto.ValorItensEstoque;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;
import com.algaworks.brewer.storage.FotoStorage;

/** O spring utiliza como padrao a convensao Impl para encontrar as implemtacao especificas das interface de JpaRepositories*/
public class CervejasImpl implements CervejasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil; 
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true) // somente uma transação de leitura
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cerveja.class);
		
		// remover essa linha para pesquisa funcionar sem paginação
		paginacaoUtil.preparar(criteria, pageable);
	
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	
	private Long total(CervejaFilter filtro){
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cerveja.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(CervejaFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getSku())) {
				criteria.add(Restrictions.eq("sku", filtro.getSku()));
			}
			
			// retorne qualquer nome que tenha a ocorrencia de texto digitada 
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}

			if (isEstiloPresente(filtro)) {
				criteria.add(Restrictions.eq("estilo", filtro.getEstilo()));
			}

			if (filtro.getSabor() != null) {
				criteria.add(Restrictions.eq("sabor", filtro.getSabor()));
			}

			if (filtro.getOrigem() != null) {
				criteria.add(Restrictions.eq("origem", filtro.getOrigem()));
			}

			// ge - maior e igual
			if (filtro.getValorDe() != null) {
				criteria.add(Restrictions.ge("valor", filtro.getValorDe()));
			}

			//le - menor e igual
			if (filtro.getValorAte() != null) {
				criteria.add(Restrictions.le("valor", filtro.getValorAte()));
			}
		}
	}
	

	
	private boolean isEstiloPresente(CervejaFilter filtro) {
		return filtro.getEstilo() != null && filtro.getEstilo().getCodigo() != null;
	}


	/** Esse metodo é utilizado na pesquisa de cervejas na tela de vendas */
	@Override
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		String queryJpql = "select new com.algaworks.brewer.dto.CervejaDTO(codigo, sku, nome, origem, valor, foto)"
		      		+ "from Cerveja where lower(sku) like lower(:skuOuNome) or lower(nome) like lower(:skuOuNome)";
		
		List<CervejaDTO> cervejasFiltradas = manager.createQuery(queryJpql, CervejaDTO.class)
				.setParameter("skuOuNome", "%" + skuOuNome + "%")
				.getResultList();
		
		// para cada cerveja retornada seta a url do thumbnail da cerveja
		cervejasFiltradas.forEach(c -> c.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + c.getFoto())));
		
		return cervejasFiltradas;
	}


	@Override
	public ValorItensEstoque valorItensEstoque() {
		String queryJpql = "select new com.algaworks.brewer.dto.ValorItensEstoque(sum(valor * quantidadeEstoque), sum(quantidadeEstoque)) from Cerveja";
		return manager.createQuery(queryJpql, ValorItensEstoque.class).getSingleResult();
	}

	
}

