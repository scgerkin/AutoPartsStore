package com.javaiii.groupproject.AutoPartsStore.command;

public class SingleStrCommand {
    String value;

    public SingleStrCommand() {

    }

    public SingleStrCommand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
