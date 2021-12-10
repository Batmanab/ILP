package uk.ac.ed.inf.database;

import java.sql.*;

/**
 * Creates a database object of DerbyDb.
 */
public class DerbyDB {
    private final Connection connection;

    public DerbyDB(String jdbcString) throws SQLException {
        connection = DriverManager.getConnection(jdbcString);
    }

    /**
     * @param sqlQuery string
     * @return output for select statement as a ResultSet
     */
    public ResultSet select(String sqlQuery) {
        Statement statement;
        try {
            statement = connection.createStatement();
            return statement.executeQuery(sqlQuery);
        }
        catch (Exception e) {
            return null;
        }

    }

    /**
     * @param sqlQuery: the query
     * @param date : the date
     * @return
     * executes the prepared statement with date parameter as placeholder. It returns the output as ResultSet.
     */
    public  ResultSet selectByDate(String sqlQuery, Date date){
        PreparedStatement pStatement;
        try{
            pStatement = connection.prepareStatement(sqlQuery);
            pStatement.setDate(1,date);
            return pStatement.executeQuery();

        } catch (Exception e){
            return null;
        }
    }

    /**
     * @param sqlQuery the sql query
     *                 to run CREATE,UPDATE statement.
     */
    public void executeUpdate(String sqlQuery){

        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sqlQuery);
        }
        catch (Exception e) {
            System.err.println("DerbyDB: Cannot executeUpdate");

        }
    }

    /**
     * @param sqlQuery the sql query
     *                 to run DROP statement.
     */
    public void execute(String sqlQuery){

        Statement statement;
        try {
            statement = connection.createStatement();
            statement.execute(sqlQuery);
        }
        catch (Exception e) {
            System.err.println("DerbyDB: Cannot execute");

        }
    }

    /**
     * @param sqlQuery sql query
     * @return returns a PreparedStatement object.
     */
    public PreparedStatement preparedStatement(String sqlQuery){

        try {
            return connection.prepareStatement(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
