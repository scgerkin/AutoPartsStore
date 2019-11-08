package com.javaiii.groupproject.AutoPartsStore.command;

import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.Models.people.Employee;

public class EditEmployeeCommand {
    private Integer idNumber;
    private String lastName;
    private String firstName;
    private String title;
    private String email;
    private String primaryPhone;
    private String secondaryPhone;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String notes;

    public EditEmployeeCommand() {}

    public EditEmployeeCommand(Employee employee) {
        this.idNumber = employee.getIdNumber();
        this.lastName = employee.getLastName();
        this.firstName = employee.getFirstName();
        this.email = employee.getEmail();
        this.title = employee.getTitle();
        this.primaryPhone = employee.getPrimaryPhone();
        this.secondaryPhone = employee.getSecondaryPhone();
        this.street = employee.getAddress().getStreet();
        this.city = employee.getAddress().getCity();
        this.state = String.valueOf(employee.getAddress().getState());
        this.zipCode = String.valueOf(employee.getAddress().getZipCode());
        this.notes = employee.getNotes();
    }

    public Employee unpackExisting() {
        Address address = new Address(street, city, state, zipCode);

        return Employee.createExisting(idNumber, lastName, firstName, email,
            primaryPhone, secondaryPhone, address, notes, title);
    }

    public Employee unpackNew() {
        Address address = new Address(street, city, state, zipCode);



        return Employee.createNew(lastName, firstName, email, primaryPhone,
            secondaryPhone, address, notes, title);
    }

    public Integer getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(Integer idNumber) {
        this.idNumber = idNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.isEmpty() ? null : title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        secondaryPhone = secondaryPhone.isEmpty() ? null : secondaryPhone;
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
