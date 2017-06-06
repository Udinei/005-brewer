package com.algaworks.brewer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = "([a-zA-Z]{2}\\d{4})?") // [a-zA-Z] permite caracteres de a à z maiusculo e minusculo,{2} até dois caracter. \\d Numeros até  {4} quatro digitos numericos. "?" e só valida se o campo tiver preenchido
public @interface SKU {
	
	@OverridesAttribute(constraint = Pattern.class, name = "message")
	String message() default "{com.algaworks.constraints.SKU.message}";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
}
