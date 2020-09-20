package br.com.factory.core.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DataNascimentoValidator.class})
public @interface DataNascimento {

	
	String message() default "data de nascimento inválida";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
	

}
