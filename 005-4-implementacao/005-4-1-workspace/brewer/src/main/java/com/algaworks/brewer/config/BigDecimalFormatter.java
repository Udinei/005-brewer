package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;
/**
 * Essa classe faz a conversao de valores para o locale pt-BR, de um Bigdecimal ou de uma string evitando assim
 * o erro abaixo  quando do uso de  intercionalizacao
 * Failed to convert property value of type [java.lang.String] to required type [java.math.BigDecimal] for property valorFrete; 
 * nested exception is java.lang.NumberFormatException
 * 
 * @author Udinei
 *
 */
public class BigDecimalFormatter implements Formatter<BigDecimal> {

	private DecimalFormat decimalFormat;
	
	public BigDecimalFormatter(String pattern){
		NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
		decimalFormat = (DecimalFormat) format;
		decimalFormat.setParseBigDecimal(true);
		decimalFormat.applyPattern(pattern);
	}
	
	@Override
	public String print(BigDecimal object, Locale locale) {
		return decimalFormat.format(object);
	}

	@Override
	public BigDecimal parse(String text, Locale locale) throws ParseException {
		return (BigDecimal) decimalFormat.parse(text);
	}
	

}
