package com.javaiii.groupproject.AutoPartsStore.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Part supplier does not match given ID.")
public class SupplierPartMismatchException extends RuntimeException {
}
