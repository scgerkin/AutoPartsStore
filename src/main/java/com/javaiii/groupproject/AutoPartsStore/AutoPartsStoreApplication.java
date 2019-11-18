package com.javaiii.groupproject.AutoPartsStore;

import com.javaiii.groupproject.AutoPartsStore.Controllers.employee.EmployeeController;
import com.javaiii.groupproject.AutoPartsStore.Controllers.inventory.InventoryController;
import com.javaiii.groupproject.AutoPartsStore.Controllers.orders.customer.CustomerOrderController;
import com.javaiii.groupproject.AutoPartsStore.Controllers.orders.resupply.ResupplyOrderController;
import com.javaiii.groupproject.AutoPartsStore.Controllers.supplier.SupplierController;
import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.orders.ResupplyOrder;
import com.javaiii.groupproject.AutoPartsStore.services.SupplierServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
public class AutoPartsStoreApplication {

	public static void main(String[] args) {
		DatabaseManager db = new DatabaseManager(true);
		SupplierController.setDb(db);
		EmployeeController.setDb(db);
		ResupplyOrderController.setDb(db);
		CustomerOrderController.setDb(db);
		InventoryController.setDb(db);
        SupplierServiceImpl.setDb(db);
		SpringApplication.run(AutoPartsStoreApplication.class, args);
	}

}
