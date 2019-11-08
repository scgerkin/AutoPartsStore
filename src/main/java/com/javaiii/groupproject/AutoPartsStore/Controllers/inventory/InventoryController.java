package com.javaiii.groupproject.AutoPartsStore.Controllers.inventory;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Car;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import com.javaiii.groupproject.AutoPartsStore.command.CreatePartCommand;
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
public class InventoryController {

    private static DatabaseManager db;
    List<Part> inventory;



    public InventoryController() {
        init();
    }

    public static void setDb(DatabaseManager databaseManager) {
        db = databaseManager;
    }

    private void init() {
        inventory = new ArrayList<>();
        buildInventory();
    }

    @RequestMapping(value="/inventory/inventoryList")
    public String initInventoryDisplay(Model model) {
        init();
        model.addAttribute("partIdCommand", new IdCommand());
        return "inventory/inventoryList";
    }

    @PostMapping(value="/inventory/inventoryList")
    public String togglePartStatus(@ModelAttribute("partIdCommand") IdCommand id, Model model) {
        try {
            togglePartStatus(id.getId());
        }
        catch (SQLException ex) {
            System.err.println("Error when toggling status");
            ex.printStackTrace();
            return "errors/databaseError";
        }

        return "inventory/inventoryList";
    }

    @RequestMapping(value="/inventory/addPart")
    public String initAddPartDisplay(Model model) {
        model.addAttribute("createPartCommand", new CreatePartCommand());
        return "inventory/addPart";
    }

    @PostMapping(value="/inventory/addPart")
    public String addPartPost(@ModelAttribute("createPartCommand")CreatePartCommand cmd,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Supplier supplier = db.retrieveSupplierByID(1);
            Car car = db.retrieveCarConfigByID(1);
            Part part = Part.createNew(supplier, car, cmd.name, cmd.description, cmd.category,
                cmd.pricePerUnit, cmd.quantityOnHand, null);
            db.saveToDatabase(part);
        }
        catch (SQLException ex) {
            System.err.println("Error saving new part to Database.");
            ex.printStackTrace();
            return "errors/databaseError";
        }
        return "inventory/partAddSuccess";
    }

    @RequestMapping(value="/inventory/partAddSuccess")
    public String initPartAddSuccess(Model model) {
        return "inventory/partAddSuccess";
    }


    @ModelAttribute("inventory")
    public List<Part> getInventory() {
        return inventory;
    }

    @ModelAttribute("suppliers")
    public List<Supplier> getSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        try {
            suppliers = db.getAllSuppliers();
        }
        catch (SQLException ex) {
            //todo handle
            System.err.println("Could not retrieve supplier list from Database.");
            ex.printStackTrace();
        }
        return suppliers;
    }


    private void togglePartStatus(Integer partID) throws SQLException {
        for (Part part : inventory) {
            if (part.getPartID().equals(partID)) {
                part.setDiscontinued(!part.isDiscontinued());
                db.saveToDatabase(part);
            }
        }
    }



    /**Gets the entire inventory list of parts from the database*/
    private void buildInventory() {
        try {
            inventory = db.getAllParts();
        }
        catch (SQLException ex) {
            //todo handle
            System.err.println("Could not retrieve inventory list from Database.");
            ex.printStackTrace();
        }
    }
}
