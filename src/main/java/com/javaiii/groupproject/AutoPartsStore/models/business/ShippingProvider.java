package com.javaiii.groupproject.AutoPartsStore.models.business;

import com.javaiii.groupproject.AutoPartsStore.models.address.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ShippingProvider encapsulates a Business that provides shipping services for
 * our products.
 *
 * All objects should be instantiated using the static factory methods provided
 * (createNew and createExisting).
 */
public class ShippingProvider extends Business {

    /**
     * Private constructor.
     *
     * All objects should be created using the factory methods createNew and
     * createExisting.
     *
     * @param businessID The Business ID saved by the database. This should not
     *                   be provided programmatically by anything other than the
     *                   database controller. Any new Business to be added to the
     *                   database should be instantiated with a value of -1 to
     *                   flag the controller to this action.
     * @param companyName The Business company name.
     * @param contactName The contact person within the company.
     * @param primaryPhone The primary phone number for contacting the Business.
     * @param secondaryPhone A secondary contact phone number.
     * @param website The fully qualified URL for the Business's home website.
     * @param address A complete Address object containing the Business's primary
     *                address information.
     * @param notes Any notes that need to be recorded regarding the Business.
     */
    private ShippingProvider(Integer businessID, String companyName, String contactName,
                             String primaryPhone, String secondaryPhone, String website,
                             Address address, String notes) {
        super(businessID, companyName, contactName, primaryPhone, secondaryPhone,
            website, address, notes);
    }

    /**
     * Factory method for creating a new Business that is to be added to the database.
     *
     * This sets each possible field for a Business to an empty string for fields
     * that are able to be NULL in the database.
     *
     * Prefer using the fully parameterized version of this method to save all
     * relevant data to the database.
     *
     * @param companyName The Business company name.
     * @param primaryPhone The primary phone number for contacting the Business.
     * @param address A complete Address object containing the Business's primary
     *                address information.
     * @return a new Business object with empty fields to be saved to the database.
     */
    public static ShippingProvider createNew(String companyName, String primaryPhone,
                                             Address address) {
        return createNew(companyName, "",
            primaryPhone, "", "", address, "");
    }

    /**
     * Factory method for creating a new Business that is to be added to the database.
     *
     * Prefer using this method over the overloaded method requiring less parameters
     * so that all relevant data can be saved to the database.
     *
     * @param companyName The Business company name.
     * @param contactName The contact person within the company.
     * @param primaryPhone The primary phone number for contacting the Business.
     * @param secondaryPhone A secondary contact phone number.
     * @param website The fully qualified URL for the Business's home website.
     * @param address A complete Address object containing the Business's primary
     *                address information.
     * @param notes Any notes that need to be recorded regarding the Business.
     * @return a new Business object to be saved to the database.
     */
    public static ShippingProvider createNew(String companyName, String contactName,
                                             String primaryPhone, String secondaryPhone,
                                             String website, Address address, String notes) {
        return new ShippingProvider(-1, companyName, contactName, primaryPhone,
            secondaryPhone, website, address, notes);
    }

    /**
     * Factory method for instantiating a Business from existing data (typically
     * the database).
     *
     * @param businessID The Business ID saved by the database. This should not
     *                   be provided programmatically by anything other than the
     *                   database controller. Any new Business to be added to the
     *                   database should be instantiated with a value of -1 to
     *                   flag the controller to this action.
     * @param companyName The Business company name.
     * @param contactName The contact person within the company.
     * @param primaryPhone The primary phone number for contacting the Business.
     * @param secondaryPhone A secondary contact phone number.
     * @param website The fully qualified URL for the Business's home website.
     * @param address A complete Address object containing the Business's primary
     *                address information.
     * @param notes Any notes that need to be recorded regarding the Business.
     * @return an existing Business from established data (typically the database).
     */
    public static ShippingProvider createExisting(Integer businessID, String companyName,
                                          String contactName, String primaryPhone,
                                          String secondaryPhone, String website,
                                          Address address, String notes) {
        return new ShippingProvider(businessID, companyName, contactName, primaryPhone,
            secondaryPhone, website, address, notes);
    }

    /**
     * Creates a PreparedStatement for use by the DatabaseManager for adding
     * or updating Shipper records.
     * @param connection The database connection.
     * @return A PreparedStatement object with Shipper information and a protocol.
     * @throws SQLException If there is a problem creating the statement.
     */
    @Override
    public PreparedStatement getWriteStatement(Connection connection) throws SQLException {
        if (getBusinessID() < 0) {
            return insert(connection);
        }
        else {
            return update(connection);
        }
    }

    /**
     * Creates an insert statement new existing Shipper information to be added
     * to the database.
     * @param connection The database connection.
     * @return An insert PreparedStatement with Shipper information.
     * @throws SQLException If there is a problem creating the statement.
     */
    private PreparedStatement insert(Connection connection) throws SQLException {
        String sql = "INSERT INTO AutoPartsStore.Shippers (Company, ContactName," +
                         " PrimaryPhone, SecondaryPhone, Website, Address, City," +
                         " State, ZipCode, Notes)" +
                         " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        fillParameters(pStmt);
        return pStmt;
    }

    /**
     * Creates an update statement for existing Shipper information to be updated
     * in the database.
     * @param connection The database connection.
     * @return An update PreparedStatement with Shipper information.
     * @throws SQLException If there is a problem creating the statement.
     */
    private PreparedStatement update(Connection connection) throws SQLException {
        String sql = "UPDATE AutoPartsStore.Shippers" +
                         " SET Company = ?, ContactName = ?, PrimaryPhone = ?," +
                         " SecondaryPhone = ?, Website = ?, Address = ?, City = ?," +
                         " State = ?, ZipCode = ?, Notes = ?" +
                         " WHERE SupplierID = ?;";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int idIndex = fillParameters(pStmt);
        pStmt.setInt(idIndex, this.getBusinessID());
        return pStmt;
    }
}
