package com.javaiii.groupproject.AutoPartsStore.Models.products;


import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseWriteable;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Business;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;



/**
 * Part represents a Part that is sold by our store.
 *
 * Member variables for a supplier are non-null if required non-null when saving
 * to the database.
 *
 * Any new part that is to be added to the database should be created createNew
 * factory method.
 */
public class Part implements DatabaseWriteable {
    private Integer partID;
    private Business supplier;
    private Car car;
    private String name;
    private String description;
    private String category;
    private BigDecimal pricePerUnit;
    private boolean discontinued;
    private Integer quantityOnHand;
    private byte[] image;

    /**
     * For constructing a Part object. Can only be used by calling the static
     * factory methods createNew or createExisting.
     *
     * @param partID The part ID saved by the database. This should not be
     *               provided programmatically by anything other than the database
     *               controller. Any new Part to be added to the database should
     *               be instantiated using the createNew method.
     * @param business The supplier who provides this item to our company.
     *                 Non-null required.
     * @param car The car or car configuration that this part can belong to.
     *            Non-null required.
     * @param name The name of the part.
     *             Non-null required.
     * @param description A brief description of what the part is.
     * @param category The category the part belongs to.
     * @param pricePerUnit The price for each unit of a part.
     *                     Non-null required.
     * @param discontinued A flag indicating this part is no longer sold by the
     *                     company.
     * @param quantityOnHand The current number of parts that we have on hand.
     * @param image The image associated with this Part.
     */
    private Part(Integer partID, Business business, Car car, String name,
                 String description, String category, BigDecimal pricePerUnit,
                 boolean discontinued, Integer quantityOnHand, byte[] image) {
        setPartID(partID);
        setSupplier(business);
        setCar(car);
        setName(name);
        setDescription(description);
        setCategory(category);
        setPricePerUnit(pricePerUnit);
        setDiscontinued(discontinued);
        setQuantityOnHand(quantityOnHand);
        setImage(image);
    }
    /**
     * Factory method for creating a new Part item to add to the database.
     * Automatically sets the partID to -1 and discontinued to false
     * (brand new parts cannot be discontinued).
     *
     * @param business The supplier who provides this item to our company.
     *                 Non-null required.
     * @param car The car or car configuration that this part can belong to.
     *            Non-null required.
     * @param name The name of the part.
     *             Non-null required.
     * @param description A brief description of what the part is.
     * @param category The category the part belongs to.
     * @param pricePerUnit The price for each unit of a part.
     *                     Non-null required.
     * @param quantityOnHand The current number of parts that we have on hand.
     * @param image The image associated with this Part.
     * @return A new Part object that is to be written to the database.
     */
    public static Part createNew(Business business, Car car, String name, String description,
                                 String category, BigDecimal pricePerUnit,
                                 Integer quantityOnHand, byte[] image) {
        return new Part(-1, business, car, name, description, category,
            pricePerUnit,  false, quantityOnHand, image);
    }

    /**
     * Factory method for creating existing Part objects from the database or for
     * parts that have an existing Part ID number but are not saved in the
     * database already.
     *
     * Constructs a new Part object from the given information and returns it.
     *
     * @param partID The part ID saved by the database. This should not be
     *               provided programmatically by anything other than the database
     *               controller. Any new Part to be added to the database should
     *               be instantiated using the createNew method.
     * @param business The supplier who provides this item to our company.
     *                 Non-null required.
     * @param car The car or car configuration that this part can belong to.
     *            Non-null required.
     * @param name The name of the part.
     *             Non-null required.
     * @param category The category the part belongs to.
     * @param description A brief description of what the part is.
     * @param pricePerUnit The price for each unit of a part.
     *                     Non-null required.
     * @param discontinued A flag indicating this part is no longer sold by the
     *                     company.
     * @param quantityOnHand The current number of parts that we have on hand.
     * @param image The image associated with this Part.
     * @return An existing Part (typically from the database) object.
     */
    public static Part createExisting(Integer partID, Business business, Car car,
                                      String name, String category, String description,
                                      BigDecimal pricePerUnit,
                                      boolean discontinued, Integer quantityOnHand, byte[] image) {
        return new Part(partID, business, car, name, description, category,
            pricePerUnit, discontinued, quantityOnHand, image);
    }

    /**Getter for partID*/
    public Integer getPartID() {
        return partID;
    }

    /**
     * Setter for partID, requires non-null.
     * This method should only be used with a very good rationale as it can cause
     * potential problems with storing information in the database.
     */
    public void setPartID(Integer partID) {
        this.partID = Objects.requireNonNull(partID);
    }

    public Business getSupplier() {
        return supplier;
    }

    /**Setter for supplier, requires non-null*/
    public void setSupplier(Business business) {
        this.supplier = Objects.requireNonNull(business);
    }

    /**Getter for car*/
    public Car getCar() {
        return car;
    }

    /**Setter for car, requires non-null*/
    public void setCar(Car car) {
        this.car = Objects.requireNonNull(car);
    }

    /**Getter for name*/
    public String getName() {
        return name;
    }

    /**Setter for name, requires non-null*/
    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    /**Getter for description*/
    public String getDescription() {
        return description;
    }

    /**Setter for quantityPerUnit, requires non-null*/
    public void setDescription(String description) {
        this.description = description;
    }

    /**Getter for category*/
    public String getCategory() {
        return category;
    }

    /**Setter for category*/
    public void setCategory(String category) {
        this.category = category;
    }

    /**Getter for pricePerUnit*/
    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    /**Setter for pricePerUnit, requires non-null*/
    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = Objects.requireNonNull(pricePerUnit);
    }

    /**Getter for discontinued status*/
    public boolean isDiscontinued() {
        return discontinued;
    }

    /**Setter for discontinued status*/
    public void setDiscontinued(boolean discontinued) {
        this.discontinued = discontinued;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Integer getQuantityOnHand() {
        return this.quantityOnHand;
    }

    /**Getter for image*/
    public byte[] getImage() {
        return image;
    }

    /**Setter for image*/
    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return isDiscontinued() == part.isDiscontinued() &&
                   getPartID().equals(part.getPartID()) &&
                   getSupplier().equals(part.getSupplier()) &&
                   getCar().equals(part.getCar()) &&
                   getName().equals(part.getName()) &&
                   Objects.equals(getDescription(), part.getDescription()) &&
                   Objects.equals(getCategory(), part.getCategory()) &&
                   (getPricePerUnit().compareTo(part.getPricePerUnit()) == 0) &&
                   Objects.equals(getQuantityOnHand(), part.getQuantityOnHand()) &&
                   Arrays.equals(getImage(), part.getImage());
    }

    /**
     * Creates a PreparedStatement for writing a Part object to the database.
     * Used by the DatabaseManager.
     * @param connection The database connection.
     * @return A PreparedStatement containing the Part we want to write.
     * @throws SQLException If there is a problem creating the statement.
     */
    @Override
    public PreparedStatement getWriteStatement(Connection connection) throws SQLException {
        if (partID < 0) {
            return insert(connection);
        }
        else {
            return update(connection);
        }
    }

    /**
     * Creates an Insert statement for new Part objects to save to the database.
     * @param connection The database connection object.
     * @return A PreparedStatement for inserting new Part objects.
     * @throws SQLException If there is a problem creating the statement.
     */
    private PreparedStatement insert(Connection connection) throws SQLException {
        String sql = "INSERT INTO AutoPartsStore.Parts" +
                         " (SupplierID, CarID, Name, Category, PartDescription," +
                         " PricePerUnit, Discontinued, QuantityOnHand, Image)" +
                         " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        fillParameters(pStmt);
        return pStmt;
    }

    /**
     * Creates an Update statement for existing Part objects to save to the database.
     * @param connection The database connection object.
     * @return A PreparedStatement for updating existing Part records.
     * @throws SQLException If there is a problem creating the statement.
     */
    private PreparedStatement update(Connection connection) throws SQLException {
        String sql = "UPDATE AutoPartsStore.Parts" +
                         " SET SupplierID = ?, CarID = ?, Name = ?, Category = ?," +
                         " PartDescription = ?, PricePerUnit = ?," +
                         " Discontinued = ?, QuantityOnHand = ?, Image = ?" +
                         " WHERE PartID = ?;";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int idIndex = fillParameters(pStmt);
        pStmt.setInt(idIndex, this.getPartID());
        return pStmt;
    }

    /**
     * Fills a PreparedStatement with Part field data.
     * @param pStmt The PreparedStatement to fill with data.
     * @return The index value of where to put the next parameter (for ID).
     * @throws SQLException If there is a problem creating the statement.
     */
    private int fillParameters(PreparedStatement pStmt) throws SQLException {
        int i = 1;
        pStmt.setInt(i++, this.getSupplier().getBusinessID());
        pStmt.setInt(i++, this.getCar().getCarID());
        pStmt.setString(i++, this.getName());
        pStmt.setString(i++, this.getCategory());
        pStmt.setString(i++, this.getDescription());
        pStmt.setBigDecimal(i++, this.getPricePerUnit());
        pStmt.setBoolean(i++, this.isDiscontinued());
        pStmt.setInt(i++, this.getQuantityOnHand());
        if (this.getImage() != null) {
            InputStream is = new ByteArrayInputStream(this.getImage());
            pStmt.setBlob(i++, is);
        }
        else {
            pStmt.setNull(i++, Types.BLOB);
        }

        return i;
    }

    @Override
    public void setGeneratedId(Integer id) {
        if (getPartID() < 0) {
            setPartID(id);
        }
    }
}
