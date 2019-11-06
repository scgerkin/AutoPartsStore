package com.javaiii.groupproject.AutoPartsStore.Models.people;

import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Customer class holds information about an customer.
 *
 * Use the factory methods createNew or createExisting for instantiation.
 * 
 * Right now, this class does not store any information not already stored by the
 * Person base class but can be modified if additional information needs to be
 * saved.
 */
public class Customer extends Person {

    /**
     * Private constructor. Use the static factory methods for instantiation.
     * @param idNumber The ID number of the Customer stored in the database.
     * @param lastName The surname of the Customer.
     * @param firstName The first name of the Customer.
     * @param email The email address of the Customer.
     * @param primaryPhone The primary phone number for the Customer.
     * @param secondaryPhone The secondary phone for the Customer.
     * @param address The full address information for a Customer.
     * @param notes Any notes that need to be appended to a Customer file.
     */
    private Customer(Integer idNumber,
                     String lastName,
                     String firstName,
                     String email,
                     String primaryPhone,
                     String secondaryPhone,
                     Address address,
                     String notes) {
        super(idNumber, lastName, firstName, email, primaryPhone,
            secondaryPhone, address, notes);
    }

    /**
     * Factory method for creating a new Customer to be added to the database.
     * @param lastName The surname of the Customer.
     * @param firstName The first name of the Customer.
     * @param email The email address of the Customer.
     * @param primaryPhone The primary phone number for the Customer.
     * @param secondaryPhone The secondary phone for the Customer.
     * @param address The full address information for a Customer.
     * @param notes Any notes that need to be appended to a Customer file.
     * @return a new Customer from the provided data that will be saved to the
     *         database.
     */
    public static Customer createNew(String lastName,
                                     String firstName,
                                     String email,
                                     String primaryPhone,
                                     String secondaryPhone,
                                     Address address,
                                     String notes) {
        return new Customer(-1, lastName, firstName, email,
            primaryPhone, secondaryPhone, address, notes);
    }

    /**
     * Factory method for creating an Customer from existing data (typically the
     * database).
     * @param idNumber The ID number of the Customer stored in the database.
     * @param lastName The surname of the Customer.
     * @param firstName The first name of the Customer.
     * @param email The email address of the Customer.
     * @param primaryPhone The primary phone number for the Customer.
     * @param secondaryPhone The secondary phone for the Customer.
     * @param address The full address information for a Customer.
     * @param notes Any notes that need to be appended to a Customer file.
     * @return an existing Customer from existing data (typically the database).
     */
    public static Customer createExisting(Integer idNumber,
                                          String lastName,
                                          String firstName,
                                          String email,
                                          String primaryPhone,
                                          String secondaryPhone,
                                          Address address,
                                          String notes) {
        return new Customer(idNumber, lastName, firstName, email, primaryPhone,
            secondaryPhone, address, notes);
    }

    /**
     * Allows the DatabaseManager to write a Customer to the database. If a new
     * Customer is to be written, it provides a PreparedStatement with insert
     * string, otherwise update string.
     * @param connection The DB connection.
     * @return A PreparedStatement with Customer information.
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
     * Customer information in the database.
     * @param connection The DB connection.
     * @return A PreparedStatement with the new Customer data to update the records
     *         with.
     * @throws SQLException If there is a problem creating the PreparedStatement.
     */
    @Override
    protected PreparedStatement insert(Connection connection) throws SQLException {
        String sql = "INSERT INTO AutoPartsStore.Customers" +
                         "(LastName, FirstName, Email, PrimaryPhone," +
                         "SecondaryPhone, Address, City, State, ZipCode, Notes)" +
                         "VALUES" +
                         "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        fillParameters(pStmt);
        return pStmt;
    }

    /**
     * Provides a method for creating a PreparedStatement for updating existing
     * Customer information in the database.
     * @param connection The DB connection.
     * @return A PreparedStatement with the new Customer data to update the records
     *         with.
     * @throws SQLException If there is a problem creating the PreparedStatement.
     */
    @Override
    protected PreparedStatement update(Connection connection) throws SQLException {
        String sql = "UPDATE AutoPartsStore.Customers" +
                         " SET LastName = ?, FirstName = ?, Email = ?, PrimaryPhone = ?," +
                         " SecondaryPhone = ?, Address = ?, City = ?, State = ?," +
                         " ZipCode = ?, Notes = ?" +
                         " WHERE CustomerID = ?;";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int idIndex = fillParameters(pStmt);
        pStmt.setInt(idIndex, this.getIdNumber());
        return pStmt;
    }

    /**
     * Explicit override of fillParameters. Simply calls the super class method
     * and returns the index. If additional information is to be included in a customer,
     * the super method should be called and then additional information appended
     * to the end of the statements in update and insert.
     * @param pStmt The PreparedStatement we wish to fill with Person data.
     * @return The index where to insert the next parameter.
     * @throws SQLException If there is an error updating the PreparedStatement
     *                      with parameter information.
     */
    @Override
    protected int fillParameters(PreparedStatement pStmt) throws SQLException {
        return super.fillParameters(pStmt);
    }
}

