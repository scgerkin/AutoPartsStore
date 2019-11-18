package com.javaiii.groupproject.AutoPartsStore.services;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService {

    private static DatabaseManager db;

    public SupplierServiceImpl() {
    }

    public static void setDb(DatabaseManager databaseManager) {
        db = databaseManager;
    }

    @Override
    public Integer getQuantityOnHand(Integer supplierId, Integer partId) {
        //todo implement
        return null;
    }

    @Override
    public Boolean updateQuantityOnHand(Integer partId, Integer quantity) {
        //todo implement
        return null;
    }

    @Override
    public Integer addNewSupplier(Supplier supplier) {
        //todo implement
        return null;
    }
}
