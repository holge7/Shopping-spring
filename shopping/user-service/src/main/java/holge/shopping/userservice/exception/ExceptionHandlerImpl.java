package holge.shopping.userservice.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import commons.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerImpl extends ResponseEntityExceptionHandler {
	Logger log = LoggerFactory.getLogger(getClass());
	
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse> handleException(UserNotFoundException e){
		ApiResponse response = new ApiResponse(e.getMessage());

		return new ResponseEntity<>(
			response, 
			e.getHttpStatus()
		);

	}
	
	@ExceptionHandler(RolNotFoundException.class)
	public ResponseEntity<ApiResponse> handleException(RolNotFoundException e){
		ApiResponse response = new ApiResponse(e.getMessage());

		return new ResponseEntity<>(
			response, 
			e.getHttpStatus()
		);

	}
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ApiResponse> handleException(UserAlreadyExistsException e){
		ApiResponse response = new ApiResponse(e.getMessage());

		return new ResponseEntity<>(
			response, 
			e.getHttpStatus()
		);

	} //ConstraintViolationException
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse> handleException(ConstraintViolationException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getConstraintViolations().forEach(v -> {
			errors.put(v.getPropertyPath().toString(), v.getMessage());
		});
		
		ApiResponse response = new ApiResponse(true, "", errors);
		
		return new ResponseEntity<>(
					response,
					HttpStatus.BAD_REQUEST
				);		

	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		((BindException) ex).getBindingResult().getAllErrors()
			.forEach(error -> {
				String name = ((FieldError) error).getField();
				String msg = error.getDefaultMessage();
				errors.put(name, msg);
			});
		
		ApiResponse response = new ApiResponse(true, "", errors);
		
		return new ResponseEntity<>(
					response,
					HttpStatus.BAD_REQUEST
				);		
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleException(Exception e){
		ApiResponse response = new ApiResponse(e.getMessage());

		return new ResponseEntity<ApiResponse>(
			response, 
			HttpStatus.INTERNAL_SERVER_ERROR
		);

	}


}
