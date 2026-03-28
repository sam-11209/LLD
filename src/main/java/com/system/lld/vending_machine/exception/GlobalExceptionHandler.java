package com.system.lld.vending_machine.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(VMException.class)
	public ResponseEntity<MyException> handleVMExceptions(VMException ex) {
		MyException e1 = new MyException();
		e1.setMsg(ex.getMsg());
		e1.setStatusCode(ex.getStatusCode());
		return ResponseEntity.status(400) //
				.body(e1);
	}
}

class MyException {
	private String msg;
	private int statusCode;

	public void setStatusCode(int code) {
		this.statusCode = code;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
