package com.javaiii.groupproject.AutoPartsStore.Controllers.employee;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.people.Employee;
import com.javaiii.groupproject.AutoPartsStore.command.EditEmployeeCommand;
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
        Employee editEmployee = null;
        try {
            editEmployee = db.getEmployeeByID(id.getId());
        }
        catch (SQLException ex) {
            System.err.println("Error retrieving employee by ID from database");
            ex.printStackTrace();
            return "error/databaseError";
        }

        EditEmployeeCommand editCmd = new EditEmployeeCommand(editEmployee);

        redirectAttributes.addFlashAttribute("employee", editCmd);

        return "redirect:/employees/editEmployee";
    }

    /**This lets us catch the redirect to edit an employee from the employee list*/
    @RequestMapping(value="/employees/editEmployee")
    public String initEditEmployee(@ModelAttribute("employee") EditEmployeeCommand cmd,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            System.err.println("Binding result has errors");
        }
        redirectAttributes.addFlashAttribute("employee", cmd);
        return "employees/editEmployee";
    }

    @PostMapping(value="/employees/editEmployee")
    public String postEditEmployee(@ModelAttribute("employee") EditEmployeeCommand cmd,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            System.err.println("Binding result has errors");
        }

        try {
            Employee employee = cmd.unpackExisting();
            db.saveToDatabase(employee);
            buildEmployeeList();
        }
        catch (SQLException ex) {
            System.err.println("Error writing employee to database");
            ex.printStackTrace();
            return "errors/databaseError";
        }

        return "employees/updateSuccess";
    }

    @RequestMapping(value="/employees/updateSuccess")
    public String initUpdateSuccessDisplay() {
        return "employees/updateSuccess";
    }

    @RequestMapping(value="/employees/addEmployee")
    public String initAddEmployeeDisplay(Model model) {
        model.addAttribute("employee", new EditEmployeeCommand());
        return "employees/addEmployee";
    }

    @PostMapping(value="/employees/addEmployee")
    public String postAddEmployee(@ModelAttribute("employee") EditEmployeeCommand cmd,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        try {
            Employee employee = cmd.unpackNew();
            db.saveToDatabase(employee);
            buildEmployeeList();
        }
        catch (SQLException ex) {
            System.err.println("Error writing new employee to database");
            ex.printStackTrace();
            return "errors/databaseError";
        }

        return "employees/updateSuccess";
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
