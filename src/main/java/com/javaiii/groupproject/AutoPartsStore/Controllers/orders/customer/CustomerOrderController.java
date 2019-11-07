package com.javaiii.groupproject.AutoPartsStore.Controllers.orders.customer;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.address.ShippingAddress;
import com.javaiii.groupproject.AutoPartsStore.Models.business.ShippingProvider;
import com.javaiii.groupproject.AutoPartsStore.Models.orders.CustomerOrder;
import com.javaiii.groupproject.AutoPartsStore.Models.people.Customer;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import com.javaiii.groupproject.AutoPartsStore.command.IdCommand;
import com.javaiii.groupproject.AutoPartsStore.command.PartCommand;
import com.javaiii.groupproject.AutoPartsStore.command.ShippingAddressCommand;
import com.javaiii.groupproject.AutoPartsStore.exceptions.PersonNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a Controller class, allowing a Customer to place an order.
 * It acts as a go-between for the database repository and the view that the
 * user sees.
 * This controller only handles creating new orders and not manipulating existing
 * orders. That functionality should reside in another class once implementation
 * is necessary.
 */
@Controller
public class CustomerOrderController {

    DatabaseManager db;

    // the customer who is placing the order
    private Customer orderingCustomer;

    // for holding a list of all parts available for ordering
    // I am fairly certain this is redundant and could be removed later
    private List<Part> activeParts;

    // a map of all the possible parts we can order
    // it also stores the number we want to order
    // this allows for a somewhat dynamic order entry process
    private Map<Part, Integer> partOrderMap;

    // this map stores all the parts that we have a quantity of more than 0
    // and the quantity we want to order
    // might be redundant
    private Map<Part, Integer> orderedItems;

    // for basic functionality, these constants are used until we can implement
    // logic for having different tax rates, order prices, shipping fees, etc.
    private final BigDecimal SALES_TAX_RATE = new BigDecimal(0.07);
    private final BigDecimal FLAT_SHIPPING_FEE = new BigDecimal(10.00);
    private final BigDecimal FLAT_MARKUP_RATE = new BigDecimal(0.30);

    /**
     * The default constructor is called when the server is initiated.
     * This connects to the database immediately to ensure that the connection is
     * good.
     * This might not be necessary or could be abstracted out into another class
     * that basically fires up all of the individual controllers instead of each
     * controller instantiating a new DatabaseManager.
     * I'm not messing with it for right now, though.
     */
    public CustomerOrderController() {
        connect();
        init();
    }

    /**Creates a new DatabaseManager object to set the connection*/
    private void connect() {
        db = new DatabaseManager(true);
    }

    /**Initializes the list and map items we will use for an order*/
    private void init() {
        activeParts = new ArrayList<>();
        partOrderMap = new HashMap<>();
        orderedItems = new HashMap<>();
    }

    /**
     * This sets up the beginning of an order. It gives the View model a Command
     * data transfer object for transferring a customer ID between the view and
     * the controller before giving the user the view to enter their ID.
     */
    @RequestMapping("/orders/customer/startCustomerOrder")
    public String startCustomerOrder(Model model) {
        connect();
        init();
        model.addAttribute("customerCommand", new IdCommand());
        return "orders/customer/startCustomerOrder";
    }


    /**Handles entering a Customer ID number and redirects to part ordering page*/
    @PostMapping("/orders/customer/startCustomerOrder")
    public String customerIdPost(@ModelAttribute("customerCommand") IdCommand customerCommand,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        System.out.println("Customer ID entered.");
        if (bindingResult.hasErrors()) {
            System.err.println("Binding result has errors");
            return "orders/customer/startCustomerOrder";
        }

        // pull up the customer information
        Integer customerId = customerCommand.getId();
        try {
            orderingCustomer = db.getCustomerByID(customerId);
        }
        catch (PersonNotFoundException ex) {
            System.err.println("Caught Exception: " +ex.getClass().getSimpleName());
            return "errors/invalidIdEntry";
        }
        catch (SQLException ex) {
            System.err.println("Caught Exception: " +ex.getClass().getSimpleName());
            return "errors/databaseError";
        }

        model.addAttribute("activeParts", activeParts);//maybe not needed

        // give the redirect the attributes we'll be using
        redirectAttributes.addFlashAttribute("activeParts", activeParts);
        redirectAttributes.addFlashAttribute("customerCommand", customerCommand);
        redirectAttributes.addFlashAttribute("partCommand", new PartCommand());

        // build the map we will use for selecting parts, initializing all part
        // quantities to 0 for the beginning of the order
        // we don't want to do this every time a post is called, so make sure the
        // list is empty first
        if (partOrderMap.isEmpty()) {
            for (Part part : activeParts) {
                partOrderMap.put(part, 0);
            }
        }

        // add the part order map to the redirect
        redirectAttributes.addFlashAttribute("partOrderMap", partOrderMap);

        // redirect to page for selecting the parts we want
        // some redundancy specifically here with resupply orders
        return "redirect:/orders/customer/selectParts";
    }

    /**
     * This adds a part that the user selects to the shopping cart.
     * The commands being passed around are data transfer objects for interacting
     * with the view and the controller.
     */
    @PostMapping("/orders/customer/selectParts")
    public String selectPartsPost(@ModelAttribute("customerCommand") IdCommand customerCommand,
                                  @ModelAttribute("partCommand") PartCommand partCommand,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        System.out.println("Part addition submission");
        if (bindingResult.hasErrors()) {
            System.err.println("Binding Result has errors");
        }

        // get the details for the order: the ID of the part ordered and the quantity
        Integer partId = partCommand.getId();
        Integer quantity = partCommand.getQuantity();
        System.out.println("PartID: " + partId + " quantity: " + quantity);
        // Pretty sure this doesn't happen because all the fields are initialized
        // as 0 whenever the form is created, but better to be safe than sorry
        if (quantity == null) {
            quantity = 0;
        }

        // update the order quantity for the partOrderMap
        // as a side note, this originally used the database to get a part based
        // on the ID, and then just did put() with the part made from the database
        // This caused quite the headache where parts would get listed multiple
        // times on the site every time you added a part
        // This was caused because getting the part from the database would create
        // a new object each time and so putting it into the map it was a new key
        // instead of using the part that was already in the map
        for (Part part : partOrderMap.keySet()) {
            if (part.getPartID().equals(partId)) {
                partOrderMap.put(part, quantity);
            }
        }

        // fairly certain this is not needed
        redirectAttributes.addFlashAttribute("customerCommand", customerCommand);
        // good chance this is actually needed
        redirectAttributes.addFlashAttribute("partCommand", new PartCommand());
        // the model should already have the active parts
        model.addAttribute("activeParts", activeParts);

        // after updating the order, we want to stay on the part selection area
        return "orders/customer/selectParts";
    }

    /**
     * This method is called when a customer clicks on the button to confirm an
     * order after adding parts to the cart. It adds a Command object for holding
     * a ShippingAddress that will be set on the next page.
     */
    @RequestMapping(value="/orders/customer/handleCart", method= RequestMethod.POST, params="action=confirmOrder")
    public String confirmOrder(RedirectAttributes redirectAttributes) {
        System.out.println("CONFIRM ORDER");
        redirectAttributes.addFlashAttribute("shippingAddressCommand", new ShippingAddressCommand());
        return "orders/customer/confirmOrder";
    }

    /**
     * This method is called when a customer decides to cancel an order while
     * selecting parts to add to the cart. It sets all the quantities of the
     * order map to 0 and returns them to the index page.
     */
    @RequestMapping(value="/orders/customer/handleCart", method=RequestMethod.POST, params="action=cancelOrder")
    public String cancelOrder() {
        System.out.println("CANCEL ORDER");
        // this is probably not needed
        // if it actually IS needed, it can probably be simplified by simply
        // instantiating a new map or using .clear()
        // I don't want to mess with it at the moment
        for (Part key : partOrderMap.keySet()) {
            partOrderMap.put(key, 0);
        }
        return "index";
    }

    /**
     * This method is called after the customer enters a shipping address and
     * confirms placement of the order. The order is then created and written
     * to the database.
     */
    @RequestMapping(value="/orders/customer/placeOrder", method=RequestMethod.POST, params="action=placeOrder")
    public String placeOrder(@ModelAttribute("shippingAddressCommand") ShippingAddressCommand shippingAddressCommand) {
        System.out.println("PLACE ORDER");

        // get the shipping address entered by the customer if there was one
        ShippingAddress shippingAddress = null;
        if (shippingAddressCommand.isDifferentFromCustomer()) {
            shippingAddress = shippingAddressCommand.getShippingAddress();
            System.out.println("Shipping Address:\n" + shippingAddress.toString());
        }

        // write the order to the database
        try {
            CustomerOrder order = createCustomerOrder(shippingAddress);
            db.saveToDatabase(order);
        }
        catch (SQLException ex) {
            return "errors/databaseError";
        }

        // success
        return "orders/orderPlaced";
    }

    /**
     * Cancels an order while on the order confirmation page.
     * We can't have multiple request mappings for one method, so this calls
     * the main cancelOrder method.
     */
    @RequestMapping(value="/orders/customer/placeOrder", method=RequestMethod.POST, params="action=cancelOrder")
    public String cancelOrderFromConfirmPage() {
        return cancelOrder();
    }

    /**
     * Creates a CustomerOrder item based on whether or not the order is being
     * shipped to an address that is different from the customer or not.
     */
    private CustomerOrder createCustomerOrder(ShippingAddress shippingAddress) throws SQLException {
        ShippingProvider shippingProvider = db.retrieveShippingProviderByID(1);
        if (shippingAddress == null) {
            return CustomerOrder.createNew(orderingCustomer, getOrderedItems(), shippingProvider,
                getShippingFee(), getOrderTaxAmount(), null);
        }
        else {
            return CustomerOrder.createNew(orderingCustomer, shippingAddress, shippingProvider,
                getShippingFee(),getOrderedItems(),getOrderTaxAmount(),null);
        }
    }

    /**For building the list of currently not discontinued Parts*/
    private void buildPartList() {
        try {
            activeParts = db.getAllActiveParts();

            // remove parts that have no stock on hand
            // todo test this, not sure if it works as intended
            for (Part part : activeParts) {
                if (part.getQuantityOnHand().compareTo(0) <= 0) {
                    activeParts.remove(part);
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**Getter for Thymeleaf*/
    @ModelAttribute("getListOfActiveParts")
    public List<Part> getListOfActiveParts() {
        buildPartList();
        return activeParts;
    }

    /**Getter for Thymeleaf*/
    @ModelAttribute("getOrderedItems")
    public Map<Part, Integer> getOrderedItems() {
        updateOrderedItems();
        return orderedItems;
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

    /**Used by Thymeleaf to get a string value of the subtotal*/
    @ModelAttribute("getOrderSubtotalCost")
    public String getOrderSubtotalCostString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getOrderSubtotalCost());
    }

    /**Getter for subtotal order cost amount*/
    public BigDecimal getOrderSubtotalCost() {
        BigDecimal total = new BigDecimal(0);
        for (Map.Entry<Part, Integer> entry : orderedItems.entrySet()) {
            BigDecimal unitCost = entry.getKey().getPricePerUnit();
            unitCost = unitCost.multiply(getFlatMarkupRate());
            Integer quantity = entry.getValue();
            BigDecimal unitPrice = unitCost.multiply(new BigDecimal(quantity));
            total = total.add(unitPrice);
        }
        return total;
    }

    /**Used by Thymeleaf to calculate the item price displayed*/
    @ModelAttribute("getFlatMarkupRate")
    public BigDecimal getFlatMarkupRate() {
        return FLAT_MARKUP_RATE;
    }

    /**Used by Thymeleaf to get a string value of the shipping fee*/
    @ModelAttribute("getShippingFee")
    public String getShippingFeeString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getShippingFee());
    }

    /**Getter for the flat shipping fee.*/
    public BigDecimal getShippingFee() {
        return FLAT_SHIPPING_FEE;
    }

    /**Used by Thymeleaf to get a string value of the tax amount*/
    @ModelAttribute("getOrderTaxAmount")
    public String getOrderTaxAmountString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getOrderTaxAmount());
    }

    /**Getter for order tax amount*/
    public BigDecimal getOrderTaxAmount() {
        return SALES_TAX_RATE.multiply(getOrderSubtotalCost());
    }

    /**Used by Thymeleaf to get a string value of the total order amount*/
    @ModelAttribute("getOrderTotalCost")
    public String getOrderTotalCostString() {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(getTotalOrderCost());
    }

    /**Getter for total order cost amount*/
    public BigDecimal getTotalOrderCost() {
        return getOrderSubtotalCost().add(getOrderTaxAmount().add(getShippingFee()));
    }
}
