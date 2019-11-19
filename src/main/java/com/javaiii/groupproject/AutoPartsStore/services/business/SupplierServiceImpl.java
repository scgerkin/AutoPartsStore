package com.javaiii.groupproject.AutoPartsStore.services.business;

import com.javaiii.groupproject.AutoPartsStore.DataAccess.DatabaseManager;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.Models.products.Part;
import com.javaiii.groupproject.AutoPartsStore.exceptions.ItemNotFoundException;
import com.javaiii.groupproject.AutoPartsStore.services.exceptions.*;
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
        try {
            Supplier supplier = getSupplier(supplierId);
            Part part = getPart(partId);
            if (supplier.getBusinessID().compareTo(part.getSupplier().getBusinessID()) != 0) {
                throw new SupplierPartMismatchException();
            }
            return part.getQuantityOnHand();
        } catch (SQLException ex) {
            throw new DatabaseException();
        }
    }

    @Override
    public void updateQuantityOnHand(Integer partId, Integer newQuantity) {
        if (newQuantity < 0 || newQuantity > MAX_QUANTITY) {
            throw new InvalidQuantityException();
        }
        try {
            Part part = getPart(partId);
            part.setQuantityOnHand(newQuantity);
            db.saveToDatabase(part);
        } catch (SQLException ex) {
            throw new DatabaseException();
        }
    }

    @Override
    public Integer addNewSupplier(Supplier supplier) {
        if (supplier.getBusinessID() == null) {
            supplier.setBusinessID(-1);
        }
        if (validSupplier(supplier)) {
            try {
                db.saveToDatabase(supplier);
            } catch (SQLException ex) {
                throw new DatabaseException();
            }
        }
        return supplier.getBusinessID();
    }

    private Supplier getSupplier(Integer supplierId) throws SQLException {
        try {
            return db.retrieveSupplierByID(supplierId);
        } catch (ItemNotFoundException ex) {
            throw new InvalidSupplierIdException();
        }
    }

    private Part getPart(Integer partId) throws SQLException {
        try {
            return db.retrievePartByID(partId);
        } catch (ItemNotFoundException ex) {
            throw new InvalidPartIdException();
        }
    }

    // this mess of conditionals is necessary because of poor entity design decision
    private boolean validSupplier(Supplier supplier) {
        if (supplier.getBusinessID() != -1) {
            return false;
        }
        if (supplier.getCompanyName() == null) {
            return false;
        }
        if (supplier.getPrimaryPhone() == null || supplier.getPrimaryPhone().length() != 10) {
            return false;
        }
        if (!supplier.getSecondaryPhone().isEmpty()) {
            if (supplier.getSecondaryPhone().length() != 10) {
                return false;
            }
        }
        return supplier.getAddress() != null;
    }
}
