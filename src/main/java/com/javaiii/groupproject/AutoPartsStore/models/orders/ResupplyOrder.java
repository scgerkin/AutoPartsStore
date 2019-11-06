package com.javaiii.groupproject.AutoPartsStore.models.orders;

import com.javaiii.groupproject.AutoPartsStore.models.people.Employee;
import com.javaiii.groupproject.AutoPartsStore.models.products.Part;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * ResupplyOrder is an order created by an Employee to restock inventory in the
 * store.
 *
 * Use factory methods createNew or createExisting for instantiation.
 */
public class ResupplyOrder extends Order {
    private Date orderApprovalDate;
    private Employee approvingManager;

    /**
     * Private constructor. Use the static factory methods for instantiation.
     * @param orderID The ID number of the order stored in the database.
     * @param initiatingEmployee The Employee who created the order.
     * @param orderCreationDate The date an order was created.
     * @param orderApprovalDate The date an order was approved by a manager before
     *                          being placed with a Supplier.
     * @param approvingManager The manager who approved the order for resupply.
     * @param shippingDate The date the order was shipped from a supplier.
     * @param shippingFee The shipping fee to be paid to the supplier or shipping
     *                    company.
     * @param orderedParts A map of parts ordered with the Key being a part and
     *                     the value being the number of those parts ordered.
     * @param taxAmount The total tax amount for the order.
     * @param orderStatus The current status of the order.
     * @param notes Any notes about the order that should be stored.
     */
    private ResupplyOrder(Integer orderID,
                          Employee initiatingEmployee,
                          Date orderCreationDate,
                          Date orderApprovalDate,
                          Employee approvingManager,
                          Date shippingDate,
                          BigDecimal shippingFee,
                          Map<Part, Integer> orderedParts,
                          BigDecimal taxAmount,
                          Status orderStatus,
                          String notes) {
        super(orderID, initiatingEmployee, orderedParts, orderCreationDate,
            shippingDate, shippingFee, taxAmount, orderStatus, notes);
        setOrderApprovalDate(orderApprovalDate);
        setApprovingManager(approvingManager);
    }

    /**
     * Factory method for creating a new ResupplyOrder to add to the database.
     *
     * Sets the orderCreationDate to when the object is created.
     *
     * Sets the status to NEW order.
     *
     * Sets orderApprovalDate, approvingManager, and shippingDate to NULL.
     *
     * @param initiatingEmployee The Employee who created the order.
     * @param shippingFee The shipping fee to be paid to the supplier or shipping
     *                    company.
     * @param taxAmount The total tax amount for the order.
     * @param notes Any notes about the order that should be stored.
     * @return a new ResupplyOrder to add to the database.
     */
    public static ResupplyOrder createNew(Employee initiatingEmployee,
                                          BigDecimal shippingFee,
                                          Map<Part, Integer> orderedParts,
                                          BigDecimal taxAmount,
                                          String notes) {
        return new ResupplyOrder(-1, initiatingEmployee, new Date(), null,
            null, null, shippingFee, orderedParts,
            taxAmount, Status.NEW, notes);
    }

    /**
     * Factory method for creating a new ResupplyOrder from existing data (typically
     * the database.
     * @param orderID The ID number of the order stored in the database.
     * @param initiatingEmployee The Employee who created the order.
     * @param orderCreationDate The date an order was created.
     * @param orderApprovalDate The date an order was approved by a manager before
     *                          being placed with a Supplier.
     * @param approvingManager The manager who approved the order for resupply.
     * @param shippingDate The date the order was shipped from a supplier.
     * @param shippingFee The shipping fee to be paid to the supplier or shipping
     *                    company.
     * @param orderedParts A map of parts ordered with the Key being a part and
     *                     the value being the number of those parts ordered.
     * @param taxAmount The total tax amount for the order.
     * @param orderStatus The current status of the order.
     * @param notes Any notes about the order that should be stored.
     * @return An existing ResupplyOrder from established data.
     */
    public static ResupplyOrder createExisting(Integer orderID,
                                               Employee initiatingEmployee,
                                               Date orderCreationDate,
                                               Date orderApprovalDate,
                                               Employee approvingManager,
                                               Date shippingDate,
                                               BigDecimal shippingFee,
                                               Map<Part, Integer> orderedParts,
                                               BigDecimal taxAmount,
                                               Status orderStatus,
                                               String notes) {
        return new ResupplyOrder(orderID, initiatingEmployee, orderCreationDate,
            orderApprovalDate, approvingManager, shippingDate, shippingFee,
            orderedParts, taxAmount, orderStatus, notes);
    }

    /**
     * Sets a ResupplyOrder status to APPROVED, marks the approving employee,
     * and sets the approvalDate to the current Date when the method is used.
     * @param approvingManager The employee (manager) who approved the ResupplyOrder.
     */
    public void approveOrder(Employee approvingManager) {
        approveOrder(approvingManager, new Date());
    }

    /**
     * Sets a ResupplyOrder status to APPROVED, marks the approving employee,
     * and sets the orderApprovalDate to the Date parameter.
     * @param approvingManager The employee (manager) who approved the ResupplyOrder.
     * @param orderApprovalDate The date the order was approved for submission to
     *                          a supplier.
     */
    public void approveOrder(Employee approvingManager, Date orderApprovalDate) {
        setOrderApprovalDate(orderApprovalDate);
        setApprovingManager(approvingManager);
        setOrderStatus(Status.APPROVED);
    }

    /**Getter for orderApprovalDate*/
    public Date getOrderApprovalDate() {
        return orderApprovalDate;
    }

    /**Setter for orderApprovalDate if it is different from current Date*/
    public void setOrderApprovalDate(Date orderApprovalDate) {
        this.orderApprovalDate = orderApprovalDate;
    }

    /**Getter for approvingManager*/
    public Employee getApprovingManager() {
        return approvingManager;
    }

    /**Setter for approvingManager, prefer using approveOrder*/
    public void setApprovingManager(Employee approvingManager) {
        this.approvingManager = approvingManager;
    }

}

