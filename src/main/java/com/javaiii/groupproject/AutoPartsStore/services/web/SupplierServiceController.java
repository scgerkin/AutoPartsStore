package com.javaiii.groupproject.AutoPartsStore.services.web;

import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.services.business.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupplierServiceController {
    private SupplierService supplierService;

    @Autowired
    public void setSupplierService(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping(value = "quantity-on-hand/{supplierId}/{partId}")
    public ResponseEntity<Integer> getQuantityOnHand(@PathVariable Integer supplierId,
                                                     @PathVariable Integer partId) {
        Integer quantityOnHand = supplierService.getQuantityOnHand(supplierId, partId);
        return new ResponseEntity<>(quantityOnHand, HttpStatus.OK);
    }

    @GetMapping(value = "update-quantity/{partId}/{newQuantity}")
    public ResponseEntity<Boolean> updateQuantityOnHand(@PathVariable Integer partId,
                                                        @PathVariable Integer newQuantity) {
        if (newQuantity < 0) {
            return new ResponseEntity<>(false, HttpStatus.NOT_ACCEPTABLE);
        }
        Boolean added = supplierService.updateQuantityOnHand(partId, newQuantity);
        if (added) {
            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "add-new-supplier/{supplier}")
    public ResponseEntity<Integer> addNewSupplier(@PathVariable Supplier supplier) {
        //todo test non-null fields
        // consider returning a String with more information
        if (supplier.getBusinessID() != -1) {
            return new ResponseEntity<>(-1, HttpStatus.NOT_ACCEPTABLE);
        }
        Integer supplierId = supplierService.addNewSupplier(supplier);
        return new ResponseEntity<>(supplierId, HttpStatus.ACCEPTED);
    }
}
