package com.lucadev.dbfacade.di;

import com.lucadev.dbfacade.DBRow;
import com.lucadev.dbfacade.DBTable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Luca Camphuisen < Luca.Camphuisen@hva.nl >
 */
public abstract class DBSerializer<T> {

    /**
     * Parse the row into a new object of type T
     * @param row
     * @return
     */
    public abstract T build(DBRow row);

    public Collection<T> build(DBTable table) {
        Collection<T> collection = new ArrayList<>();
        table.forEach(row -> collection.add(build(row)));
        return collection;
    }

}
