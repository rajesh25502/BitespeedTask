package com.example.BitespeedTask.Controller;

import com.example.BitespeedTask.Exception.ApplicationError;
import com.example.BitespeedTask.Exception.EmailNotFoundException;
import com.example.BitespeedTask.Exception.PhoneNumberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@RestController
public class ErrorHandler {

    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<ApplicationError> handlePhoneNumberNotFoundException(
            PhoneNumberNotFoundException exception, WebRequest webRequest)
    {
        ApplicationError error=new ApplicationError();
        error.setCode(101);
        error.setMessage(exception.getMessage());

        return  new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
