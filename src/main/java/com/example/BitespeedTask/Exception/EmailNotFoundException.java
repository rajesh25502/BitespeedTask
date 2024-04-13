package com.example.BitespeedTask.Exception;

public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException(String message)
    {
        super(message);
    }
}
