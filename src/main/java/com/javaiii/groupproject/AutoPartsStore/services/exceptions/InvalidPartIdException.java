package com.javaiii.groupproject.AutoPartsStore.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Part ID is invalid.")
public class InvalidPartIdException extends RuntimeException {
}
