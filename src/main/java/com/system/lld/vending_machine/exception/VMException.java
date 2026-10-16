package com.system.lld.vending_machine.exception;

import lombok.Getter;

@Getter
public class VMException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String msg;
	private int statusCode;

	public VMException(int statusCode, String msg) {
		this.msg = msg;
		this.statusCode = statusCode;
	}

}
