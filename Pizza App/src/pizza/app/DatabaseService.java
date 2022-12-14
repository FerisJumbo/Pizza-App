package pizza.app;

import java.util.ArrayList;

import java.sql.*;
import io.bretty.console.table.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseService {
    private String _URL,
            _user,
            _pass;
    
    private static Connection _connection;
    
    
    /**
     * Setups up the connection to the database with the provided information.
     * 
     * @param host
     * @param port
     * @param db
     * @param user
     * @param pass 
     */
    public DatabaseService(String host, String port, String db, String user, String pass) {
        _URL = "jdbc:postgresql://" + host + ":" + port + "/"+db;
        _user = user;
        _pass = pass;
        
        try {
            _connection = DriverManager.getConnection(_URL,_user,_pass);
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    /**
     * Will GET information from the database.
     * 
     * @param query
     * @return 
     */
    public static ResultSet GET(String query) {
        try {
            Statement statement = _connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            return resultSet;
        } catch (SQLException e) { e.printStackTrace(); }
        
        return null;
    }
    
    public static ArrayList<ArrayList<String>> GETAsMultiArray(String query) {
        ResultSet rs = GET(query);
        
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        
        int columnCount;
        
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
            
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                for (int i = 0; i < columnCount; i++) {
                    row.add(rs.getString(i+1));
                }
                data.add(row);
            }
            
            return replaceNullValues(data);
            
        } catch (SQLException ex) { ex.printStackTrace(); }
        
        return null;
    }
    
    /**
     * Returns a Table object which contains the query and can be directly
     * printed to screen.
     * @param headers
     * @param query
     * @return 
     */
    public static Table GETAsPrintableTable(String query) {
        int MAX_WIDTH = 20;
        
        ColumnFormatter<String> cf = ColumnFormatter.text(Alignment.CENTER, MAX_WIDTH);
        
        ResultSet rs = GET(query);
        
        int columnCount;
        try {
            // Pass headers and data into a multidimensional ArrayList
            ResultSetMetaData rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
            
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i-1] = rsmd.getColumnName(i).toUpperCase();
            }

            ArrayList<ArrayList<String>> data = GETAsMultiArray(query);

            // Convert Multidimensional ArrayList to Array
            String[][] dataAsArray = new String[data.size()][];
            for (int i = 0; i < data.size(); i++) {
                ArrayList<String> row = data.get(i);
                dataAsArray[i] = row.toArray(new String[row.size()]);
            }

            return Table.of(headers, dataAsArray, cf);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    
    /**
    * Replaces all null values in the given data with empty strings.
    *
    * @param data an ArrayList of ArrayLists of strings representing the data to process
    * @return an ArrayList of ArrayLists of strings where all null values have been replaced with empty strings
    */
    private static ArrayList<ArrayList<String>> replaceNullValues(ArrayList<ArrayList<String>> data) {
        for (int i = 0; i < data.size(); i++) {
            ArrayList<String> row = data.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == null) {
                    row.set(j, "");
                }
            }
        }
        return data;
    }
    
    /**
     * Posts the query to the database.Used for INSERT, UPDATE, and DROP
     * queries.
     * @param query
     * @param addedItem
     * @return
     */
    public static void POST(String query) {
        try {
            PreparedStatement st = _connection.prepareStatement(query);
            st.executeUpdate();
            st.close();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
    
}
