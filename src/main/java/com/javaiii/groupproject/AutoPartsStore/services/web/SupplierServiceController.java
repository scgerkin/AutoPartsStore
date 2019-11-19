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
    public ResponseEntity updateQuantityOnHand(@PathVariable Integer partId,
                                               @PathVariable Integer newQuantity) {
        supplierService.updateQuantityOnHand(partId, newQuantity);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "add-new-supplier/{supplier}")
    public ResponseEntity<Integer> addNewSupplier(@PathVariable Supplier supplier) {
        Integer supplierId = supplierService.addNewSupplier(supplier);
        return new ResponseEntity<>(supplierId, HttpStatus.ACCEPTED);
    }
}
