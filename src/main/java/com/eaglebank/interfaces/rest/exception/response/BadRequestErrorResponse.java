package com.eaglebank.interfaces.rest.exception.response;

import java.util.List;

public record BadRequestErrorResponse(String message,
                                      List<ValidationErrorDetails> details
) {
}
