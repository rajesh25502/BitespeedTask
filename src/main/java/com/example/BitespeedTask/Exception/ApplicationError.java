package com.example.BitespeedTask.Exception;

import lombok.Data;

@Data
public class ApplicationError {
    private int code;
    private String message;
}
