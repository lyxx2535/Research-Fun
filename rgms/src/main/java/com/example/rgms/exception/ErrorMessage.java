package com.example.rgms.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private String errorCode;
    private String errorMsg;
}
