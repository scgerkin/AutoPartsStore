package com.javaiii.groupproject.AutoPartsStore.services.business;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class SupplierServiceImpl implements SupplierService {

    private static DatabaseManager db;

    private static final int MAX_QUANTITY = 100;

    public SupplierServiceImpl() {
    }

    public static void setDb(DatabaseManager databaseManager) {
        db = databaseManager;
    }

    @Override
    public Integer getQuantityOnHand(Integer supplierId, Integer partId) {
        Supplier supplier = null;
        Part part = null;

        try {
            supplier = db.retrieveSupplierByID(supplierId);
            part = db.retrievePartByID(partId);
        }
        catch (SQLException ex) {
            //fixme this should be handled more specifically
            return -1;
        }
        return part.getQuantityOnHand();
    }

    @Override
    public Boolean updateQuantityOnHand(Integer partId, Integer newQuantity) {
        if (newQuantity > MAX_QUANTITY) {
            //fixme throw an exception
            return false;
        }

        Part part = null;

        try {
            part = db.retrievePartByID(partId);
            part.setQuantityOnHand(newQuantity);
            db.saveToDatabase(part);
            return true;
        }
        catch (SQLException ex) {
            //fixme handle with more detail
            return false;
        }
    }

    @Override
    public Integer addNewSupplier(Supplier supplier) {
        if (supplier.getBusinessID() != -1) {
            //fixme handle with exception and detail
            return -1;
        }
        try {
            db.saveToDatabase(supplier);
            return supplier.getBusinessID();
        }
        catch (SQLException ex) {
            //fixme handle with exception and detail
            return -1;
        }
    }
}
