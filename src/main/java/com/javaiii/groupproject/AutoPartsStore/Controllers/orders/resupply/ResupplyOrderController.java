package com.javaiii.groupproject.AutoPartsStore.Controllers.orders.resupply;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.orders.ResupplyOrder;
import com.javaiii.groupproject.AutoPartsStore.Models.people.Employee;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import com.javaiii.groupproject.AutoPartsStore.command.EmployeeCommand;
import com.javaiii.groupproject.AutoPartsStore.command.PartCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResupplyOrderController {

    DatabaseManager db;

    private Employee orderingEmployee;
    private List<Part> activeParts;
    private Map<Part, Integer> partOrderMap;
    private Map<Part, Integer> orderedItems;

    private String orderNotes;

    private final BigDecimal SALES_TAX_RATE = new BigDecimal(0.07);
    private final BigDecimal FLAT_SHIPPING_FEE = new BigDecimal(10.00);

    /**
     * Default constructor.
     * Connects to the database and initializes the lists and maps we are going
     * to use for orders.
     */
    public ResupplyOrderController() {
        connect();
        init();
    }

    /**Initializes a connection to the database*/
    private void connect() {
        db = new DatabaseManager(true);
    }

    /**Reset all member variables when we come to a Resupply*/
    private void init() {
        activeParts = new ArrayList<>();
        partOrderMap = new HashMap<>();
        orderNotes = "";
    }

    /**For setting the Command object we will use for resupply orders*/
    @RequestMapping("/orders/resupply/startResupplyOrder")
    public String startResupplyOrder(Model model) {
        connect();
        init();
        model.addAttribute("employeeCommand", new EmployeeCommand());
        return "orders/resupply/startResupplyOrder";
    }

    @ModelAttribute("getListOfActiveParts")
    public List<Part> getListOfActiveParts() {
        buildPartList();
        return activeParts;
    }

    /**For handling user selection of Supplier*/
    @PostMapping("/orders/resupply/startResupplyOrder")
    public String supplierPost(@ModelAttribute("employeeCommand") EmployeeCommand employeeCommand,
                               BindingResult bindingResult, // MUST follow command
                               Model model,
                               RedirectAttributes redirectAttributes) {
        System.out.println("Employee ID entered.");
        if (bindingResult.hasErrors()) {
            System.out.println("Binding Result has errors");
            return "orders/resupply/startResupplyOrder";
        }

        Integer employeeID = employeeCommand.getId();

        try {
            orderingEmployee = db.getEmployeeByID(employeeID);
        }
        catch (SQLException ex) {
            System.err.println("INVALID EMPLOYEE ID");
            ex.printStackTrace();
        }

        model.addAttribute("activeParts", activeParts);
        redirectAttributes.addFlashAttribute("employeeCommand", employeeCommand);
        redirectAttributes.addFlashAttribute("activeParts", activeParts);
        redirectAttributes.addFlashAttribute("partCommand", new PartCommand());

        if (partOrderMap.isEmpty()) {
            for (Part part : activeParts) {
                partOrderMap.put(part, 0);
            }
        }

        redirectAttributes.addFlashAttribute("partOrderMap", partOrderMap);
        return "redirect:/orders/resupply/selectParts";
    }

    /**Processes adding a part to the order*/
    @PostMapping("/orders/resupply/selectParts")
    public String selectPartsPost(@ModelAttribute("employeeCommand") EmployeeCommand employeeCommand,
                                  @ModelAttribute("partCommand") PartCommand partCommand,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        System.out.println("Part addition submission");
        if (bindingResult.hasErrors()) {
            System.out.println("Binding Result has errors");
        }
        Integer partID = partCommand.getId();
        Integer quantity = partCommand.getQuantity();
        System.out.println("PartID: " + partID + " quantity: " + quantity);

        if (quantity == null) {
            quantity = 0;
        }

        for (Part part : partOrderMap.keySet()) {
            if (part.getPartID().equals(partID)) {
                partOrderMap.put(part, quantity);
            }
        }

        updateOrderedItems();
        redirectAttributes.addFlashAttribute("employeeCommand", employeeCommand);
        redirectAttributes.addFlashAttribute("partCommand", new PartCommand());
        model.addAttribute("activeParts", activeParts);
        return "orders/resupply/selectParts";
    }

    @RequestMapping(value="/orders/resupply/handleCart", method=RequestMethod.POST, params="action=confirmOrder")
    public ModelAndView checkout() {
        System.out.println("CONFIRM ORDER");
        ModelAndView modelAndView = new ModelAndView("/orders/resupply/confirmOrder.html");
        return modelAndView;
    }

    @RequestMapping(value="/orders/resupply/handleCart", method=RequestMethod.POST, params="action=cancelOrder")
    public String emptyCart() {
        System.out.println("CANCEL ORDER");
        for (Part key : partOrderMap.keySet()) {
            partOrderMap.put(key, 0);
        }
        return "index";
    }

    @RequestMapping(value="/orders/resupply/placeOrder", method=RequestMethod.POST, params="action=placeOrder")
    public String placeOrder() {
        System.out.println("PLACE ORDER");

        try {
            ResupplyOrder resupplyOrder = ResupplyOrder.createNew(
                orderingEmployee,
                getShippingFee(),
                getOrderedItems(),
                getOrderTaxAmount(),
                null//fixme
            );
            db.saveToDatabase(resupplyOrder);
        }
        catch (SQLException ex) {
            return "../../errors/databaseWriteError";
        }

        return "orders/orderPlaced";
    }

    @RequestMapping(value="/orders/resupply/placeOrder", method=RequestMethod.POST, params="action=cancelOrder")
    public String cancelOrder() {
        System.out.println("STOP ORDER");
        return emptyCart();
    }

    @RequestMapping("orders/resupply/selectParts")
    public String selectParts(Model model) {
        return "orders/resupply/selectParts";
    }

    @ModelAttribute("getOrderNotes")
    public String getOrderNotes() {
        return orderNotes;
    }

    @ModelAttribute("partOrderMap")
    public Map<Part, Integer> getPartOrderMap() {
        return partOrderMap;
    }

    @ModelAttribute("getOrderedItems")
    public Map<Part, Integer> getOrderedItems() {
        updateOrderedItems();
        return orderedItems;
    }

    @ModelAttribute("orderHasItems")
    public boolean orderHasItems() {
        for (Integer quant: partOrderMap.values()) {
            if (quant.compareTo(0) > 0) {
                return true;
            }
        }
        return false;
    }

    @ModelAttribute("getOrderSubtotalCost")
    public String getOrderSubtotalCostString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getOrderSubtotalCost());
    }

    private BigDecimal getOrderSubtotalCost() {
        BigDecimal total = new BigDecimal(0);
        for (Map.Entry<Part, Integer> entry : orderedItems.entrySet()) {
            BigDecimal unitCost = entry.getKey().getPricePerUnit();
            Integer quantity = entry.getValue();
            BigDecimal unitPrice = unitCost.multiply(new BigDecimal(quantity));
            total = total.add(unitPrice);
        }
        return total;
    }

    @ModelAttribute("getOrderTaxAmount")
    public String getOrderTaxAmountString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getOrderTaxAmount());
    }

    public BigDecimal getOrderTaxAmount() {
        return SALES_TAX_RATE.multiply(getOrderSubtotalCost());
    }

    @ModelAttribute("getShippingFee")
    public String getShippingFeeString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getShippingFee());
    }

    public BigDecimal getShippingFee() {
        return getFLAT_SHIPPING_FEE();
    }

    @ModelAttribute("getOrderTotalCost")
    public String getOrderTotalCostString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getTotalOrderCost());
    }

    public BigDecimal getTotalOrderCost() {
        return getOrderSubtotalCost().add(getOrderTaxAmount().add(getShippingFee()));
    }

    /**Updates the list of ordered items with the quantity*/
    private void updateOrderedItems() {
        orderedItems = new HashMap<>();
        for (Map.Entry<Part, Integer> entry : partOrderMap.entrySet()) {
            Integer orderAmt = entry.getValue();
            if (orderAmt.compareTo(0) > 0) {
                orderedItems.put(entry.getKey(), orderAmt);
            }
        }
    }

    /**For building the list of currently not discontinued Parts*/
    private void buildPartList() {
        try {
            activeParts = db.getAllActiveParts();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
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

    public List<Part> getActiveParts() {
        return activeParts;
    }

    public void setActiveParts(List<Part> activeParts) {
        this.activeParts = activeParts;
    }

    public void setPartOrderMap(Map<Part, Integer> partOrderMap) {
        this.partOrderMap = partOrderMap;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

    public BigDecimal getSALES_TAX_RATE() {
        return SALES_TAX_RATE;
    }

    public BigDecimal getFLAT_SHIPPING_FEE() {
        return FLAT_SHIPPING_FEE;
    }
}
