package com.javaiii.groupproject.AutoPartsStore.command;

public class EmployeeCommand {
    Integer id;

    public EmployeeCommand() {
    }

    public EmployeeCommand(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
