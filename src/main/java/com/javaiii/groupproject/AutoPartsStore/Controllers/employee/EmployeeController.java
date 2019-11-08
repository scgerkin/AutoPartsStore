package com.javaiii.groupproject.AutoPartsStore.Controllers.employee;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.people.Employee;
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
public class EmployeeController {

    private DatabaseManager db;

    private List<Employee> employeeList;

    public EmployeeController() {
        connect();
        init();
    }

    @RequestMapping(value="/employees/employeeList")
    public String initEmployeeListDisplay(Model model) {
        init();
        model.addAttribute("employeeIdCommand", new IdCommand());
        return "employees/employeeList";
    }

    @PostMapping(value="/employees/employeeList")
    public String postEmployeeList(@ModelAttribute("employeeIdCommand") IdCommand id,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        return "employees/employeeList";
    }












    @ModelAttribute("employeeList")
    public List<Employee> getEmployeeList() {
        return employeeList;
    }



    private void connect() {
        db = new DatabaseManager(true);
    }

    private void init() {

        buildEmployeeList();
    }

    private void buildEmployeeList() {
        connect();
        try {
            employeeList = db.getAllEmployees();
        }
        catch (SQLException ex) {
            //todo handle dynamically
            System.err.println("Error retrieving employee list");
            ex.printStackTrace();
            employeeList = new ArrayList<>();
        }
    }
}
