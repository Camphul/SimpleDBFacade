package com.lucadev.dbfacade;

import com.lucadev.dbfacade.di.DBDependencyInject;
import com.lucadev.dbfacade.di.DBSerializer;

import java.util.*;

/**
 * Represents the table that is returned from executing a query
 *
 * @author Luca Camphuisen < Luca.Camphuisen@hva.nl >
 */
public class DBTable extends ArrayList<DBRow> {

    /**
     * Column name to index of column mapping.
     */
    private final HashMap<String, Integer> COLUMNS;

    /**
     * Database instance that was used to execute the query to obtain this table.
     */
    private final Database DATABASE;

    public DBTable(final Database database, final HashMap<String, Integer> columns) {
        this.DATABASE = database;
        this.COLUMNS = columns;
    }

    public HashMap<String, Integer> getColumns() {
        return COLUMNS;
    }

    public Database getDatabase() {
        return DATABASE;
    }

    public int getColumnLength() {
        return getColumns().size();
    }

    public int getColumIndex(String columnName) {
        return getColumns().get(columnName);
    }

    public <T> Collection<T> deserializeTo(Class<T> clazz) {
        DBSerializer<T> serializer = DBDependencyInject.find(clazz);
        if(serializer == null) {
            return Collections.emptyList();
        }

        return serializer.build(this);
    }

    public String getColumnName(int index) {
        for (Map.Entry<String, Integer> entry : getColumns().entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<String> getColumnNames() {
        return new ArrayList<>(COLUMNS.keySet());
    }

    public DBRow first() {
        //prevent nullpointer
        if (isEmpty()) {
            return null;
        }
        return get(0);
    }

    public DBRow last() {
        //prevent nullpointer
        if (isEmpty()) {
            return null;
        }

        return get(size() - 1);
    }
}
