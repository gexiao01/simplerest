package com.dp.simplerest.exception;

public class RestException extends Exception {

	/**
     *  
     */
	private static final long serialVersionUID = 7812415381586383619L;

	public RestException() {
		super();
	}

	public RestException(String msg) {
		super(msg);
	}

	public RestException(Throwable t) {
		super(t);
	}

	public RestException(String msg, Throwable t) {
		super(msg, t);
	}
}
