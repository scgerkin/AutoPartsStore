package com.javaiii.groupproject.AutoPartsStore.DataAccess.test;


import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.models.business.ShippingProvider;
import com.javaiii.groupproject.AutoPartsStore.models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.models.orders.CustomerOrder;
import com.javaiii.groupproject.AutoPartsStore.models.orders.Order;
import com.javaiii.groupproject.AutoPartsStore.models.orders.ResupplyOrder;
import com.javaiii.groupproject.AutoPartsStore.models.people.Customer;
import com.javaiii.groupproject.AutoPartsStore.models.people.Employee;
import com.javaiii.groupproject.AutoPartsStore.models.products.Car;
import com.javaiii.groupproject.AutoPartsStore.models.products.Part;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DATest {

    public static DatabaseManager db;
    public static Address testAddress;

    public static void main(String[] args) throws Exception {
        db = new DatabaseManager(true);
        testAddress = new Address("123 test", "TestCity", "GA", "99999");
        System.out.println("MAKE SURE DATABASE HAS BEEN CLEANED" +
                               " AND RESET BEFORE RUNNING TESTS OR THEY WILL ALWAYS FAIL!");

        try {// employee test suite
            Employee employee = test_createAndWriteNewEmployee();
            test_getAllEmployees();
            test_getEmployeeByIDReturnsEqualInformationToNewlyCreatedEmployee(employee);
            test_getEmployeeByEmailReturnsEqualInformationToNewlyCreatedEmployee(employee);
            test_getAllEmployeesReturnsListOf102();
            System.out.println("Employee Suite PASSED.");
        }
        catch (Exception ex) {
            System.err.println("EMPLOYEE SUITE FAILURE");
            ex.printStackTrace();
        }

        try {// customer test suite
            Customer customer = test_createAndWriteNewCustomer();
            test_getCustomerByIDReturnsEqualInformationToNewlyCreatedCustomer(customer);
            test_getCustomerByEmailReturnsEqualInformationToNewlyCreatedCustomer(customer);
            System.out.println("Customer Suite PASSED.");
        }
        catch (Exception ex) {
            System.err.println("CUSTOMER SUITE FAILURE");
            ex.printStackTrace();
        }

        try {// supplier test suite
            Supplier supplier = test_createAndWriteNewSupplier();
            test_retrieveSupplierByIDReturnsEqualInformationToNewlyCreatedSupplier(supplier);
            System.out.println("Supplier Suite PASSED.");
        }
        catch (Exception ex) {
            System.err.println("SUPPLIER SUITE FAILURE");
            ex.printStackTrace();
        }

        try {// shipping provider test suite
            ShippingProvider shippingProvider = test_createAndWriteNewShippingProvider();
            test_retrieveShippingProviderByIDReturnsEqualInformationToNewlyCreatedShippingProvider(shippingProvider);
            System.out.println("Shipping Provider Suite PASSED.");
        }
        catch (Exception ex) {
            System.err.println("SHIPPING PROVIDER FAILURE");
            ex.printStackTrace();
        }

        try {// car test suite
            test_retrieveCarMakeByIDReturnsKnownValue();
            test_retrieveCarMakeIDByNameReturnsKnownValue();
            test_getAllCarMakesReturnsCorrectNumberOfMakes();
            test_getAllCarModelsReturnsCorrectNumberOfModels();
            test_getAllProductionYearsReturnsCorrectNumberOfYearValues();
            test_retrieveCarModelByIDReturnsKnownValue();
            test_retrieveCarModelIDByNameReturnsKnownValue();
            Car car = test_retrieveCarConfigByIDReturnsKnownValue();
            test_retrieveCarConfigByFieldsReturnsKnownValue();
            System.out.println("Car Suite PASSED.");
        }
        catch (Exception ex) {
            System.err.println("CAR SUITE FAILURE");
            ex.printStackTrace();
        }

        try {// part test suite
            Supplier supplier = db.retrieveSupplierByID(6);
            Car car = db.retrieveCarConfigByID(300);
            test_writeTenPartsToDatabase(supplier, car);
            test_getAllPartsReturns20PartsOnFreshDatabase();
            test_getAllActivePartsReturnsNonEmptyList();
            System.out.println("Part Suite PASSED.");
        }
        catch (Exception ex) {
            System.err.println("PART SUITE FAILURE");
            ex.printStackTrace();
        }

        try {// order test suite
            test_getCustomerOrderByIDReturnsCorrectInformation();
            test_collectAllOrdersReturnsNotEmptyListForCustomerWithOrders();
            test_collectAllOrdersReturnsEmptyListForCustomerWithoutOrders();

            test_getResupplyOrderByIDReturnsCorrectInformation();
            test_collectAllOrdersReturnsNotEmptyListForEmployeeWithOrders();
            test_collectAllOrdersReturnsEmptyListForEmployeeWithoutOrders();
        }
        catch (Exception ex) {
            System.err.println("ORDER SUITE FAILURE");
            ex.printStackTrace();
        }
    }

    public static Employee test_createAndWriteNewEmployee() {
        Employee employee = Employee.createNew(
            "TestLastName",
            "TestFirstName",
            "test@test.test",
            "0123456789",
            "0123456789",
            testAddress,
            null,
            "TestTitle"
        );
        try {
            db.saveToDatabase(employee);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_createAndWriteNewEmployee",
                "SQL Exception, see stacktrace above.");
        }
        if (employee.getIdNumber() < 0) {
            throw new TestFailureException(
                "test_createAndWriteNewEmployee",
                "Create and Write new employee did not update ID."
            );
        }
        return employee;
    }

    public static void test_getAllEmployees() {
        List<Employee> employees;

        try {
            employees = db.getAllEmployees();
            if (employees.isEmpty()) {
                throw new TestFailureException(
                    "test_getAllEmployees",
                    "Employee List returned empty."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getAllEmployees",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void
    test_getEmployeeByIDReturnsEqualInformationToNewlyCreatedEmployee(Employee newlyCreatedEmployee) {
        try {
            Employee employee = db.getEmployeeByID(newlyCreatedEmployee.getIdNumber());
            if (!employee.equals(newlyCreatedEmployee)) {
                throw new TestFailureException(
                    "test_getEmployeeByIDReturnsEqualInformationToNewlyCreatedEmployee",
                    " Attempting to use ID to get an existing employee" +
                        " on a newly created employee did not" +
                        " return the same information.");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getEmployeeByIDReturnsEqualInformationToNewlyCreatedEmployee",
                "SQL Exception, see stacktrace above.");
        }

    }

    public static void
    test_getEmployeeByEmailReturnsEqualInformationToNewlyCreatedEmployee(Employee newlyCreatedEmployee) {
        try {
            Employee employee = db.getEmployeeByEmail(newlyCreatedEmployee.getEmail());
            if (!employee.equals(newlyCreatedEmployee)) {
                throw new TestFailureException(
                    "test_getEmployeeByEmailReturnsEqualInformationToNewlyCreatedEmployee",
                    " Attempting to use Email to get an existing employee" +
                        " on a newly created employee did not" +
                        " return the same information."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getEmployeeByEmailReturnsEqualInformationToNewlyCreatedEmployee",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static Customer test_createAndWriteNewCustomer() {
        Customer customer = Customer.createNew(
            "customerLastName",
            "customerFirstName",
            "customer@email.com",
            "0123456789",
            "0123456789",
            testAddress,
            null
        );
        try {
            db.saveToDatabase(customer);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_createAndWriteNewCustomer",
                "SQL Exception, see stacktrace above.");
        }
        if (customer.getIdNumber() < 0) {
            throw new TestFailureException(
                "test_createAndWriteNewCustomer",
                "Writing new customer did not update ID number."
            );
        }
        return customer;
    }

    public static void
    test_getCustomerByIDReturnsEqualInformationToNewlyCreatedCustomer(Customer newlyCreatedCustomer) {
        try {
            Customer customer = db.getCustomerByID(newlyCreatedCustomer.getIdNumber());
            if (!customer.equals(newlyCreatedCustomer)) {
                throw new TestFailureException(
                    "test_getCustomerByIDReturnsEqualInformationToNewlyCreatedCustomer",
                    " Attempting to use ID to get an existing Customer" +
                    " on a newly created customer did not" +
                    " return the same information."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getCustomerByIDReturnsEqualInformationToNewlyCreatedCustomer",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void
    test_getCustomerByEmailReturnsEqualInformationToNewlyCreatedCustomer(Customer newlyCreatedCustomer) {
        try {
            Customer customer = db.getCustomerByEmail(newlyCreatedCustomer.getEmail());
            if (!customer.equals(newlyCreatedCustomer)) {
                throw new TestFailureException(
                    "test_getCustomerByEmailReturnsEqualInformationToNewlyCreatedCustomer",
                    " Attempting to use email to get an existing Customer" +
                        " on a newly created customer did not" +
                        " return the same information."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getCustomerByEmailReturnsEqualInformationToNewlyCreatedCustomer",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static Supplier test_createAndWriteNewSupplier() {
        Supplier supplier = Supplier.createNew("Test Supplier", "0123456789", testAddress);
        try {
            db.saveToDatabase(supplier);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_createAndWriteNewSupplier",
                "SQL Exception, see stacktrace above.");
        }
        if (supplier.getBusinessID() < 0) {
            throw new TestFailureException(
                "test_createAndWriteNewSupplier",
                "Writing new Supplier did not update ID number."
            );
        }
        return supplier;
    }

    public static void
    test_retrieveSupplierByIDReturnsEqualInformationToNewlyCreatedSupplier(Supplier newlyCreatedSupplier) {
        try {
            Supplier supplier = db.retrieveSupplierByID(newlyCreatedSupplier.getBusinessID());
            if (!supplier.equals(newlyCreatedSupplier)) {//fixme test failure point likely here
                throw new TestFailureException(
                    "test_retrieveSupplierByIDReturnsEqualInformationToNewlyCreatedSupplier",
                    "Attempting to use ID to get existing Supplier on a" +
                        " newly created Supplier did not" +
                        " return the same information."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_retrieveSupplierByIDReturnsEqualInformationToNewlyCreatedSupplier",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static ShippingProvider test_createAndWriteNewShippingProvider() {
        ShippingProvider shippingProvider =
            ShippingProvider.createNew("Shipping Provider", "0123456789", testAddress);
        try {
            db.saveToDatabase(shippingProvider);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_createAndWriteNewShippingProvider",
                "SQL Exception, see stacktrace above.");
        }
        if (shippingProvider.getBusinessID() < 0) {
            throw new TestFailureException(
                "test_createAndWriteNewShippingProvider",
                "Writing new ShippingProvider did not update ID number."
            );
        }
        return shippingProvider;
    }

    public static void
    test_retrieveShippingProviderByIDReturnsEqualInformationToNewlyCreatedShippingProvider
        (ShippingProvider newlyCreatedShippingProvider) {
        try {
            ShippingProvider shippingProvider =
                db.retrieveShippingProviderByID(newlyCreatedShippingProvider.getBusinessID());
            if (!shippingProvider.equals(newlyCreatedShippingProvider)) {//fixme test failure point likely here
                throw new TestFailureException(
                    "test_retrieveShippingProviderByIDReturnsEqualInformationToNewlyCreatedShippingProvider",
                    "Attempting to use ID to get existing ShippingProvider on a" +
                        " newly created ShippingProvider did not" +
                        " return the same information."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_retrieveShippingProviderByIDReturnsEqualInformationToNewlyCreatedShippingProvider",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_retrieveCarMakeByIDReturnsKnownValue() {
        try {
            String makeName = db.retrieveCarMakeByID(6);
            if (!makeName.equals("Chevrolet")) {
                throw new TestFailureException(
                    "test_retrieveCarMakeByIDReturnsKnownValue",
                    "Expected: Chevrolet\n" +
                    "Received: " + makeName
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_retrieveCarMakeByIDReturnsKnownValue",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_retrieveCarMakeIDByNameReturnsKnownValue() {
        try {
            Integer makeId = db.retrieveCarMakeIDByName("Chevrolet");
            if (!makeId.equals(6)) {
                throw new TestFailureException(
                    "test_retrieveCarMakeIDByNameReturnsKnownValue",
                    "Expected: 6\n" +
                    "Received: " + String.valueOf(makeId)
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_retrieveCarMakeIDByNameReturnsKnownValue",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_getAllCarMakesReturnsCorrectNumberOfMakes() {
        try {
            String sql = "SELECT COUNT(MakeName)" +
                             " FROM AutoPartsStore.CarMakes;";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int actual = rs.getInt(1);
            List<String> makes = db.getAllCarMakes();

            if (!(makes.size() == actual)) {
                throw new TestFailureException(
                    "test_getAllCarMakesReturnsCorrectNumberOfMakes",
                    "Expected: " + actual + "\n" +
                    "Received: " + makes.size()
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getAllCarMakesReturnsCorrectNumberOfMakes",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_getAllCarModelsReturnsCorrectNumberOfModels() {
        try {
            String sql = "SELECT COUNT(CarModelName)" +
                             " FROM AutoPartsStore.CarModels" +
                             " WHERE CarMakeID = 6;";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int actual = rs.getInt(1);
            List<String> models = db.getAllCarModels("Chevrolet");

            if (!(models.size() == actual)) {
                throw new TestFailureException(
                    "test_getAllCarModelsReturnsCorrectNumberOfModels",
                    "Expected: " + actual + "\n" +
                    "Received: " + models.size()
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getAllCarModelsReturnsCorrectNumberOfModels",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_getAllProductionYearsReturnsCorrectNumberOfYearValues() {
        try {
            String sql = "SELECT COUNT(ProductionYear)" +
                             " FROM AutoPartsStore.CarConfigurations" +
                             " WHERE CarModelID = 79;";
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int actual = rs.getInt(1);

            List<Integer> years = db.getAllProductionYears("Sonic");

            if (!(years.size() == actual)) {
                throw new TestFailureException(
                    "test_getAllProductionYearsReturnsCorrectNumberOfYearValues",
                    "Expected: " + actual + "\n" +
                    "Received: " + years.size()
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getAllProductionYearsReturnsCorrectNumberOfYearValues",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_retrieveCarModelByIDReturnsKnownValue() {
        try {
            String model = db.retrieveCarModelByID(79);
            if (!model.equals("Sonic")) {
                throw new TestFailureException(
                    "test_retrieveCarModelByIDReturnsKnownValue",
                    "Expected: Sonic\n" +
                    "Received: " + model
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_retrieveCarModelByIDReturnsKnownValue",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_retrieveCarModelIDByNameReturnsKnownValue() {
        try {
            Integer modelId = db.retrieveCarModelIDByName("Sonic");
            if (!modelId.equals(79)) {
                throw new TestFailureException(
                    "test_retrieveCarModelIDByNameReturnsKnownValue",
                    "Expected: 79\n" +
                    "Received: " + String.valueOf(modelId)
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_retrieveCarModelIDByNameReturnsKnownValue",
                "SQL Exception, see stacktrace above.");
        }

    }

    public static Car test_retrieveCarConfigByIDReturnsKnownValue() {
        try {
            Car car = db.retrieveCarConfigByID(300);
            if (!car.getMake().equals("Chevrolet")) {
                throw new TestFailureException(
                    "test_retrieveCarConfigByIDReturnsKnownValue",
                    "Make does not match."
                );
            }
            if (!car.getModel().equals("Sonic")) {
                throw new TestFailureException(
                    "test_retrieveCarConfigByIDReturnsKnownValue",
                    "Model does not match."
                );
            }
            if (!car.getProductionYear().equals(2015)) {
                throw new TestFailureException(
                    "test_retrieveCarConfigByIDReturnsKnownValue",
                    "Year does not match."
                );
            }
            return car;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_retrieveCarConfigByIDReturnsKnownValue",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_retrieveCarConfigByFieldsReturnsKnownValue() {
        try {
            Car carByField = db.retrieveCarConfigByFields("Chevrolet", "Sonic", 2015);
            Car carById = db.retrieveCarConfigByID(300);
            if (!carByField.equals(carById)) {//fixme test failure point here
                throw new TestFailureException(
                    "test_retrieveCarConfigByFieldsReturnsKnownValue",
                    "Retrieving car by fields did not return the same" +
                        " car as the ID that contains these fields.\n" +
                    "Expected: " + carById.getMake() + " " + carById.getModel() +
                        " " + carById.getProductionYear() + "\n" +
                    "Received: " + carByField.getMake() + " " + carByField.getModel() +
                        " " + carByField.getProductionYear() + "\n"
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_retrieveCarConfigByFieldsReturnsKnownValue",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_writeTenPartsToDatabase(Supplier supplier, Car car) {
        try {

            for (int i = 0; i < 10; i++) {
                Part part = writeAndSaveDummyPart("PARTNAME " + i);
                // test retrieval gets same thing as what was written
                Part getPart = db.retrievePartByID(part.getPartID());
                if (part.getPartID() < 0) {
                    throw new TestFailureException(
                        "test_writeTenPartsToDatabase",
                        "Writing new Part to database did not update Part ID"
                    );
                }
                if (!part.equals(getPart)) {
                    throw new TestFailureException(
                        "test_writeTenPartsToDatabase",
                        "Retrieving newly written Part from DB by ID did not" +
                            " return equal information."
                    );
                }
            }
        }

        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_writeTenPartsToDatabase",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_getAllPartsReturns20PartsOnFreshDatabase() {
        try {
            List<Part> parts = db.getAllParts();
            if (parts.size() != 20) {
                throw new TestFailureException(
                    "test_getAllPartsReturnsTenPartsOnFreshDatabase",
                    "Expected 10 parts from the database.\n" +
                    "Got: " + parts.size()
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getAllPartsReturnsTenPartsOnFreshDatabase",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_getCustomerOrderByIDReturnsCorrectInformation() {
        try {
            Customer customer = db.getCustomerByID(1);
            Part part = writeAndSaveDummyPart("CustomerOrderTest");
            ShippingProvider shippingProvider = db.retrieveShippingProviderByID(1);
            Map<Part, Integer> parts = new HashMap<>();
            parts.put(part, 20);
            BigDecimal shippingFee = new BigDecimal(19.99);
            shippingFee = shippingFee.setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxAmount = new BigDecimal(4.88);
            taxAmount = taxAmount.setScale(2, RoundingMode.HALF_UP);

            Order order = CustomerOrder.createNew(
                customer,
                parts,
                shippingProvider,
                shippingFee,
                taxAmount,
                "Test Notes"
                );
            db.saveToDatabase(order);
            if (order.getOrderID() < 0) {
                throw new TestFailureException(
                    "test_getCustomerOrderByIDReturnsCorrectInformation",
                    "Order ID was not updated when saving to database."
                );
            }
            Order getOrder = db.getCustomerOrderByID(order.getOrderID());
            System.out.println("Compare orders manually");
            System.out.println(order.toString());
            System.out.println();
            System.out.println(getOrder.toString());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getCustomerOrderByIDReturnsCorrectInformation",
                "SQL Exception, see stacktrace above.");
        }
    }

    // make sure to run after the above test
    public static void test_collectAllOrdersReturnsNotEmptyListForCustomerWithOrders() {
        try {
            Customer customer = db.getCustomerByID(1);
            db.collectAllOrders(customer);
            if (customer.getOrderList().size() != 1) {
                throw new TestFailureException(
                    "test_collectAllOrdersReturnsNotEmptyListForCustomerWithOrders",
                    "Get all orders for a customer with orders failed.\n" +
                    "Expected list size: 1\n" +
                    "Received: " + customer.getOrderList().size() + "\n"
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_collectAllOrdersReturnsNotEmptyListForCustomerWithOrders",
                "SQL Exception, see stacktrace above.");
        }
    }

    public static void test_collectAllOrdersReturnsEmptyListForCustomerWithoutOrders() {
        try {
            Customer customer = db.getCustomerByID(20);
            db.collectAllOrders(customer);
            if (!customer.getOrderList().isEmpty()) {
                throw new TestFailureException(
                    "test_collectAllOrdersReturnsEmptyListForCustomerWithoutOrders",
                    "Customer order list is not empty."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_collectAllOrdersReturnsEmptyListForCustomerWithoutOrders",
                "SQL Exception, see stacktrace above."
            );
        }
    }

    public static void test_getResupplyOrderByIDReturnsCorrectInformation() {
        try {
            Employee initEmp = db.getEmployeeByID(1);
            String sDate1 = "2012/12/31";
            Date creationDate = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            sDate1 = "2013/01/05";
            Date approvalDate = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            Employee approvingManager = db.getEmployeeByID(2);
            sDate1 = "2014/02/07";
            Date shipDate = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            BigDecimal shippingFee = new BigDecimal(6.50);
            shippingFee = shippingFee.setScale(2, RoundingMode.HALF_UP);
            Part part = writeAndSaveDummyPart("ResupplyOrderTest");
            Map<Part, Integer> parts = new HashMap<>();
            parts.put(part, 20);
            BigDecimal taxAmount = new BigDecimal(4.50);
            taxAmount = taxAmount.setScale(2, RoundingMode.HALF_UP);
            Order.Status status = Order.Status.APPROVED;
            String notes = "TEST RESUPPLY ORDER";

            ResupplyOrder order = ResupplyOrder.createNew(initEmp, shippingFee, parts, taxAmount, notes);
            db.saveToDatabase(order);
            if (order.getOrderID() < 0) {
                throw new TestFailureException(
                    "test_getResupplyOrderByIDReturnsCorrectInformation",
                    "Order ID was not updated when saving to database."
                );
            }
            ResupplyOrder getOrder = db.getResupplyOrderByID(order.getOrderID());
            System.out.println("Compare orders manually");
            System.out.println(order.toString());
            System.out.println();
            System.out.println(getOrder.toString());

            // overwrite the data and save, then get it again to see if all fields are the same still
            order = ResupplyOrder.createExisting(order.getOrderID(), initEmp,
                creationDate, approvalDate, approvingManager, shipDate,
                shippingFee, parts, taxAmount, status, notes);
            db.saveToDatabase(order);

            getOrder = db.getResupplyOrderByID(order.getOrderID());
            System.out.println("Overwritten order with full details, check values");
            System.out.println("Compare orders manually");
            System.out.println(order.toString());
            System.out.println();
            System.out.println(getOrder.toString());
        }
        catch (ParseException ex) {
            System.err.println("DATE FORMAT PARSE ERROR");
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getResupplyOrderByIDReturnsCorrectInformation",
                "SQL Exception, see stacktrace above."
            );
        }
    }

    public static void test_collectAllOrdersReturnsNotEmptyListForEmployeeWithOrders() {
        try {
            Employee employee = db.getEmployeeByID(1);
            db.collectAllOrders(employee);
            if (employee.getOrderList().size() != 1) {
                throw new TestFailureException(
                    "test_collectAllOrdersReturnsNotEmptyListForEmployeeWithOrders",
                    "Get all orders for a customer with orders failed.\n" +
                        "Expected list size: 1\n" +
                        "Received: " + employee.getOrderList().size() + "\n"
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_collectAllOrdersReturnsNotEmptyListForEmployeeWithOrders",
                "SQL Exception, see stacktrace above.");
        }
    }


    public static void test_collectAllOrdersReturnsEmptyListForEmployeeWithoutOrders() {
        try {
            Employee employee = db.getEmployeeByID(20);
            db.collectAllOrders(employee);
            if (!employee.getOrderList().isEmpty()) {
                throw new TestFailureException(
                    "test_collectAllOrdersReturnsEmptyListForEmployeeWithoutOrders",
                    "Employee order list is not empty."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_collectAllOrdersReturnsEmptyListForEmployeeWithoutOrders",
                "SQL Exception, see stacktrace above."
            );
        }
    }

    public static void test_getAllActivePartsReturnsNonEmptyList() {
        try {
            List<Part> parts = db.getAllActiveParts();
            if (parts.isEmpty()) {
                throw new TestFailureException(
                    "test_getAllActivePartsReturnsNonEmptyList",
                    "Part list for active parts was returned empty."
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getAllActivePartsReturnsNonEmptyList",
                "SQL Exception when writing dummy part. See stacktrace above."
            );
        }
    }

    public static void test_getAllEmployeesReturnsListOf102() {
        try {
            List<Employee> employees = db.getAllEmployees();
            if (employees.size() != 102) {
                throw new TestFailureException(
                    "test_getAllEmployeesReturnsListOf102",
                    "Get all employees did not return expected number of employees.\n"+
                        "Expected: 102\n"+
                        "Received: " + employees.size()
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "test_getAllEmployeesReturnsListOf101",
                "SQL Exception when writing dummy part. See stacktrace above."
            );
        }
    }


    private static Part writeAndSaveDummyPart(String name) {
        try {
            Supplier supplier = db.retrieveSupplierByID(1);
            Car car = db.retrieveCarConfigByID(300);
            BigDecimal pricePerUnit = new BigDecimal(5);
            pricePerUnit = pricePerUnit.setScale(2, RoundingMode.HALF_UP);
            String imgLoc = "src\\main\\resources\\imgs\\placeholder.png";
            byte[] img = Files.readAllBytes(Paths.get(imgLoc));
            Part part = Part.createNew(
                supplier,
                car,
                name,
                "DESCRIPTION",
                "CATEGORY",
                pricePerUnit,
                "1",
                1000,
                img);
            db.saveToDatabase(part);
            return part;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "writeAndSaveDummyPart",
                "IO Exception when getting image, see stacktrace above."
            );
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new TestFailureException(
                "writeAndSaveDummyPart",
                "SQL Exception when writing dummy part. See stacktrace above."
            );
        }
    }
}

class TestFailureException extends RuntimeException {
    TestFailureException(String testName, String details) {
        super("TEST FAILURE: " + testName + "\n" + details);
    }
}