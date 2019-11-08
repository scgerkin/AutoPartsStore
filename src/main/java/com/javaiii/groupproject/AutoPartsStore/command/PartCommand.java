package com.javaiii.groupproject.AutoPartsStore.command;

//todo this should probably be renamed
public class PartCommand {
    Integer id;
    Integer quantity;

    public PartCommand() {}

    public PartCommand(Integer id, Integer quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
