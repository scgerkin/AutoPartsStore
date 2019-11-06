package com.javaiii.groupproject.AutoPartsStore.Controllers;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
public class PartListController {

    @RequestMapping("/partList")
    public String partList(Model model) {
        DatabaseManager db = new DatabaseManager();
        List<Part> parts = null;
        try {
            parts = db.getAllActiveParts();
        }
        catch (SQLException ex) {
            System.out.println("SQL Exception");
        }

        model.addAttribute("parts", parts);
        return "partList";
    }
}
