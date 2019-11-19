package com.javaiii.groupproject.AutoPartsStore.services.web;

import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.services.business.SupplierService;
import com.javaiii.groupproject.AutoPartsStore.services.exceptions.BadSupplierInformationException;
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

    @RequestMapping(value = "add-new-supplier/{supplier}")
    public ResponseEntity<Integer> addNewSupplier(@PathVariable Supplier supplier) {
        System.out.println("ADD NEW SUPPLIER");
        Integer supplierId = supplierService.addNewSupplier(supplier);
        return new ResponseEntity<>(supplierId, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "add-new-supplier/{companyName}/{contactPerson}/{primaryPhone}/{secondaryPhone}" +
                            "/{website}/{street}/{city}/{state}/{zipCode}/{notes}")
    public ResponseEntity<Integer> addNewSupplier(@PathVariable String companyName,
                                                  @PathVariable String contactPerson,
                                                  @PathVariable String primaryPhone,
                                                  @PathVariable String secondaryPhone,
                                                  @PathVariable String website,
                                                  @PathVariable String street,
                                                  @PathVariable String city,
                                                  @PathVariable String state,
                                                  @PathVariable String zipCode,
                                                  @PathVariable String notes) {
        System.out.println("RECEIVED NEW SUPPLIER");
        try {
            Address address = new Address(street, city, state, zipCode);
            Supplier supplier = Supplier.createNew(companyName, contactPerson, primaryPhone, secondaryPhone, website, address, notes);
            Integer supplierId = supplierService.addNewSupplier(supplier);
            return new ResponseEntity<>(supplierId, HttpStatus.ACCEPTED);
        }
        catch (Exception ex) {
            throw new BadSupplierInformationException();
        }
    }
}
