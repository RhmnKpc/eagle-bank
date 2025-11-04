package com.eaglebank.interfaces.rest.exception.response;


public record ValidationErrorDetails(
        String field,
        String message,
        String type
) {
}
