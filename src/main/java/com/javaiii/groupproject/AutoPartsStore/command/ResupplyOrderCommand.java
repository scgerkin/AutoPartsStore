package com.javaiii.groupproject.AutoPartsStore.command;

import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ResupplyOrderCommand {
    int employeeID;
    BigDecimal shippingFee;
    BigDecimal taxAmount;
    List<Part> parts;
    String notes;
}
