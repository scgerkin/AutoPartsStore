package com.javaiii.groupproject.AutoPartsStore;

import com.javaiii.groupproject.AutoPartsStore.Controllers.employee.EmployeeController;
import com.javaiii.groupproject.AutoPartsStore.Controllers.inventory.InventoryController;
import com.javaiii.groupproject.AutoPartsStore.Controllers.orders.customer.CustomerOrderController;
import com.javaiii.groupproject.AutoPartsStore.Controllers.orders.resupply.ResupplyOrderController;
import com.javaiii.groupproject.AutoPartsStore.Controllers.supplier.SupplierController;
import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.services.business.SupplierServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutoPartsStoreApplication {

	public static void main(String[] args) {
		DatabaseManager db = new DatabaseManager(false);
		SupplierController.setDb(db);
		EmployeeController.setDb(db);
		ResupplyOrderController.setDb(db);
		CustomerOrderController.setDb(db);
		InventoryController.setDb(db);
        SupplierServiceImpl.setDb(db);
		SpringApplication.run(AutoPartsStoreApplication.class, args);
	}

}
