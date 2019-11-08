package com.javaiii.groupproject.AutoPartsStore.command;

import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;

public class EditSupplierCommand {
    private Integer businessId;
    private String companyName;
    private String contactPerson;
    private String primaryPhone;
    private String secondaryPhone;
    private String website;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String notes;

    public EditSupplierCommand() {
    }

    public EditSupplierCommand(Supplier supplier) {
        this.businessId = supplier.getBusinessID();
        this.companyName = supplier.getCompanyName();
        this.contactPerson = supplier.getContactName();
        this.primaryPhone = supplier.getPrimaryPhone();
        this.secondaryPhone = supplier.getSecondaryPhone();
        this.website = supplier.getWebsite();
        this.street = supplier.getAddress().getStreet();
        this.city = supplier.getAddress().getCity();
        this.state = String.valueOf(supplier.getAddress().getState());
        this.zipCode = String.valueOf(supplier.getAddress().getZipCode());
        this.notes = supplier.getNotes();
    }

    public Supplier unpackNew() {
        Address address = new Address(street, city, state, zipCode);

        return Supplier.createNew(companyName, contactPerson, primaryPhone,
            secondaryPhone, website, address, notes);
    }

    public Supplier unpackExisting() {
        Address address = new Address(street, city, state, zipCode);

        return Supplier.createExisting(businessId, companyName, contactPerson,
            primaryPhone, secondaryPhone, website, address, notes);
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson.isEmpty() ? null : contactPerson;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone.isEmpty() ? null : secondaryPhone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website.isEmpty() ? null : website;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes.isEmpty() ? null : notes;
    }
}
