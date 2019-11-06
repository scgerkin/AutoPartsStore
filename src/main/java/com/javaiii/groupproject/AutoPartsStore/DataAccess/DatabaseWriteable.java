package com.javaiii.groupproject.AutoPartsStore.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This interface is used to allow writing Objects to the database. Everything
 * that can be saved to the database must override this method and provide a way
 * of creating a PreparedStatement that the DatabaseManager can execute to save
 * it to the database. The implementation for this should ideally include two
 * separate methods, one for insert and one for update. These should be ultimately
 * returned by this method for updating the database with new/modified data.
 */
public interface DatabaseWriteable {
    PreparedStatement getWriteStatement(Connection connection) throws SQLException;
    void setGeneratedId(Integer id);
}
