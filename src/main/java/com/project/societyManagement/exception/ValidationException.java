package com.project.societyManagement.exception;

import java.util.Map;

public class ValidationException extends RuntimeException {

  private Map<String,String> errors;

  public ValidationException(){
    super("Validation failed.");
  }

  public ValidationException(String message) {
    super(message);
  }

  public  ValidationException(Map<String,String>errors){
    super("Validation failed");
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  public void setErrors(Map<String, String> errors) {
    this.errors = errors;
  }
}
