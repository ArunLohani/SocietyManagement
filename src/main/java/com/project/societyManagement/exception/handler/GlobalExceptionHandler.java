package com.project.societyManagement.exception.handler;

import com.project.societyManagement.dto.Error.ErrorResponse;
import com.project.societyManagement.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse<Map<String,String>>> handleValidationNotValid(ValidationException ex){

        ErrorResponse errorResponse = new ErrorResponse(HttpStatusCode.valueOf(400),ex.getMessage(),ex.getErrors());

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse<Map<String,String>>> handleBadCredentials(BadCredentialsException ex){

        ErrorResponse errorResponse = new ErrorResponse(HttpStatusCode.valueOf(401),"Invalid Email or Password",ex.getLocalizedMessage());

        return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse<Map<String,String>>> handleIllegalArguement(IllegalArgumentException ex){

        ErrorResponse errorResponse = new ErrorResponse(HttpStatusCode.valueOf(400),"Illegal Arguments Provided",ex.getLocalizedMessage());

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);

    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<String>> handleAllOtherExceptions(Exception ex){

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.valueOf(500),ex.getMessage(),ex.getLocalizedMessage());

        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);

    }


}
