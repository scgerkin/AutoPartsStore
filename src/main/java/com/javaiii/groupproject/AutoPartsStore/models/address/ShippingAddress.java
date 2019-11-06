package com.javaiii.groupproject.AutoPartsStore.models.address;


import com.javaiii.groupproject.AutoPartsStore.models.people.Customer;

import java.util.Objects;

/**
 * ShippingAddress allows the creation of an address specifically for shipping
 * items. It can be created from a Customer and use the customer information for
 * creating the information or with a different shipping name and address or with
 * the customer name and a new shipping address.
 */
public class ShippingAddress extends Address {
    private String shipName;

    /**
     * Constructor for creating a ShippingAddress where we ship directly to the
     * Customer information on file.
     * @param customer The customer we want to ship to.
     */
    public ShippingAddress(Customer customer) {
        this(customer, customer.getAddress());
    }

    /**
     * Constructor for creating a ShippingAddress where the ship name is not the
     * Customer name or and the address is not the Customer address.
     * @param shipName The name that should appear on the shipping label.
     * @param shippingAddress The address we are shipping to.
     */
    public ShippingAddress(String shipName, Address shippingAddress) {
        super(shippingAddress);
        this.shipName = shipName;
    }

    /**
     * Constructor for creating a ShippingAddress where the Customer name is used
     * but is sent to a different address than what is on file.
     * @param customer The customer we are shipping to.
     * @param shippingAddress The different address we want to ship to.
     */
    public ShippingAddress(Customer customer, Address shippingAddress) {
        this((customer.getFirstName() + " " + customer.getLastName()), shippingAddress);
    }

    /**Getter for shipName*/
    public String getShipName() {
        return shipName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ShippingAddress that = (ShippingAddress) o;
        return Objects.equals(shipName, that.shipName);
    }
}
