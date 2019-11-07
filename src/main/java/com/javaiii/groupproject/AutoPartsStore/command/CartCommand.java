package com.javaiii.groupproject.AutoPartsStore.command;

public class CartCommand {
    String notes;
    Integer employeeID;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        this.employeeID = employeeID;
    }
}
