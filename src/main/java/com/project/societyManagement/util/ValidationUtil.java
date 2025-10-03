package com.project.societyManagement.util;


import com.project.societyManagement.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ValidationUtil {

    @Autowired
    private  Validator validator;

    public <T> void validate(T entity){

        Set<ConstraintViolation<T>> violations = validator.validate(entity);

        if(!violations.isEmpty()){
            System.out.println("VALIDATIONS "+violations);
            Map<String,String> errors = new HashMap<>();
            for (ConstraintViolation violation : violations){

                errors.put(violation.getPropertyPath().toString(),violation.getMessage());
            }
            throw new ValidationException(errors);
        }

    }


}
