package com.algaworks.brewer.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name ="venda")
@DynamicUpdate //altera somente os atributos que foram modificados durante uma edição
public class Venda implements Serializable {

	private static final long serialVersionUID = 1L;
	

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long codigo; 
	
	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao; 
	
	@Column(name = "valor_frete")
	private BigDecimal valorFrete = BigDecimal.ZERO; 
	
	@Column(name = "valor_desconto")
	private BigDecimal valorDesconto  = BigDecimal.ZERO; 
	
	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO; 
	
	private String  observacao;
	
	@Column(name = "data_hora_entrega")
	private LocalDateTime dataHoraEntrega;
		
	
	@ManyToOne
	@JoinColumn(name = "codigo_cliente")
	private Cliente cliente;

	
	@ManyToOne
	@JoinColumn(name = "codigo_usuario")
	private Usuario usuario;


	@Enumerated(EnumType.STRING)
	private StatusVenda status = StatusVenda.ORCAMENTO; 
	
	/** orphanRemoval = true, para quando da edição ou exclusao de registros, 
	 O hibernate remover os itens orfão da lista e também do banco. caso contrario sera
	 duplicado e inserido uma nova lista com os mesmos registros no banco
	  CascadeType.ALL para que o JPA ao salvar uma venda salve tambem os itens da venda*/
	@OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemVenda> itens = new ArrayList<>();
	
	@Transient
	private String uuid;
		
	@Transient
	private LocalDate dataEntrega;
		
	@Transient
	private LocalTime horarioEntrega;

	@PostLoad // Apos carregar para edição seta campos transientes
	private void postLoad() {
		if (dataHoraEntrega != null) {
			this.dataEntrega = this.dataHoraEntrega.toLocalDate();
			this.horarioEntrega = this.dataHoraEntrega.toLocalTime();
			}
		}
	
	/** seta a venda no itemVenda */
	public void adicionarItens(List<ItemVenda> itens) {
		this.itens = itens;
		// para cada itemVenda seta o atributo codigo_venda - this é a venda que esta sendo criada
		this.itens.forEach(i -> i.setVenda(this));
		
	}
	
	public BigDecimal getValorTotalItens(){
		return   getItens().stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public void calcularValorTotal(){
		this.valorTotal = calcularValorTotal(getValorTotalItens(), getValorFrete(), getValorDesconto());
	}
	
	public Long getDiasCriacao(){
		LocalDate inicio = dataCriacao != null ? dataCriacao.toLocalDate() : LocalDate.now();
		
		// ChronoUnit - conta quantos dias tem entre as datas inicio e a data atual do sitema
		return ChronoUnit.DAYS.between(inicio, LocalDate.now());
	}
	
	public boolean isSalvarPermitido(){
		return !status.equals(StatusVenda.CANCELADA);
	}
	
	public boolean isSalvarProibido(){
		return !isSalvarPermitido();
	}
	
	
	private BigDecimal calcularValorTotal(BigDecimal valorTotalItens, BigDecimal valorFrete, BigDecimal valorDesconto) {
		BigDecimal valorTotal = valorTotalItens
				.add(Optional.ofNullable(valorFrete).orElse(BigDecimal.ZERO)
				.subtract(Optional.ofNullable(valorDesconto).orElse(BigDecimal.ZERO)));
		return valorTotal;
	}
	
	

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public BigDecimal getValorFrete() {
		return valorFrete == null ? BigDecimal.ZERO : this.valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto == null ? BigDecimal.ZERO : this.valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public LocalDateTime getDataHoraEntrega() {
		return dataHoraEntrega;
	}

	public void setDataHoraEntrega(LocalDateTime dataHoraEntrega) {
		this.dataHoraEntrega = dataHoraEntrega;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public StatusVenda getStatus() {
		return status;
	}

	public void setStatus(StatusVenda status) {
		this.status = status;
	}

	public List<ItemVenda> getItens() {
		return itens;
	}

	public void setItens(List<ItemVenda> itens) {
		this.itens = itens;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public LocalDate getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(LocalDate dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public LocalTime getHorarioEntrega() {
		return horarioEntrega;
	}

	public void setHorarioEntrega(LocalTime horarioEntrega) {
		this.horarioEntrega = horarioEntrega;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Venda other = (Venda) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	public boolean isNova() {
		return codigo == null;
	}

	
	
}
