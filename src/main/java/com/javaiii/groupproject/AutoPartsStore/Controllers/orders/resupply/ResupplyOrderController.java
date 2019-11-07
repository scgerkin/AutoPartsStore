package com.javaiii.groupproject.AutoPartsStore.Controllers.orders.resupply;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import com.javaiii.groupproject.AutoPartsStore.command.CartCommand;
import com.javaiii.groupproject.AutoPartsStore.command.PartCommand;
import com.javaiii.groupproject.AutoPartsStore.command.SupplierCommand;
import com.javaiii.groupproject.AutoPartsStore.exceptions.EmptyListException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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

    private List<Part> activeParts;
    private List<String> availableSuppliers;
    private List<Part> partsFilteredBySupplier;
    private Map<Part, Integer> partOrderMap;
    private String orderNotes;

    // Constants for tax and shipping rates
    private final BigDecimal SALES_TAX_RATE = new BigDecimal(0.07);
    private final BigDecimal FLAT_SHIPPING_FEE = new BigDecimal(10.00);


    /**Default constructor connects to the database only*/
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
        availableSuppliers = new ArrayList<>();
        partsFilteredBySupplier = new ArrayList<>();
        partOrderMap = new HashMap<>();
        orderNotes = "";
    }

    /**For setting the Command object we will use for resupply orders*/
    @RequestMapping("/orders/resupply/startResupplyOrder")
    public String startResupplyOrder(Model model) {
        connect();
        init();
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
        redirectAttributes.addFlashAttribute("partCommand", new PartCommand());
        redirectAttributes.addFlashAttribute("cartCommand", new CartCommand());

        if (partOrderMap.isEmpty()) {
            for (Part part : partsFilteredBySupplier) {
                partOrderMap.put(part, 0);
            }
        }

        redirectAttributes.addFlashAttribute("partOrderMap", partOrderMap);
        return "redirect:/orders/resupply/selectParts";
    }

    @PostMapping("/orders/resupply/selectParts")
    public String selectPartsPost(@ModelAttribute("command") SupplierCommand command,
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


        redirectAttributes.addFlashAttribute("command", command);
        redirectAttributes.addFlashAttribute("partCommand", new PartCommand());
        model.addAttribute("partsFilteredBySupplier", partsFilteredBySupplier);


        return "orders/resupply/selectParts";
    }

    @RequestMapping(value="/orders/resupply/handleCart", method=RequestMethod.POST, params="action=checkout")
    public ModelAndView checkout() {
        System.out.println("CHECKOUT");
        ModelAndView modelAndView = new ModelAndView("/orders/resupply/checkout.html");
        return modelAndView;
    }

    @RequestMapping(value="/orders/resupply/handleCart", method=RequestMethod.POST, params="action=emptyCart")
    public String emptyCart(Model model) {
        System.out.println("EMPTY CART");
        for (Part key : partOrderMap.keySet()) {
            partOrderMap.put(key, 0);
        }
        return startResupplyOrder(model);
    }

    @RequestMapping("orders/resupply/selectParts")
    public String selectParts(Model model) {
        return "orders/resupply/selectParts";
    }

    @RequestMapping("orders/resupply/orderCart")
    public String orderCart(Model model) {
        model.addAttribute("cartCommand", new CartCommand());
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
        Map<Part, Integer> orderedItems = new HashMap<>();
        for (Map.Entry<Part, Integer> entry : partOrderMap.entrySet()) {
            Integer orderAmt = entry.getValue();
            if (orderAmt.compareTo(0) > 0) {
                orderedItems.put(entry.getKey(), orderAmt);
            }
        }
        return orderedItems;
    }

    @RequestMapping("orders/resupply/getNumOrdered")
    public Integer getNumOrdered(Integer partID) {
        System.out.println("getNumOrdered called:");
        System.out.println("PartID: " + partID);
        return 5;
    }

    @ModelAttribute("cartHasItems")
    public boolean cartHasItems() {
        for (Integer quant: partOrderMap.values()) {
            if (quant.compareTo(0) > 0) {
                return true;
            }
        }
        return false;
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

    public List<Part> getActiveParts() {
        return activeParts;
    }

    public void setActiveParts(List<Part> activeParts) {
        this.activeParts = activeParts;
    }

    public List<String> getAvailableSuppliers() {
        return availableSuppliers;
    }

    public void setAvailableSuppliers(List<String> availableSuppliers) {
        this.availableSuppliers = availableSuppliers;
    }

    public List<Part> getPartsFilteredBySupplier() {
        return partsFilteredBySupplier;
    }

    public void setPartsFilteredBySupplier(List<Part> partsFilteredBySupplier) {
        this.partsFilteredBySupplier = partsFilteredBySupplier;
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
