package com.javaiii.groupproject.AutoPartsStore.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Quantity requested is not valid.")
public class InvalidQuantityException extends RuntimeException {
}
