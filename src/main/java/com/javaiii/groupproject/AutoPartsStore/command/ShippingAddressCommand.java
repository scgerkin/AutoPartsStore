package com.javaiii.groupproject.AutoPartsStore.command;

public class ShippingAddressCommand {
    boolean differentFromCustomer;
    String name;
    String street;
    String city;
    String state;
    String zip;

    public ShippingAddressCommand() {
    }

    public ShippingAddressCommand(boolean differentFromCustomer, String name, String street, String city, String state, String zip) {
        this.differentFromCustomer = differentFromCustomer;
        this.name = name;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public boolean isDifferentFromCustomer() {
        return differentFromCustomer;
    }

    public void setDifferentFromCustomer(boolean differentFromCustomer) {
        this.differentFromCustomer = differentFromCustomer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
