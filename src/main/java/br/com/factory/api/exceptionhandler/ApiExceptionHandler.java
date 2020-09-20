package br.com.factory.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.factory.api.exceptionhandler.Problem.Campo;
import br.com.factory.domain.exception.EntidadeNaoEncontradaException;
import br.com.factory.domain.exception.NegocioException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<Object> handleEntidadeNaoEncontrada(NegocioException ex, WebRequest req) {

		HttpStatus status = HttpStatus.NOT_FOUND;
		return handleExceptionInternal(ex, getProblem(ex, status), new HttpHeaders(), status, req);

	}

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<Object> handleNegocio(NegocioException ex, WebRequest req) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return handleExceptionInternal(ex, getProblem(ex, status), new HttpHeaders(), status, req);

	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<Campo> campos = new ArrayList<Problem.Campo>();

		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			String campo = ((FieldError) error).getField();
			String mensagem = messageSource.getMessage(error, LocaleContextHolder.getLocale());

			campos.add(new Problem.Campo(campo, mensagem));
		}

		Problem problem = new Problem();

		problem.setStatus(status.value());
		problem.setTitulo("Um ou mais campos estão inválidos. " + "Faça o preenchimento correto e tente novamente");
		problem.setDataHora(OffsetDateTime.now());
		problem.setCampos(campos);

		return super.handleExceptionInternal(ex, problem, headers, status, request);
	}

	private Problem getProblem(NegocioException ex, HttpStatus httpStatus) {
		Problem problem = new Problem();

		problem.setStatus(httpStatus.value());
		problem.setTitulo(ex.getMessage());
		problem.setDataHora(OffsetDateTime.now());

		return problem;

	}

}
