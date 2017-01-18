package com.lucadev.dbfacade;

import com.lucadev.dbfacade.di.DBDependencyInject;
import com.lucadev.dbfacade.di.DBSerializer;

import java.util.*;

/**
 * Represents a single row/record of a database table that was returned through a query.
 *
 * @author Luca Camphuisen < Luca.Camphuisen@hva.nl >
 */
public class DBRow extends ArrayList {

    /**
     * Reference to the entire table
     */
    private final DBTable TABLE;

    private final int ROW_NUMBER;

    /**
     * Constructor requires the table as argument for correct usage(such as column names).
     *
     * @param dbTable
     */
    public DBRow(final DBTable dbTable, final int rowNumber) {
        this.TABLE = dbTable;
        this.ROW_NUMBER = rowNumber;
    }

    public boolean hasNext() {
        return TABLE.size() >= ROW_NUMBER + 1;
    }

    /**
     * Get the next db row
     * @return
     */
    public DBRow next() {
        if(hasNext()) {
            return TABLE.get(ROW_NUMBER + 1);
        }
        return null;
    }

    public boolean hasPrevious() {
        return ROW_NUMBER - 1 >= 0 && TABLE.size() > 1;
    }

    /**
     * Get the previous db row
     * @return
     */
    public DBRow previous() {
        if(hasPrevious()) {
            return TABLE.get(ROW_NUMBER - 1);
        }
        return null;
    }

    /**
     * Obtain the entire result table
     *
     * @return
     */
    public DBTable getTable() {
        return TABLE;
    }

    public List<String> getColumnNames() {
        return getTable().getColumnNames();
    }

    public Object getObject(int i) {
        return super.get(i);
    }

    public Object getObject(String columnName) {
        return getObject(getTable().getColumIndex(columnName));
    }

    public String getString(int i) {
        if (isNull(i)) {
            return null;
        }
        return (String) getObject(i);
    }

    public <T> T deserializeTo(Class<T> clazz) {
        DBSerializer<T> serializer = DBDependencyInject.find(clazz);
        if(serializer == null) {
            return null;
        }

        return serializer.build(this);
    }

    public String getString(String columnName) {
        return (String) getObject(columnName);
    }

    public Date getDate(int i) {
        if (isNull(i)) {
            return null;
        }
        return (Date) getObject(i);
    }

    public Date getDate(String columnName) {
        return (Date) getObject(columnName);
    }

    public int getInt(int i) {
        if (isNull(i)) {
            return 0;
        }
        return (Integer) getObject(i);
    }

    public int getInt(String columnName) {
        Object object = getObject(columnName);
        return (object == null) ? 0 : (Integer) object;
    }

    public double getDouble(int i) {
        if (isNull(i)) {
            return 0;
        }
        return (Double) getObject(i);
    }

    public double getDouble(String columnName) {
        return (Double) getObject(columnName);
    }

    public boolean getBoolean(int i) {
        if (isNull(i)) {
            return false;
        }
        return (Boolean) getObject(i);
    }

    public boolean getBoolean(String columnName) {
        return (Boolean) getObject(columnName);
    }

    public boolean isNull(int i) {
        return super.get(i) == null;
    }

    public boolean isNull(String columnName) {
        return super.get(getTable().getColumIndex(columnName)) == null;
    }

}
