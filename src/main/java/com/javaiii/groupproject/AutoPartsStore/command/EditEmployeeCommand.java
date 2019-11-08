package com.javaiii.groupproject.AutoPartsStore.command;

import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.Models.people.Employee;
import lombok.Data;

@Data
public class EditEmployeeCommand {
    public Integer idNumber;
    public String lastName;
    public String firstName;
    public String title;
    public String email;
    public String primaryPhone;
    public String secondaryPhone;
    public String street;
    public String city;
    public String state;
    public String zipCode;
    public String notes;

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

        secondaryPhone = (secondaryPhone.isEmpty()) ? null : secondaryPhone;

        return Employee.createNew(lastName, firstName, email, primaryPhone,
            secondaryPhone, address, notes, title);
    }
}
