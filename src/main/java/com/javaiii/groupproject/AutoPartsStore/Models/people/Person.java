package com.javaiii.groupproject.AutoPartsStore.Models.people;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseWriteable;
import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.Models.orders.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Base class for the people we store in the database.
 *
 * Any class extending this should implement factory methods for creating a new
 * and existing object rather than allow instantiation from a constructor.
 */
public abstract class Person implements DatabaseWriteable {
    private Integer idNumber;
    private String lastName;
    private String firstName;
    private String email;
    private String primaryPhone;
    private String secondaryPhone;
    private Address address;
    private String notes;
    private List<Order> orderList;

    /**
     * This constructor should be used to construct a new Person object by the
     * inheriting class. The class should also typically have a private constructor
     * and provide factory methods for creating objects that extend Person.
     * @param idNumber The ID number of the person stored in the database.
     * @param lastName The surname of the Person.
     * @param firstName The first name of the Person.
     * @param email The email address of the Person.
     * @param primaryPhone The primary phone number for the person.
     * @param secondaryPhone The secondary phone number for the person.
     * @param address The full address information for a Person.
     * @param notes Any notes that need to be appended to a Person file.
     */
    protected Person(Integer idNumber,
                     String lastName,
                     String firstName,
                     String email,
                     String primaryPhone,
                     String secondaryPhone,
                     Address address,
                     String notes) {
        setIdNumber(idNumber);
        setLastName(lastName);
        setFirstName(firstName);
        setEmail(email);
        setPrimaryPhone(primaryPhone);
        setSecondaryPhone(secondaryPhone);
        setAddress(address);
        setNewNotes(notes);
    }

    /**Getter for idNumber*/
    public Integer getIdNumber() {
        return idNumber;
    }

    /**Setter for idNumber*/
    public void setIdNumber(Integer idNumber) {
        this.idNumber = Objects.requireNonNull(idNumber);
    }

    /**Getter for lastName*/
    public String getLastName() {
        return lastName;
    }

    /**Setter for lastName*/
    public void setLastName(String lastName) {
        this.lastName = Objects.requireNonNull(lastName);
    }

    /**Getter for firstName*/
    public String getFirstName() {
        return firstName;
    }

    /**Setter for firstName*/
    public void setFirstName(String firstName) {
        this.firstName = Objects.requireNonNull(firstName);
    }

    /**Getter for email*/
    public String getEmail() {
        return email;
    }

    /**Setter for email*/
    public void setEmail(String email) {
        this.email = email;
    }

    /**Getter for primaryPhone*/
    public String getPrimaryPhone() {
        return primaryPhone;
    }

    /**Setter for primaryPhone*/
    public void setPrimaryPhone(String primaryPhone) {
        if (primaryPhone.length() != 10) {
            throw new IllegalArgumentException("Phone number must be 10 characters only with no delimiters");
        }
        this.primaryPhone = Objects.requireNonNull(primaryPhone);
    }

    /**Getter for secondaryPhone*/
    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    /**Setter for secondaryPhone*/
    public void setSecondaryPhone(String secondaryPhone) {
        if (secondaryPhone == null) {
            this.secondaryPhone = null;
            return;
        }
        if (secondaryPhone.length() != 10) {
            throw new IllegalArgumentException("Phone number must be 10 characters only with no delimiters");
        }
        this.secondaryPhone = secondaryPhone;
    }

    /**Getter for address*/
    public Address getAddress() {
        return address;
    }

    /**Setter for address*/
    public void setAddress(Address address) {
        this.address = Objects.requireNonNull(address);
    }

    /**Getter for notes*/
    public String getNotes() {
        return notes;
    }

    /**
     * Method for adding new notes to a Business.
     * In general, existing notes should only ever be appended to, not overwritten.
     * Prefer this method of setNewNotes().
     * @param note The note we wish to append to the Business.
     */
    public void appendNote(String note) {
        if (this.notes != null) {
            this.notes += "\n" + note;
        }
        else {
            this.notes = note;
        }
    }

    /**
     * Method for clearing and overwriting existing Person notes. This method
     * should not be used except in the case that the notes need to be completely
     * overwritten. In general, notes should be appended to using the appendNotes()
     * method to maintain a constant record of any notes.
     * @param notes The new notes we wish to set for the Person. Ideally, an
     *              entire note field should not completely overwritten. We should
     *              take the current notes from the system, remove what we need,
     *              and then use this method to set the notes to the updated information.
     */
    public void setNewNotes(String notes) {
        this.notes = notes;
    }

    /**Getter for orderList*/
    public List<Order> getOrderList() {
        return orderList;
    }

    /**Setter for orderList*/
    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    /**
     * Equals override.
     * @param o The object to compare.
     * @return True if all Person values are the same between this and the object
     *          we are comparing.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return getIdNumber().equals(person.getIdNumber()) &&
                   getLastName().equals(person.getLastName()) &&
                   getFirstName().equals(person.getFirstName()) &&
                   getEmail().equals(person.getEmail()) &&
                   getPrimaryPhone().equals(person.getPrimaryPhone()) &&
                   Objects.equals(getSecondaryPhone(), person.getSecondaryPhone()) &&
                   getAddress().equals(person.getAddress()) &&
                   Objects.equals(getNotes(), person.getNotes());
    }

    /**
     * For creating a PreparedStatement for inserting new records into the DB.
     * @param connection The DB connection.
     * @return A PreparedStatement with Person information to be inserted.
     * @throws SQLException If there's an error in the insertion.
     */
    protected abstract PreparedStatement insert(Connection connection) throws SQLException;

    /**
     * For creating a PreparedStatement for updating existing records in the DB.
     * @param connection The DB connection.
     * @return A PreparedStatement with Person information to be updated.
     * @throws SQLException If there's an error in the update.
     */
    protected abstract PreparedStatement update(Connection connection) throws SQLException;

    /**
     * Fills a PreparedStatement with Person information. Any query string should
     * follow the order lain out in this method for insertion. Any additional
     * information to be included should be handled in an override of this method.
     * @param pStmt The PreparedStatement we wish to fill with Person data.
     * @return The index value of where to insert the next parameter.
     * @throws SQLException If there's an error setting a parameter.
     */
    protected int fillParameters(PreparedStatement pStmt) throws SQLException {
        int i = 1;
        pStmt.setString(i++, this.getLastName());
        pStmt.setString(i++, this.getFirstName());
        pStmt.setString(i++, this.getEmail());
        pStmt.setString(i++, this.getPrimaryPhone());
        pStmt.setString(i++, this.getSecondaryPhone());
        pStmt.setString(i++, this.getAddress().getStreet());
        pStmt.setString(i++, this.getAddress().getCity());
        pStmt.setString(i++, String.valueOf(this.getAddress().getState()));
        pStmt.setString(i++, String.valueOf(this.getAddress().getZipCode()));
        pStmt.setString(i++, this.getNotes());
        return i;
    };

    @Override
    public void setGeneratedId(Integer id) {
        if (getIdNumber() < 0) {
            setIdNumber(id);
        }
    }
}
