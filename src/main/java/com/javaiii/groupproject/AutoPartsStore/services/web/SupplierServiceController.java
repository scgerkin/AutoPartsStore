package com.javaiii.groupproject.AutoPartsStore.services.web;

import com.javaiii.groupproject.AutoPartsStore.services.business.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupplierServiceController {
    private SupplierService supplierService;

    @Autowired
    public void setSupplierService(SupplierService supplierService) {
        this.supplierService = supplierService;
    }
}
