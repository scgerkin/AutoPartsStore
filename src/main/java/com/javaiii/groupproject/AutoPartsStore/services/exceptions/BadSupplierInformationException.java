package com.javaiii.groupproject.AutoPartsStore.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,
    reason = "Supplier information given is invalid or incomplete. Verify given" +
                 " ID is NULL or -1 and all required information is present.")
public class BadSupplierInformationException extends RuntimeException {
}
