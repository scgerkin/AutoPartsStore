package com.javaiii.groupproject.AutoPartsStore.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Problem accessing database service.")
public class DatabaseException extends RuntimeException {
}
