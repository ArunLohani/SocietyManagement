package com.project.societyManagement.util;

import com.project.societyManagement.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ValidationUtil {

    @Autowired
    private  Validator validator;

    public <T> void validate(T entity){
        log.info("Validating {}", entity.getClass().getName());
        Set<ConstraintViolation<T>> violations = validator.validate(entity);

        if(!violations.isEmpty()){
           log.error("Validation Failed...");
            Map<String,String> errors = new HashMap<>();
            for (ConstraintViolation violation : violations){
                log.error("Validation Errors - {} : {} ",violation.getPropertyPath().toString(),violation.getMessage());
                errors.put(violation.getPropertyPath().toString(),violation.getMessage());
            }
            throw new ValidationException(errors);
        }
        log.info("Validation Passed.");

    }


}
