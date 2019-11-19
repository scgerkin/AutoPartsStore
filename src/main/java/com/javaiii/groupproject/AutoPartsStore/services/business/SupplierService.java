package com.javaiii.groupproject.AutoPartsStore.services.business;

import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;

public interface SupplierService {
    Integer getQuantityOnHand(Integer supplierId, Integer partId);
    void updateQuantityOnHand(Integer partId, Integer quantity);
    Integer addNewSupplier(Supplier supplier);
}
