package com.system.lld.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> annotationFailedException(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(e -> {
			String msg = e.getDefaultMessage();
			String field = ((FieldError) e).getField();
			errors.put(field, msg);
		});
		return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);

	}

//	@ExceptionHandler(ConstraintViolationException.class)
//	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
	//// List<FieldError> errors =
	/// ex.getConstraintViolations().stream().map(constraintViolation -> { / return
	/// new FieldError( / constraintViolation.getRootBeanClass().getName() + " " +
	/// constraintViolation.getPropertyPath(), / constraintViolation.getMessage(),
	/// null); / }).collect(Collectors.toList());
//
//		List<String> errors = ex.getConstraintViolations().stream().map(c -> c.getMessage())
//				.collect(Collectors.toList());
//		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//
//	}

}
