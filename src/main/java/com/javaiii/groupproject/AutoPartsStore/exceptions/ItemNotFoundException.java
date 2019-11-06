package com.javaiii.groupproject.AutoPartsStore.exceptions;

import java.sql.SQLException;

public class ItemNotFoundException extends SQLException {
    public ItemNotFoundException(String exceptionMsg) {
        super(exceptionMsg);
    }
}
