package com.javaiii.groupproject.AutoPartsStore.Controllers.orders.resupply;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.orders.ResupplyOrder;
import com.javaiii.groupproject.AutoPartsStore.Models.people.Employee;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import com.javaiii.groupproject.AutoPartsStore.command.SelectVendorCommand;
import com.javaiii.groupproject.AutoPartsStore.exceptions.PersonNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResupplyOrderController {

    DatabaseManager db;

    // Constants for tax and shipping rates
    private final BigDecimal SALES_TAX_RATE = new BigDecimal(0.07);
    private final BigDecimal FLAT_SHIPPING_FEE = new BigDecimal(10.00);

    // Variables
    private Map<Part, Integer> listedParts = new HashMap<>();
    private Map<Part, Integer> orderedParts = new HashMap<>();
    private BigDecimal orderAmount;
    private BigDecimal orderTotal;
    private BigDecimal taxAmount;
    private Employee employee1; // only needed when not using database
    private Employee orderEmployee;
    private ResupplyOrder resupplyOrder;
    private String link;

    // Property variables
    private List<Part> parts = new ArrayList<>();
    private List<Part> partsSelectedByVendor = new ArrayList<>();
    private List<String> vendorNames = new ArrayList<>();
    private String errorMsg;
    private String orderEmployeeEmail;
    private String orderNotes;
    private String selectedVendor;

    @RequestMapping("/orders/resupply/startResupplyOrder")
    public String resupplyOrder(Model model) {
        model.addAttribute("command", new SelectVendorCommand());
        return "orders/resupply/startResupplyOrder";
    }

    @ModelAttribute("getListOfVendors")
    public List<String> getListOfVendors() {
        init();
        return vendorNames;
    }

    public ResupplyOrderController() {
        init();
    }

    private void init() {
        errorMsg = "";
        orderNotes = "";

        try {
            connect();
            parts = db.getAllActiveParts();
        }
        catch (SQLException ex){
            errorMsg = getExceptionMsg(ex);
        }

        for (Part value : parts) {
            if (!vendorNames.contains(value.getSupplier().getCompanyName()))
                vendorNames.add(value.getSupplier().getCompanyName());
        }

        for (Part part : parts) {
            if (!vendorNames.contains(part.getSupplier().getCompanyName()) &&
                    !part.getSupplier().getCompanyName().equals("Auto Parts Store"))
                vendorNames.add(part.getSupplier().getCompanyName());
        }
    }

    private void connect() {
        db = new DatabaseManager(true);
    }

    /**
     * loadParts method
     * used with resupplyvendor.xhtml as submit form process
     */
    public String loadParts() {

        for (Part part : parts) {
            if (part.getSupplier().getCompanyName().equals(selectedVendor)) {
                partsSelectedByVendor.add(part);
                listedParts.put(part, 0);
            }
        }
        link = "resupplyorder";
        return link;
    }

    /**
     * backToMain method
     * used with resupplyvendor.xhtml
     */
    public String backToMain() {
        selectedVendor = "";
        link = "index";
        return link;
    }

    /**
     * getQuantityInCart method
     * used with resupplyorder.xhtml
     */
    public int getQuantityInCart(Part part) {
        return listedParts.get(part);
    }

    /**
     * addItemToCart method
     * used with resupplyorder.xhtml
     */
    public String addItemToCart(Part part) {
        int qty = listedParts.get(part);
        qty++;
        listedParts.replace(part, qty);
        link = "resupplyorder";
        return link;
    }

    /**
     * createResupplyOrder method
     * used with resupplyorder.xhtml
     */
    public void createResupplyOrder() {
        orderAmount = new BigDecimal(0);
        for (Map.Entry<Part, Integer> items : orderedParts.entrySet()) {
            Part key = items.getKey();
            Integer value = items.getValue();
            orderAmount = orderAmount.add(key.getPricePerUnit().multiply(new BigDecimal(value)));
        }

        taxAmount = orderAmount.multiply(SALES_TAX_RATE);
        orderTotal = orderAmount.add(taxAmount.add(FLAT_SHIPPING_FEE));
        resupplyOrder =  ResupplyOrder.createNew(orderEmployee, FLAT_SHIPPING_FEE, orderedParts, taxAmount,
            orderNotes);
    }

    /**
     * processCart method
     * used with resupplyorder.xhtml as submit form process
     */
    public String processCart() {
        link = "";
        errorMsg = "";

        for (Map.Entry<Part, Integer> items : listedParts.entrySet()) {
            Part key = items.getKey();
            Integer value = items.getValue();
            if (value > 0) { orderedParts.put(key, value); }
        }

        if (orderedParts.isEmpty()) {
            errorMsg = "You do not not have any parts in your cart. Please add parts to your order before proceeding.";
            link = "resupplyorder";
            return link;
        } else if (orderEmployeeEmail == null || orderEmployeeEmail.equals("")) {
            errorMsg = "You must enter your employee email address in order to proceed.";
            link = "resupplyorder";
            return link;
        } else {
            try {
                connect();
                orderEmployee = db.getEmployeeByEmail(orderEmployeeEmail);
                this.createResupplyOrder();
                link = "placeorder";
                return link;
            } catch (PersonNotFoundException ex) {
                errorMsg = getExceptionMsg(ex);
                orderEmployeeEmail = "";
                link = "resupplyorder";
                return link;
            } catch (SQLException ex){
                errorMsg = "Database connection error. Could not verify employee credentials.\n" + getExceptionMsg(ex);
                link = "resupplyorder";
                return link;
            }
        }
    }


    /**
     * resetCart method
     * used with resupplyorder.xhtml as reset form process
     * used with placeorder.xhtml as part of cancelOrder method
     */
    public void resetCart() {
        for (int i = 0; i < partsSelectedByVendor.size(); i++) {
            listedParts.put(partsSelectedByVendor.get(i), 0);
        }

        orderNotes = "";
        orderEmployeeEmail = "";
        errorMsg = "";
    }

    /**
     * placeOrder method
     * used with placeorder.xhtml as submit form process
     */
    public String placeOrder() {
        try {
            connect();
            db.saveToDatabase(resupplyOrder);
            link = "orderconfirmation";
            return link;
        } catch (SQLException ex) {
            errorMsg = "There was an error placing your order.\n + ex.getMessage()";
            link = "placeorder";
            return link;
        }
    }

    /**
     * cancelOrder method
     * used with placeorder.xhtml
     */
    public String cancelOrder() {
        resupplyOrder = null;
        orderedParts.clear();
        this.resetCart();
        link = "resupplyorder";
        return link;
    }

    private String getExceptionMsg(Exception ex) {
        // if running locally, print to console
        ex.printStackTrace();

        // else, update status with stack trace information and display on page
        String msg = "EXCEPTION: ";
        msg += ex.getClass().getSimpleName();
        msg += "\n";
        StackTraceElement[] elements = ex.getStackTrace();
        for (StackTraceElement element : elements) {
            msg += element.toString();
            msg += "\n";
        }
        return msg;
    }

    public BigDecimal getSALES_TAX_RATE() {
        return SALES_TAX_RATE;
    }

    public BigDecimal getFLAT_SHIPPING_FEE() {
        return FLAT_SHIPPING_FEE;
    }

    public Map<Part, Integer> getListedParts() {
        return listedParts;
    }

    public void setListedParts(Map<Part, Integer> listedParts) {
        this.listedParts = listedParts;
    }

    public Map<Part, Integer> getOrderedParts() {
        return orderedParts;
    }

    public void setOrderedParts(Map<Part, Integer> orderedParts) {
        this.orderedParts = orderedParts;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Employee getEmployee1() {
        return employee1;
    }

    public void setEmployee1(Employee employee1) {
        this.employee1 = employee1;
    }

    public Employee getOrderEmployee() {
        return orderEmployee;
    }

    public void setOrderEmployee(Employee orderEmployee) {
        this.orderEmployee = orderEmployee;
    }

    public ResupplyOrder getResupplyOrder() {
        return resupplyOrder;
    }

    public void setResupplyOrder(ResupplyOrder resupplyOrder) {
        this.resupplyOrder = resupplyOrder;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public List<Part> getPartsSelectedByVendor() {
        return partsSelectedByVendor;
    }

    public void setPartsSelectedByVendor(List<Part> partsSelectedByVendor) {
        this.partsSelectedByVendor = partsSelectedByVendor;
    }

    public List<String> getVendorNames() {
        return vendorNames;
    }

    public void setVendorNames(List<String> vendorNames) {
        this.vendorNames = vendorNames;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getOrderEmployeeEmail() {
        return orderEmployeeEmail;
    }

    public void setOrderEmployeeEmail(String orderEmployeeEmail) {
        this.orderEmployeeEmail = orderEmployeeEmail;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

    public String getSelectedVendor() {
        return selectedVendor;
    }

    public void setSelectedVendor(String selectedVendor) {
        this.selectedVendor = selectedVendor;
    }
}
