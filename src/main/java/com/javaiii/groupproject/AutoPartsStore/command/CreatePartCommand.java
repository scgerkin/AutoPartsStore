package com.javaiii.groupproject.AutoPartsStore.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePartCommand {
    public String name;
    public String category;
    public String description;
    public BigDecimal pricePerUnit;
    public Integer quantityOnHand;
}
