package com.project.societyManagement.dto.Error;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse<T> {
    private LocalDateTime timestamp;
    private HttpStatusCode statusCode;
    private String message;
    private T errors;

   public ErrorResponse(HttpStatusCode statusCode , String message , T errors){
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.message = message;
        this.errors = errors;
    }
}
