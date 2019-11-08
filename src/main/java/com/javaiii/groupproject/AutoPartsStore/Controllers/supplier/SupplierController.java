package com.javaiii.groupproject.AutoPartsStore.Controllers.supplier;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.command.EditSupplierCommand;
import com.javaiii.groupproject.AutoPartsStore.command.IdCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SupplierController {

    private DatabaseManager db;
    private List<Supplier> supplierList;

    public SupplierController() {
        connect();
        init();
    }

    /**
     * This is used to initialize the command object we will need to select a
     * supplier to edit. It is called any time a request is made to the
     * mapping in the annotation. It then gives the model an attribute for
     * holding onto a supplier ID from the list when clicking edit.
     */
    @RequestMapping(value="/suppliers/supplierList")
    public String initSupplierListDisplay(Model model) {
        init();
        model.addAttribute("supplierIdCommand", new IdCommand());
        return "suppliers/supplierList";
    }

    /**
     * This specifically handles Post requests to the Supplier list page. It
     * grabs the ID of the selected Supplier from the Command object and gets
     * the Supplier information from the database from the ID. It then creates
     * another Command object specifically for editing Suppliers from the
     * information received from the database and gives it to the attributes
     * that will be given to the redirect action and tells Spring to redirect
     * to the editSupplier page.
     */
    @PostMapping(value="/suppliers/supplierList")
    public String postSupplierList(@ModelAttribute("supplierIdCommand") IdCommand id,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            System.err.println("Binding result has errors");
        }
        Supplier editSupplier = null;
        try {
            editSupplier = db.retrieveSupplierByID(id.getId());
        }
        catch (SQLException ex) {
            System.err.println("Error retrieving supplier by ID from database");
            ex.printStackTrace();
            return "error/databaseError";
        }

        EditSupplierCommand editCmd = new EditSupplierCommand(editSupplier);
        redirectAttributes.addFlashAttribute("supplier", editCmd);
        return "redirect:/suppliers/editSupplier";
    }

    /**
     * This sets up the editSupplier page. It receives a Command object containing
     * the information of the Supplier that we want to make changes to. Thymeleaf
     * uses the command object to fill the input values of the form with the
     * current information. When the user clicks the submit button, the information
     * that is in the form will then be put back into the Command object to be
     * sent to the Post handler.
     */
    @RequestMapping(value="/suppliers/editSupplier")
    public String initEditSupplierView(@ModelAttribute("supplier") EditSupplierCommand cmd,
                                       BindingResult bindingResult,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            System.err.println("Binding result has errors");
        }
        redirectAttributes.addFlashAttribute("supplier", cmd);
        return "suppliers/editSupplier";
    }

    /**
     * This handles Post requests on the editSupplier page. It grabs the Command
     * object holding the updated information of the supplier that has been edited.
     * It then unpacks this information into a new Supplier object, which is then
     * written to the database.
     */
    @PostMapping(value="/suppliers/editSupplier")
    public String postEditSupplier(@ModelAttribute("supplier") EditSupplierCommand cmd,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            System.err.println("Binding result has errors");
        }

        // get the information from the command and write it to the database
        try {
            Supplier supplier = cmd.unpackExisting();
            db.saveToDatabase(supplier);
            buildSupplierList();
        }
        catch (SQLException ex) {
            System.err.println("Error writing supplier to database");
            ex.printStackTrace();
            return "errors/databaseError";
        }

        // redirect to the success page
        return "suppliers/updateSuccess";
    }

    /**
     * Lets Spring and Thymeleaf map the updateSuccess template to a request.
     */
    @RequestMapping(value="/suppliers/updateSuccess")
    public String initUpdateSuccessView() {
        return "suppliers/updateSuccess";
    }

    /**
     * Lets Spring and Thymeleaf map the addEmployee template to a request.
     * It also gives Thymeleaf a new Command object to work with while setting
     * the fields of a new Supplier.
     */
    @RequestMapping(value="/suppliers/addSupplier")
    public String initAddSupplierView(Model model) {
        model.addAttribute("supplier", new EditSupplierCommand());
        return "suppliers/addSupplier";
    }

    /**
     * This handles post requests when adding a new Supplier. It takes the Command
     * object with the information entered on the form, unpacks it into a Supplier
     * object, and then writes that to the database.
     */
    @PostMapping(value="/suppliers/addSupplier")
    public String postAddSupplier(@ModelAttribute("supplier") EditSupplierCommand cmd,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            System.err.println("Binding result has errors");
        }

        try {
            Supplier supplier = cmd.unpackNew();
            db.saveToDatabase(supplier);
            buildSupplierList();
        }
        catch (SQLException ex) {
            System.err.println("Error saving new supplier to database");
            ex.printStackTrace();
            return "errors/databaseError";
        }

        return "suppliers/updateSuccess";
    }

    /**Spring Getter mapping for supplierList*/
    @ModelAttribute("supplierList")
    public List<Supplier> getSupplierList() {
        buildSupplierList();
        return supplierList;
    }

    /**Initializes a new connection to the database*/
    private void connect() {
        db = new DatabaseManager(true);
    }

    /**Initializes the objects the controller is to work with*/
    private void init() {
        buildSupplierList();
    }

    /**Builds the list of suppliers from the database*/
    private void buildSupplierList() {
        connect();
        try {
            supplierList = db.getAllSuppliers();
        }
        catch (SQLException ex) {
            System.err.println("Error retrieving supplier list");
            ex.printStackTrace();
            supplierList = new ArrayList<>();// return an empty list
        }
    }
}
