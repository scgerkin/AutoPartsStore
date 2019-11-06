package com.javaiii.groupproject.AutoPartsStore.Models.people;

import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;

import java.sql.*;
import java.util.Objects;

/**
 * Employee class holds information about an employee.
 *
 * Use the factory methods createNew or createExisting for instantiation.
 */
public class Employee extends Person {
    private String title;

    /**
     * Private constructor. Use the static factory methods for instantiation.
     * @param idNumber The ID number of the Employee stored in the database.
     * @param lastName The surname of the Employee.
     * @param firstName The first name of the Employee.
     * @param email The email address of the Employee.
     * @param primaryPhone The primary phone number for the Employee.
     * @param secondaryPhone The secondary phone for the Employee.
     * @param address The full address information for a Employee.
     * @param notes Any notes that need to be appended to a Employee file.
     * @param title The title of the Employee (ie Sales, Marketing, etc).
     */
    private Employee(Integer idNumber,
                     String lastName,
                     String firstName,
                     String email,
                     String primaryPhone,
                     String secondaryPhone,
                     Address address,
                     String notes,
                     String title) {
        super(idNumber, lastName, firstName, email, primaryPhone, secondaryPhone, address, notes);
        this.title = title;
    }

    /**
     * Factory method for creating a new Employee to be added to the database.
     * @param lastName The surname of the Employee.
     * @param firstName The first name of the Employee.
     * @param email The email address of the Employee.
     * @param primaryPhone The primary phone number for the Employee.
     * @param secondaryPhone The secondary phone for the Employee.
     * @param address The full address information for a Employee.
     * @param notes Any notes that need to be appended to a Employee file.
     * @param title The title of the Employee (ie Sales, Marketing, etc).
     * @return a new Employee from the provided data that will be saved to the
     *         database.
     */
    public static Employee createNew(String lastName,
                                   String firstName,
                                   String email,
                                   String primaryPhone,
                                   String secondaryPhone,
                                   Address address,
                                   String notes,
                                   String title) {
        return new Employee(-1, lastName, firstName, email, primaryPhone,
            secondaryPhone,address, notes, title);
    }

    /**
     * Factory method for creating an Employee from existing data (typically the
     * database).
     * @param idNumber The ID number of the Employee stored in the database.
     * @param lastName The surname of the Employee.
     * @param firstName The first name of the Employee.
     * @param email The email address of the Employee.
     * @param primaryPhone The primary phone number for the Employee.
     * @param secondaryPhone The secondary phone for the Employee.
     * @param address The full address information for a Employee.
     * @param notes Any notes that need to be appended to a Employee file.
     * @param title The title of the Employee (ie Sales, Marketing, etc).
     * @return an existing Employee from existing data (typically the database).
     */
    public static Employee createExisting(Integer idNumber,
                                        String lastName,
                                        String firstName,
                                        String email,
                                        String primaryPhone,
                                        String secondaryPhone,
                                        Address address,
                                        String notes,
                                        String title) {
        return new Employee(idNumber, lastName, firstName, email, primaryPhone,
            secondaryPhone, address, notes, title);
    }

    /**Getter for title*/
    public String getTitle() {
        return title;
    }

    /**Setter for title*/
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Override of parent equals method makes sure Title is the same across objects.
     * @param o The object to compare.
     * @return True if both objects contain the same data.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getTitle(), employee.getTitle());
    }

    /**
     * Allows the DatabaseManager to write an Employee to the database. If a new
     * Employee is to be written, it provides a PreparedStatement with insert
     * string, otherwise update string.
     * @param connection The DB connection.
     * @return A PreparedStatement with Employee information.
     * @throws SQLException If there is a problem creating the PreparedStatement.
     */
    @Override
    public PreparedStatement getWriteStatement(Connection connection) throws SQLException {
        if (getIdNumber() < 0) {
            return insert(connection);
        }
        else {
            return update(connection);
        }
    }

    /**
     * Provides a method for creating a PreparedStatement for inserting new
     * Employee information in the database.
     * @param connection The DB connection.
     * @return A PreparedStatement with the new Employee data to update the records
     *         with.
     * @throws SQLException If there is a problem creating the PreparedStatement.
     */
    @Override
    protected PreparedStatement insert(Connection connection) throws SQLException {
        String sql = "INSERT INTO AutoPartsStore.Employees" +
                         " (LastName, FirstName, Email, PrimaryPhone," +
                         " SecondaryPhone, Address, City, State, ZipCode, Notes," +
                         " Title)" +
                         " VALUES" +
                         " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        fillParameters(pStmt);
        return pStmt;
    }

    /**
     * Provides a method for creating a PreparedStatement for updating existing
     * Employee information in the database.
     * @param connection The DB connection.
     * @return A PreparedStatement with the new Employee data to update the records
     *         with.
     * @throws SQLException If there is a problem creating the PreparedStatement.
     */
    @Override
    protected PreparedStatement update(Connection connection) throws SQLException {
        String sql = "UPDATE AutoPartsStore.Employees" +
                         " SET LastName = ?, FirstName = ?, Email = ?," +
                         " PrimaryPhone = ?, SecondaryPhone = ?, Address = ?," +
                         " City = ?, State = ?, ZipCode = ?, Notes = ?, Title = ?" +
                         " WHERE EmployeeID = ?;";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int idIndex = fillParameters(pStmt);
        pStmt.setInt(idIndex, getIdNumber());
        return pStmt;
    }

    /**
     * Fills a PreparedStatement with Employee information by first filling it
     * with data fields that are the same across all Person objects and then
     * adds Title to the end of the statement.
     * @param pStmt The PreparedStatement we wish to fill with Person data.
     * @throws SQLException If there is an error updating the PreparedStatement
     *                      with parameter information.
     */
    @Override
    protected int fillParameters(PreparedStatement pStmt) throws SQLException {
        int i = super.fillParameters(pStmt);
        pStmt.setString(i++, this.getTitle());
        return i;
    }


}
