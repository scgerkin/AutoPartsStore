package com.javaiii.groupproject.AutoPartsStore.Controllers.inventory;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import com.javaiii.groupproject.AutoPartsStore.command.IdCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InventoryController {

    private DatabaseManager db;
    List<Part> inventory;



    public InventoryController() {
        connect();
        init();
    }

    private void connect() {
        db = new DatabaseManager(true);
    }

    private void init() {
        inventory = new ArrayList<>();
        buildInventory();
    }

    @RequestMapping(value="/inventory/inventoryList")
    public String initInventoryDisplay(Model model) {
        connect();
        init();
        model.addAttribute("partIdCommand", new IdCommand());
        return "inventory/inventoryList";
    }

    @RequestMapping(value="/inventory/addPart")
    public String initAddPartDisplay(Model model) {
        return "inventory/addPart";
    }


    @ModelAttribute("inventory")
    public List<Part> getInventory() {
        return inventory;
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
