package com.javaiii.groupproject.AutoPartsStore.services.web;

import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.services.business.SupplierService;
import com.javaiii.groupproject.AutoPartsStore.services.consumers.commands.*;
import com.javaiii.groupproject.AutoPartsStore.services.exceptions.DatabaseException;
import com.javaiii.groupproject.AutoPartsStore.services.exceptions.SupplierPartMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SupplierServiceController {
    private SupplierService supplierService;

    @Autowired
    public void setSupplierService(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping(value = "quantity-on-hand/", consumes = "application/json")
    public ResponseEntity<Integer> getQuantityOnHand(@RequestBody PartQuantityCommand partQuantityCommand) {
        Integer supplierId = partQuantityCommand.getSupplierId();
        Integer partId = partQuantityCommand.getPartId();
        try {
            Integer quantityOnHand = supplierService.getQuantityOnHand(supplierId, partId);
            return new ResponseEntity<>(quantityOnHand, HttpStatus.OK);
        }
        catch (SupplierPartMismatchException ex) {
            return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
        }
        catch (DatabaseException ex) {
            return new ResponseEntity<>(-1, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PostMapping(value = "update-quantity/", consumes = "application/json")
    public ResponseEntity updateQuantityOnHand(@RequestBody PartQuantityCommand partQuantityCommand) {
        Integer partId = partQuantityCommand.getPartId();
        Integer quantity = partQuantityCommand.getQuantity();
        supplierService.updateQuantityOnHand(partId, quantity);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "add-new-supplier/", consumes = "application/json")
    public ResponseEntity<Integer> addNewSupplier(@RequestBody Supplier supplier) {
        Integer supplierId = supplierService.addNewSupplier(supplier);
        return new ResponseEntity<>(supplierId, HttpStatus.ACCEPTED);
    }
}
