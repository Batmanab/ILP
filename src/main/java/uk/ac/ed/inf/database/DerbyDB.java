package uk.ac.ed.inf.database;

import java.sql.*;

public class DerbyDB {
    private Connection connection;

    public DerbyDB(String jdbcString) throws SQLException {
        connection = DriverManager.getConnection(jdbcString);
    }

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
}
