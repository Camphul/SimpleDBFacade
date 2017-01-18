package com.lucadev.dbfacade;

import com.lucadev.dbfacade.util.Helper;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database facade which makes it easy to use java jdbc with this project.
 *
 * @author Luca Camphuisen < Luca.Camphuisen@hva.nl >
 */
public class Database {

    /**
     * The connection with the database
     */
    private Connection connection;

    /**
     * Debug mode, will print more things to console.
     */
    private static final boolean DEBUG = false;

    /**
     * Logger format for regular console output
     */
    private static final String LOG_FORMAT = "DB - %s\r\n";

    /**
     * Logger format for debug info console output
     */
    private static final String LOG_FORMAT_DEBUG = "DB-DEBUG - %s\r\n";

    /**
     * Date format that is used to store dates into the database.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Logs a message into the console following the LOG_FORMAT format.
     *
     * @param message the message to log.
     */
    public void log(String message) {
        System.out.printf(LOG_FORMAT, message);
    }

    /**
     * Logs an exception to our debug console.
     *
     * @param ex the exception that was thrown
     */
    public void log(Exception ex) {
        log(ex.getMessage());
        ex.printStackTrace();
    }

    public Database() {

    }

    public Database(Connection connection) {
        this.connection = connection;
    }

    public Database(String url, String username, String password) {
        try {
            connect(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Same as the log method but it only prints when DEBUG == true. Also uses another format.
     *
     * @param message
     */
    public void debug(String message) {
        if (DEBUG) {
            System.out.printf(LOG_FORMAT_DEBUG, message);
        }
    }

    private boolean connect(String url, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        return true;
    }

    /**
     * Gets the raw database connection from java sql.
     *
     * @return
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Just easily selects all rows from the specified table.
     *
     * @param tableName the name of the db table
     * @return
     */
    public DBTable selectAll(String tableName) {
        return executeTableQuery(String.format("SELECT * FROM %s", tableName));
    }

    /**
     * Deletes all contents of a table. Be warned!
     *
     * @param tableName
     * @return
     */
    public int deleteAll(String tableName) {
        return executeUpdateQuery(String.format("DELETE FROM %s", tableName));
    }

    /**
     * Executes an update query such as INSERT, DELETE or UPDATE.
     *
     * @param query the query to execute.
     * @return the rows that changed. Most of the time it's 1 when you update or add an entry
     */
    public int executeUpdateQuery(String query) {
        Statement statement = createNewStatement();
        if (statement == null) {
            log("Statement cannot be null!");
        }
        try {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            log(e);
        }
        return 0;
    }

    /**
     * Executes a update query such as INSERT, DELETE or UPDATE but is prepared/secure.
     *
     * @param query
     * @param mapping
     * @return
     */
    public int executePreparedUpdateQuery(String query, Object... mapping) {
        PreparedStatement preparedStatement = createNewPreparedStatement(query, mapping);
        if (preparedStatement == null) {
            log("Prepared statement is null! Cant execute update.");
            return 0;
        }
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log(e);
        }
        return 0;
    }

    /**
     * Execute a regular old query, no prepared stuff or anything.
     *
     * @param query
     * @return
     */
    public ResultSet executeQuery(String query) {
        Statement statement = createNewStatement();
        if (statement == null) {
            log("Statement cannot be null!");
            return null;
        }
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            log(e);
        }
        return null;
    }

    /**
     * Executes a prepared statement and returns a resultset
     *
     * @param query
     * @param params
     * @return
     */
    public ResultSet executePreparedQuery(String query, Object... params) {
        PreparedStatement preparedStatement = createNewPreparedStatement(query, params);
        if (preparedStatement == null) {
            log("Prepared statement is null! Cant execute query!");
            return null;
        }
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            log(e);
        }
        return null;
    }

    public DBTable executePreparedTableQuery(String query, Object... params) {
        ResultSet resultSet = executePreparedQuery(query, params);
        if (resultSet == null) {
            log("Result from prepared statement is null! Cannot convert to table.");
            return null;
        }
        return convertToTable(resultSet);
    }

    public DBTable executeTableQuery(String query) {
        ResultSet resultSet = executeQuery(query);
        if (resultSet == null) {
            log("ResultSet is null and cannot be converted to DBTable!");
            return null;
        }

        return convertToTable(resultSet);
    }

    /**
     * Creates a new statement in which a query can be executed.
     *
     * @return
     */
    public Statement createNewStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            log(e);
        }
        return null;
    }

    /**
     * Creates a secure prepared statement which cant be used for sql injection attacks.
     *
     * @param query The query to execute with question marks where the values should be.
     * @param maps  the objects to replace question marks with.
     * @return the created prepared statement.
     */
    public PreparedStatement createNewPreparedStatement(String query, Object... maps) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
            for (int i = 0; i < maps.length; i++) {
                //Sets the question mark to that value
                Object obj = maps[i];
                preparedStatement.setObject(i + 1, obj);
            }
            return preparedStatement;
        } catch (SQLException e) {
            log(e);
        }
        return null;
    }

    /**
     * Converts a result set to a dbtable.
     *
     * @param resultSet
     * @return
     */
    public DBTable convertToTable(ResultSet resultSet) {
        try {
            debug("Converting resultset to dbtable...");
            ResultSetMetaData resultMeta = resultSet.getMetaData();
            int columnSize = resultMeta.getColumnCount();
            HashMap<String, Integer> columns = new HashMap<>();
            debug("Column size: " + columnSize);
            for (int x = 0; x < columnSize; x++) {
                debug(resultMeta.getColumnLabel(x + 1) + ":" + x);
                columns.put(resultMeta.getColumnLabel(x + 1), x);
            }
            DBTable table = new DBTable(this, columns);
            //loop through rows
            while (resultSet.next()) {
                DBRow row = new DBRow(table, table.size());
                debug("Adding row to table conversion.");
                //loop through all columns of the current row
                for (int i = 0; i < columnSize; i++) {
                    row.add(i, resultSet.getObject(i + 1));
                }
                table.add(row);

            }
            resultSet.close();
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get list of column names.
     * @param rs
     * @return
     */
    public List<String> getColumns(ResultSet rs) {
        List<String> columnNames = new ArrayList();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(rsmd.getColumnName(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return columnNames;
    }

}
