package com.project.societyManagement.dto.Error;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse<T> {
    private HttpStatusCode statusCode;
    private String message;
    private T errors;


}
