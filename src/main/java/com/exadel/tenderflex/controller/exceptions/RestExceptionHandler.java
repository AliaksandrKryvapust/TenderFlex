package com.exadel.tenderflex.controller.exceptions;

import com.exadel.tenderflex.controller.exceptions.dto.ExceptionDto;
import com.exadel.tenderflex.controller.exceptions.dto.MultipleExceptionDto;
import com.exadel.tenderflex.controller.exceptions.dto.SingleExceptionDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({DataIntegrityViolationException.class, NoSuchElementException.class,
            IllegalArgumentException.class, BadCredentialsException.class})
    public ResponseEntity<SingleExceptionDto> handleBadRequest(Exception ex) {
        this.makeLog(ex);
        SingleExceptionDto message = SingleExceptionDto.builder().logref("error")
                .message("The request contains incorrect data. Change the request and send it again").build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({OptimisticLockException.class})
    protected ResponseEntity<SingleExceptionDto> handleOptimisticLock(Exception ex) {
        this.makeLog(ex);
        SingleExceptionDto message = SingleExceptionDto.builder().logref("optimistic_lock")
                .message("Version does not match. Get new data and retry").build();
        return new ResponseEntity<>(message, HttpStatus.LOCKED);
    }

    @ExceptionHandler({Exception.class, IllegalStateException.class})
    public ResponseEntity<SingleExceptionDto> handleInternalServerError(Exception ex) {
        this.makeLog(ex);
        SingleExceptionDto message = SingleExceptionDto.builder().logref("error")
                .message("The server could not process the request correctly. Please contact the administrator").build();
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(@lombok.NonNull MethodArgumentNotValidException ex,
                                                                           @NonNull HttpHeaders headers,
                                                                           @NonNull HttpStatus status,
                                                                           @NonNull WebRequest request) {
        this.makeLog(ex);
        List<FieldError> allErrors = ex.getBindingResult().getFieldErrors();
        List<ExceptionDto> errors = new ArrayList<>();
        for (FieldError error : allErrors) {
            ExceptionDto newError = ExceptionDto.builder().field(error.getField())
                    .message(Objects.requireNonNull(error.getDefaultMessage())).build();
            errors.add(newError);
        }
        MultipleExceptionDto message = MultipleExceptionDto.builder().errors(errors).build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    private void makeLog(Exception ex) {
        log.error(ex.getMessage() + "\t" + ex.getCause() + "\n" + ex);
    }
}
