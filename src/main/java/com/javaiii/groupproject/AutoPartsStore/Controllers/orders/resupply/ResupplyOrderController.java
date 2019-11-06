package com.javaiii.groupproject.AutoPartsStore.Controllers.orders.resupply;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.orders.ResupplyOrder;
import com.javaiii.groupproject.AutoPartsStore.Models.people.Employee;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import com.javaiii.groupproject.AutoPartsStore.command.ResupplyOrderCommand;
import com.javaiii.groupproject.AutoPartsStore.command.SupplierCommand;
import com.javaiii.groupproject.AutoPartsStore.exceptions.EmptyListException;
import com.javaiii.groupproject.AutoPartsStore.exceptions.PersonNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResupplyOrderController {

    DatabaseManager db;

    private List<Part> activeParts = new ArrayList<>();
    private List<String> availableSuppliers = new ArrayList<>();
    private List<Part> partsFilteredBySupplier = new ArrayList<>();

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
    private List<Part> partsSelectedByVendor = new ArrayList<>();
    private String errorMsg;
    private String orderEmployeeEmail;
    private String orderNotes;
    private String selectedVendor;

    /**Default constructor connects to the database only*/
    public ResupplyOrderController() {
        connect();
    }

    /**Initializes a connection to the database*/
    private void connect() {
        db = new DatabaseManager(true);
    }

    /**For setting the Command object we will use for resupply orders*/
    @RequestMapping("/orders/resupply/startResupplyOrder")
    public String startResupplyOrder(Model model) {
        model.addAttribute("command", new SupplierCommand());
        return "orders/resupply/startResupplyOrder";
    }

    /**For getting the list of available Vendors based on active Parts*/
    @ModelAttribute("getListOfSuppliers")
    public List<String> getListOfSuppliers() {
        buildSupplierList();
        return availableSuppliers;
    }

    @ModelAttribute("getListOfActiveParts")
    public List<Part> getListOfActiveParts() {
        buildPartList();
        return activeParts;
    }

    /**For handling user selection of Supplier*/
    @PostMapping("/orders/resupply/startResupplyOrder")
    public String supplierPost(@ModelAttribute("command") SupplierCommand command,
                               BindingResult bindingResult, // MUST follow command
                               Model model,
                               RedirectAttributes redirectAttributes) {
        System.out.println("Supplier Selection Submission");
        if (bindingResult.hasErrors()) {
            System.out.println("Binding Result has errors");
            return "orders/resupply/startResupplyOrder";
        }
        // build the list of available parts based on the selected vendor
        String selectedSupplier = command.getSelectedSupplier();
        partsFilteredBySupplier = getPartsBySupplier(selectedSupplier);
        model.addAttribute("partsFilteredBySupplier", partsFilteredBySupplier);
        redirectAttributes.addFlashAttribute("command", command);
        redirectAttributes.addFlashAttribute("partsFilteredBySupplier", partsFilteredBySupplier);
        return "redirect:/orders/resupply/selectParts";
    }

//    @ModelAttribute("getPartsFilteredBySupplier")
//    public List<Part> getPartsFilteredBySupplier() {
//        return partsFilteredBySupplier;
//    }

    @RequestMapping("orders/resupply/selectParts")
    public String selectParts(Model model) {
        model.addAttribute("command", new ResupplyOrderCommand());
        return "orders/resupply/selectParts";
    }

    /**For building the list of currently not discontinued Parts*/
    private void buildPartList() {
        connect();
        try {
            activeParts = db.getAllActiveParts();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**For building the list of available vendors based on available Parts*/
    private void buildSupplierList() {
        buildPartList();
        if (activeParts.isEmpty()) {
            throw new EmptyListException(
                "Parts List Empty when attempting to retrieve list of Vendors."
            );
        }
        for (Part part : activeParts) {
            String vendorName = part.getSupplier().getCompanyName();
            if (!availableSuppliers.contains(vendorName)) {
                availableSuppliers.add(vendorName);
            }
        }
    }

    /**Creates a list of Parts filtered by Supplier*/
    private List<Part> getPartsBySupplier(String selectedSupplier) {
        List<Part> parts = new ArrayList<>();
        // add parts by supplier to the list
        for (Part part : activeParts) {
            String partSupplier = part.getSupplier().getCompanyName();
            if (partSupplier.equals(selectedSupplier)) {
                parts.add(part);
            }
        }
        return parts;
    }

    /**
     * loadParts method
     * used with resupplyvendor.xhtml as submit form process
     */
    public String loadParts() {

        for (Part part : activeParts) {
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

    public List<Part> getActiveParts() {
        return activeParts;
    }

    public void setActiveParts(List<Part> activeParts) {
        this.activeParts = activeParts;
    }

    public List<Part> getPartsSelectedByVendor() {
        return partsSelectedByVendor;
    }

    public void setPartsSelectedByVendor(List<Part> partsSelectedByVendor) {
        this.partsSelectedByVendor = partsSelectedByVendor;
    }

    public List<String> getAvailableSuppliers() {
        return availableSuppliers;
    }

    public void setAvailableSuppliers(List<String> availableSuppliers) {
        this.availableSuppliers = availableSuppliers;
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
