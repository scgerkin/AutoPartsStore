package com.javaiii.groupproject.AutoPartsStore.models.orders;

import com.javaiii.groupproject.AutoPartsStore.models.address.ShippingAddress;
import com.javaiii.groupproject.AutoPartsStore.models.business.ShippingProvider;
import com.javaiii.groupproject.AutoPartsStore.models.people.Customer;
import com.javaiii.groupproject.AutoPartsStore.models.products.Part;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * CustomerOrder is an order created by a Customer.
 *
 * Use factory methods createNew or createExisting for instantiation.
 */
public class CustomerOrder extends Order {
    Customer customer;
    ShippingAddress shippingAddress;
    ShippingProvider shippingProvider;

    /**
     * Private constructor. Use the static factory methods for instantiation.
     * @param orderID The ID number of the order stored in the database.
     * @param customer The Customer who created the order.
     * @param orderCreationDate The date an Order was created.
     * @param shippingAddress The address the order is to be shipped to.
     * @param shippingProvider The ShippingProvider for the Order.
     * @param shippingDate The date the Order was shipped to a Customer.
     * @param shippingFee The shipping fee to be paid by the Customer.
     * @param orderedParts A map of parts ordered with the Key being a part and
     *                     the value being the number of those parts ordered.
     * @param taxAmount The total tax amount for the order.
     * @param orderStatus The current status of the order.
     * @param notes Any notes about the order that should be stored.
     */
    private CustomerOrder(Integer orderID,
                          Customer customer,
                          Date orderCreationDate,
                          ShippingAddress shippingAddress,
                          ShippingProvider shippingProvider,
                          Date shippingDate,
                          BigDecimal shippingFee,
                          Map<Part, Integer> orderedParts,
                          BigDecimal taxAmount,
                          Status orderStatus,
                          String notes) {
        super(orderID, customer, orderedParts, orderCreationDate, shippingDate,
            shippingFee, taxAmount, orderStatus, notes);
        setCustomer(customer);
        setShippingAddress(shippingAddress);
        setShippingProvider(shippingProvider);
    }

    /**
     * Factory method for creating a new CustomerOrder to add to the database.
     *
     * Uses the Customer's Address for shipping.
     *
     * Sets the orderCreationDate to when the object is created.
     *
     * Sets the status to NEW order.
     *
     * @param customer The Customer who created the order.
     * @param shippingProvider The ShippingProvider for the Order.
     * @param shippingFee The shipping fee to be paid by the Customer.
     * @param orderedParts A map of parts ordered with the Key being a part and
     *                     the value being the number of those parts ordered.
     * @param taxAmount The total tax amount for the order.
     * @param notes Any notes about the order that should be stored.
     * @return a new CustomerOrder from the provided data (shipped to the Customer
     *         Address).
     */
    public static CustomerOrder createNew(Customer customer,
                                          Map<Part, Integer> orderedParts,
                                          ShippingProvider shippingProvider,
                                          BigDecimal shippingFee,
                                          BigDecimal taxAmount,
                                          String notes) {
        return CustomerOrder.createNew(customer, new ShippingAddress(customer),
            shippingProvider, shippingFee, orderedParts, taxAmount,
            notes);
    }

    /**
     * Factory method for creating a new CustomerOrder to add to the database.
     *
     * Uses a ShippingAddress different from the Customer's address (this should
     * be created outside this class and given to the method).
     *
     * Sets the orderCreationDate to when the object is created.
     *
     * Sets the status to NEW order.
     *
     * @param customer The Customer who created the order.
     * @param shippingAddress The address the order is to be shipped to.
     * @param shippingProvider The ShippingProvider for the Order.
     * @param shippingFee The shipping fee to be paid by the Customer.
     * @param orderedParts A map of parts ordered with the Key being a part and
     *                     the value being the number of those parts ordered.
     * @param taxAmount The total tax amount for the order.
     * @param notes Any notes about the order that should be stored.
     * @return a new Customer Order from the provided data (shipped to an Address
     *         differing from the Customer Address).
     */
    public static CustomerOrder createNew(Customer customer,
                                          ShippingAddress shippingAddress,
                                          ShippingProvider shippingProvider,
                                          BigDecimal shippingFee,
                                          Map<Part, Integer> orderedParts,
                                          BigDecimal taxAmount,
                                          String notes) {
        return new CustomerOrder(-1, customer, new Date(), shippingAddress,
            shippingProvider, null, shippingFee, orderedParts,
              taxAmount, Status.NEW, notes);
    }

    /**
     * Factory method for creating an existing CustomerOrder from established
     * data (typically the database).
     *
     * @param orderID The ID number of the order stored in the database.
     * @param customer The Customer who created the order.
     * @param orderCreationDate The date an Order was created.
     * @param shippingAddress The address the order is to be shipped to.
     * @param shippingProvider The ShippingProvider for the Order.
     * @param shippingDate The date the Order was shipped to a Customer.
     * @param shippingFee The shipping fee to be paid by the Customer.
     * @param orderedParts A map of parts ordered with the Key being a part and
     *                     the value being the number of those parts ordered.
     * @param taxAmount The total tax amount for the order.
     * @param orderStatus The current status of the order.
     * @param notes Any notes about the order that should be stored.
     * @return an existing CustomerOrder from stored data (typically the database).
     */
    public static CustomerOrder createExisting(Integer orderID,
                                               Customer customer,
                                               Date orderCreationDate,
                                               ShippingAddress shippingAddress,
                                               ShippingProvider shippingProvider,
                                               Date shippingDate,
                                               BigDecimal shippingFee,
                                               Map<Part, Integer> orderedParts,
                                               BigDecimal taxAmount,
                                               Status orderStatus,
                                               String notes) {
        return new CustomerOrder(orderID, customer, orderCreationDate,
            shippingAddress, shippingProvider, shippingDate, shippingFee,
            orderedParts, taxAmount, orderStatus, notes);
    }

    /**Getter for customer*/
    public Customer getCustomer() {
        return customer;
    }

    /**Setter for customer*/
    public void setCustomer(Customer customer) {
        this.customer = Objects.requireNonNull(customer);
    }

    /**Getter for shippingAddress*/
    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    /**Getter for shippingAddress*/
    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**Setter for shippingAddress*/
    public void setShippingAddress(Customer customer) {
        this.shippingAddress = new ShippingAddress(customer);
    }

    /**Getter for shippingProvider*/
    public ShippingProvider getShippingProvider() {
        return shippingProvider;
    }

    /**Setter for shippingProvider*/
    public void setShippingProvider(ShippingProvider shippingProvider) {
        this.shippingProvider = shippingProvider;
    }

    /**
     * Method for shipping an order with the current date.
     * A shippingProvider must be set before shipping an order.
     */
    public void shipOrder() {
        if (shippingProvider != null) {
            shipOrder(new Date());
        }
        else {
            throw new NullPointerException("There is no shipping provider for this order.");
        }
    }

    /**
     * Method for shipping an order with a provided date. Sets the order status
     * to SHIPPED.
     * @param shippingDate
     */
    public void shipOrder(Date shippingDate) {
        setShippingDate(shippingDate);
        setOrderStatus(Status.SHIPPED);
    }

}
