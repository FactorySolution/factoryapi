package br.com.factory.domain.exception;

public class EntidadeNaoEncontradaException extends NegocioException {

	private static final long serialVersionUID = -4998919187584412453L;

	public EntidadeNaoEncontradaException(String message) {
		super(message);
	}

}
