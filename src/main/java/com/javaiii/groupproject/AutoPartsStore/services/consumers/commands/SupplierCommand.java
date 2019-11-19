package com.javaiii.groupproject.AutoPartsStore.services.consumers.commands;

import lombok.Data;

@Data
public class SupplierCommand {
    private String companyName;
    private String contactName;
    private String primaryPhone;
    private String secondaryPhone;
    private String website;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String notes;
}
