package br.com.factory.core.validator;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DataNascimentoValidator implements ConstraintValidator<DataNascimento, LocalDate> {

	@Override
	public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
		boolean valid = true;
		if (date != null) {
			valid = !date.isAfter(LocalDate.now());
		}

		return valid;
	}

}
