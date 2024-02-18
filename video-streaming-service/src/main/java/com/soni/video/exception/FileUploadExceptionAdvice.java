package com.soni.video.exception;

import com.soni.video.dto.ApiResponse;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(DataBufferLimitException.class)
    public ResponseEntity<ApiResponse> handleMaxSizeException(DataBufferLimitException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ApiResponse("File is too large!"));
    }
}
