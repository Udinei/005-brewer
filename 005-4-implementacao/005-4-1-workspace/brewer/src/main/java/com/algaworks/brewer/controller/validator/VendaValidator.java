package com.algaworks.brewer.controller.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.algaworks.brewer.model.Venda;

@Component
public class VendaValidator implements Validator {

	/** Declara suporte de validação para a classe Venda */
	@Override
	public boolean supports(Class<?> clazz) {
		return Venda.class.isAssignableFrom(clazz);
	}

	
	/** target vira como objeto da classe que esta sendo validada */
	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors,"cliente.codigo", "", "Selecione um cliente na pesquisa rápida");
		
		// convertendo target para Venda
		Venda venda = (Venda) target;
		validarSeInformouApenasHorarioEntrega(errors, venda);
		validarSeInformouItens(errors, venda);
		
		if(venda.getValorTotal().compareTo(BigDecimal.ZERO) < 0){
			errors.reject("", "Valor total não pode ser negativo");
		}
 	}

	private void validarSeInformouItens(Errors errors, Venda venda) {
		if(venda.getItens().isEmpty()){
			errors.reject("", "Adicione pelo menos uma cerveja na venda");
		}
	}

	private void validarSeInformouApenasHorarioEntrega(Errors errors, Venda venda) {
		if(venda.getHorarioEntrega() != null && venda.getDataEntrega() == null){
			errors.rejectValue("dataEntrega", "", "Informe uma data de entrega para um horário");
		}
	}
	
}
