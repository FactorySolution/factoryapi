package br.com.factory.domain.exception;

public class NegocioException extends RuntimeException {

	private static final long serialVersionUID = 6137186584838071893L;

	public NegocioException(String message) {
		super(message);
	}
}
