package com.javaiii.groupproject.AutoPartsStore.services;

import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;

public interface SupplierService {
    Integer getQuantityOnHand(Integer supplierId, Integer partId);
    Boolean updateQuantityOnHand(Integer partId, Integer quantity);
    Integer addNewSupplier(Supplier supplier);
}
