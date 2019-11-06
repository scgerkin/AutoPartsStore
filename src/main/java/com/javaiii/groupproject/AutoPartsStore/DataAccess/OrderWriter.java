package com.javaiii.groupproject.AutoPartsStore.DataAccess;

import com.javaiii.groupproject.AutoPartsStore.models.orders.CustomerOrder;
import com.javaiii.groupproject.AutoPartsStore.models.orders.Order;
import com.javaiii.groupproject.AutoPartsStore.models.orders.ResupplyOrder;
import com.javaiii.groupproject.AutoPartsStore.models.people.Employee;
import com.javaiii.groupproject.AutoPartsStore.models.products.Part;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;
import java.util.Objects;

/**
 * OrderWriter class allows writing Order objects to the database.
 *
 * Because writing an Order to the database is a little more complicated than writing
 * most other objects, this class is necessary to provide some additional functionality
 * with minimal code duplication. Where code is duplicated, it is at least contained
 * within the same class instead of across multiple classes where more errors are
 * likely to happen.
 *
 * This works for both new and existing orders. When a new Order is written to the
 * database, the Order object is updated with the ID number created at writing,
 * allowing for additional manipulation of an Order without needing to retrieve
 * it entirely from the database again before allowing manipulation.
 *
 * The only public methods are the constructor and the write(order) methods.
 * The constructor needs an instance of DatabaseManager to know how it is writing
 * to the database.
 *
 * The method by which this class is used is very simple. Simple instantiate an
 * OrderWriter object, then use the write(order) method. For example:
 * [code]
 *  OrderWriter orderWriter = new OrderWriter(databaseManager);
 *  orderWriter.write(order);
 * [/code]
 *
 * This handles everything behind the scenes for writing orders. No fuss, no muss.
 */
public class OrderWriter {
    private Order order;
    private DatabaseManager db;
    private Connection connection;
    private boolean newOrder;

    /**
     * Constructor for writing an Order object to the database. It must know
     * how we are connecting to the database to work. If the DatabaseManager
     * needs to be changed (don't), instantiate a new OrderWriter object.
     * @param db The DatabaseManager currently used to write data.
     */
    public OrderWriter(DatabaseManager db) {
        this.db = db;
        connection = db.getConnection();
    }

    /**
     * The method for writing an Order object to the database. Will insert new
     * orders or update existing orders. For any new orders written, it will
     * update the Order ID number in the database to the object.
     * @param order The Order we want to write.
     * @throws SQLException For the following reasons (not exhaustive):
     * 1. Cannot connect to the database.
     * 2. Attempting to write an order using aggregated objects with incomplete
     * database information (IE writing a new part without first saving the
     * part to the database).
     */
    public void write(Order order) throws SQLException {
        this.order = Objects.requireNonNull(order);
        this.newOrder = order.getOrderID() < 0;
        writeOrder();
    }

    /**
     * Initiates the writing decision sequence.
     * @throws SQLException As above.
     */
    private void writeOrder() throws SQLException {
        if (order instanceof CustomerOrder) {
            writeCustomerOrder();
        }
        else if (order instanceof ResupplyOrder) {
            writeResupplyOrder();
        }
    }

    /**
     * Initiates the writing decision sequence for CustomerOrders.
     * @throws SQLException As above.
     */
    private void writeCustomerOrder() throws SQLException {
        if (newOrder) {
            insertCustomerOrder();
        }
        else {
            updateCustomerOrder();
        }
    }

    /**
     * Initiates the writing decision sequence for ResupplyOrders.
     * @throws SQLException As above.
     */
    private void writeResupplyOrder() throws SQLException {
        if (newOrder) {
            insertResupplyOrder();
        }
        else {
            updateResupplyOrder();
        }
    }

    /**
     * Inserts a new CustomerOrder into the database and updates the Order ID
     * number.
     * @throws SQLException As above.
     */
    private void insertCustomerOrder() throws SQLException {
        //write the order information to the Orders table
        String sql = "INSERT INTO AutoPartsStore.CustomerOrders" +
                         " (CustomerID, OrderDate, ShipName, ShipAddress," +
                         " ShipCity, ShipState, ShipZipCode, ShipperID, ShipFee," +
                         " TaxStatusID, TaxAmount, OrderStatusID, Notes)" +
                         " VALUES" +
                         " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        fillCustomerOrderParameters(pStmt);
        pStmt.executeUpdate();
        ResultSet rs = pStmt.getGeneratedKeys();
        if (rs.next()) {
            order.setOrderID(rs.getInt(1));
            addOrderDetails();
        }
        else {
            //todo provide more information
            throw new SQLException("Error inserting new order.");
        }
    }

    /**
     * Updates an existing CustomerOrder with new information.
     * @throws SQLException As above.
     */
    private void updateCustomerOrder() throws SQLException {
        //fixme doesn't add additional parts to an order
        // this may not get implemented because fuck that
        String sql = "UPDATE AutoPartsStore.CustomerOrders" +
                         " SET CustomerID = ?, OrderDate = ?, ShipName = ?," +
                         " ShipAddress = ?, ShipCity =?, ShipState = ?," +
                         " ShipZipCode = ?, ShipperID = ?, ShipFee = ?," +
                         " TaxStatusID = ?, TaxAmount = ?, OrderStatusID = ?," +
                         " Notes = ?, ShipDate = ?" +
                         " WHERE OrderID = ?;";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int idIndex = fillCustomerOrderParameters(pStmt);
        Date sqlDate = new Date(order.getShippingDate().getTime());
        if (sqlDate != null) {
            pStmt.setDate(idIndex++, sqlDate);
        }
        else {
            pStmt.setNull(idIndex++, Types.TIMESTAMP);
        }
        pStmt.setInt(idIndex, order.getOrderID());
        pStmt.executeUpdate();
    }

    /**
     * Fills CustomerOrder PreparedStatement with data.
     * @param pStmt The PreparedStatement we're filling.
     * @return A filled PreparedStatement.
     * @throws SQLException As above.
     */
    private int fillCustomerOrderParameters(PreparedStatement pStmt) throws SQLException {
        int i = 1;
        pStmt.setInt(i++, order.getInitiatingPerson().getIdNumber());
        Date orderDate = new Date(order.getOrderCreationDate().getTime());
        pStmt.setDate(i++, orderDate);
        pStmt.setString(i++, ((CustomerOrder)order).getShippingAddress().getShipName());
        pStmt.setString(i++, ((CustomerOrder)order).getShippingAddress().getStreet());
        pStmt.setString(i++, ((CustomerOrder)order).getShippingAddress().getCity());
        pStmt.setString(i++, String.valueOf(((CustomerOrder)order).getShippingAddress().getState()));
        pStmt.setString(i++, String.valueOf(((CustomerOrder)order).getShippingAddress().getZipCode()));
        pStmt.setInt(i++, ((CustomerOrder)order).getShippingProvider().getBusinessID());
        pStmt.setBigDecimal(i++, order.getShippingFee());
        BigDecimal taxAmount = order.getTaxAmount();
        int taxStatus = (taxAmount.compareTo(BigDecimal.ZERO) > 0) ? 1 : 0;
        pStmt.setInt(i++, taxStatus);
        pStmt.setBigDecimal(i++, taxAmount);
        Integer orderStatus = Order.statusIntegerMap.get(order.getOrderStatus());
        pStmt.setInt(i++, orderStatus);
        pStmt.setString(i++, order.getNotes());
        return i;
    }

    /**
     * Adds Parts list and quantity into the OrderDetails table.
     * @throws SQLException As above.
     */
    private void addOrderDetails() throws SQLException {
        Map<Part, Integer> parts = order.getOrderedParts();

        for (Part part : parts.keySet()) {
            // insert part information into OrderDetails
            writePart(part, parts.get(part));
        }
    }

    /**
     * Writes individual Part with quantity to the OrderDetails table.
     * @param part The part we want to add for the given order.
     * @param quantity The quantity of Parts in the order.
     * @throws SQLException As above.
     */
    private void writePart(Part part, Integer quantity) throws SQLException {
        String orderIDColumnName = null;
        if (order instanceof CustomerOrder) {
            orderIDColumnName = "CustomerOrderID";
        }
        else {
            orderIDColumnName = "ResupplyOrderID";
        }

        String sql = "INSERT INTO AutoPartsStore.OrderDetails" +
                         " ("+orderIDColumnName+", PartID, Quantity, UnitPrice)" +
                         " VALUES (?, ?, ?, ?);";
        PreparedStatement pStmt = connection.prepareStatement(sql);
        int i = 1;
        pStmt.setInt(i++, order.getOrderID());
        pStmt.setInt(i++, part.getPartID());
        pStmt.setInt(i++, quantity);
        pStmt.setBigDecimal(i++, part.getPricePerUnit());
        pStmt.executeUpdate();
    }

    /**
     * Inserts a new ResupplyOrder into the database and sets the order ID Number.
     * @throws SQLException As above.
     */
    private void insertResupplyOrder() throws SQLException {
        String sql = "INSERT INTO AutoPartsStore.ResupplyOrders" +
                         " (CreatedBy, CreationDate, ShipFee, ShipDate, TaxAmount," +
                         " OrderStatusID, ApprovedBy, ApprovedDate, Notes)" +
                         " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement pStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        fillResupplyOrderParameters(pStmt);
        pStmt.executeUpdate();
        ResultSet rs = pStmt.getGeneratedKeys();
        if (rs.next()) {
            order.setOrderID(rs.getInt(1));
            addOrderDetails();
        }
        else {
            //todo provide more information
            throw new SQLException("Error inserting new order.");
        }
    }

    /**
     * Updates an existing ResupplyOrder.
     * @throws SQLException As above.
     */
    private void updateResupplyOrder() throws SQLException {
        String sql = "UPDATE AutoPartsStore.ResupplyOrders" +
                         " SET CreatedBy = ?, CreationDate = ?, ShipFee = ?," +
                         " ShipDate = ?," +
                         " TaxAmount = ?, OrderStatusID = ?, ApprovedBy = ?," +
                         " ApprovedDate = ?, Notes = ?" +
                         " WHERE ResupplyOrderID = ?;";
        PreparedStatement pStmt = connection.prepareStatement(sql);
        int idIndex = fillResupplyOrderParameters(pStmt);
        pStmt.setInt(idIndex, order.getOrderID());
        pStmt.executeUpdate();
    }

    /**
     * Fills a ResupplyOrder PreparedStatement with the order data.
     * @param pStmt The PreparedStatement we want to fill.
     * @return The filled PreparedStatement.
     * @throws SQLException As above.
     */
    private int fillResupplyOrderParameters(PreparedStatement pStmt) throws SQLException {
        int i = 1;
        pStmt.setInt(i++, order.getInitiatingPerson().getIdNumber());
        Date sqlDate = new Date(order.getOrderCreationDate().getTime());
        pStmt.setDate(i++, sqlDate);
        pStmt.setBigDecimal(i++, order.getShippingFee());
        try {
            Date shipDate = new Date(order.getShippingDate().getTime());
            pStmt.setDate(i++, shipDate);
        }
        catch (NullPointerException ex) {
            pStmt.setNull(i++, Types.TIMESTAMP);
        }
        pStmt.setBigDecimal(i++, order.getTaxAmount());
        Integer orderStatus = Order.statusIntegerMap.get(order.getOrderStatus());
        pStmt.setInt(i++, orderStatus);
        Employee approvingManager = ((ResupplyOrder)order).getApprovingManager();
        Integer approvingManagerId = (approvingManager == null) ? null : approvingManager.getIdNumber();
        if (approvingManagerId == null) {
            pStmt.setNull(i++, Types.INTEGER);
        }
        else {
            pStmt.setInt(i++, approvingManagerId);
        }
        java.util.Date orderApprovalDate = ((ResupplyOrder)order).getOrderApprovalDate();
        if (orderApprovalDate != null) {
            sqlDate = new Date(((ResupplyOrder) order).getOrderApprovalDate().getTime());
            pStmt.setDate(i++, sqlDate);
        }
        else {
            pStmt.setNull(i++, Types.TIMESTAMP);
        }
        pStmt.setString(i++, order.getNotes());
        return i;
    }
}
