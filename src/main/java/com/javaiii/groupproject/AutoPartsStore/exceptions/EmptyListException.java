package com.javaiii.groupproject.AutoPartsStore.exceptions;

public class EmptyListException extends IllegalStateException {
    public EmptyListException(String listName) {
        super(listName);
    }
}
