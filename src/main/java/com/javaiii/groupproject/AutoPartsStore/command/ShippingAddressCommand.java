package com.javaiii.groupproject.AutoPartsStore.command;

import com.javaiii.groupproject.AutoPartsStore.Models.address.ShippingAddress;

public class ShippingAddressCommand {
    boolean differentFromCustomer;
    ShippingAddress shippingAddress;

    public ShippingAddressCommand() {
    }

    public ShippingAddressCommand(boolean differentFromCustomer, ShippingAddress shippingAddress) {
        this.differentFromCustomer = differentFromCustomer;
        this.shippingAddress = shippingAddress;
    }

    public boolean isDifferentFromCustomer() {
        return differentFromCustomer;
    }

    public void setDifferentFromCustomer(boolean differentFromCustomer) {
        this.differentFromCustomer = differentFromCustomer;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
