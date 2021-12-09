package uk.ac.ed.inf.database;

import java.io.CharArrayReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Deliveries {
    String orderNo;
    String deliveredTo;
    int costInPence;

    public Deliveries(String orderNo, String deliveredTo, int costInPence) {
        this.orderNo = orderNo;
        this.deliveredTo = deliveredTo;
        this.costInPence = costInPence;
    }

    public static void makeTable(DerbyDB derbyDB){
        String sqlDropQuery = "drop table deliveries";
        String sqlQuery = " CREATE TABLE deliveries( "
                + "orderNo char(8), "
                + "deliveredTo VARCHAR(19), "
                + "costInPence int)";
        derbyDB.execute(sqlDropQuery);
        derbyDB.executeUpdate(sqlQuery);
    }
    public static void runBatch(DerbyDB derbyDB, ArrayList<Deliveries> deliveries){
        String sqlQuery = "insert into deliveries(orderNo,deliveredTo,costInPence) values (?,?,?)";
        PreparedStatement pst = derbyDB.preparedStatement(sqlQuery);
        for (Deliveries delivery: deliveries){
            try {
                pst.setCharacterStream(1,new CharArrayReader(delivery.orderNo.toCharArray()));
                pst.setString(2,delivery.deliveredTo);
                pst.setInt(3,delivery.costInPence);
                pst.addBatch();
            } catch (SQLException e) {
                System.err.println("Error: Deliveries, addBatch. Insert failed");
            }
        }
        executeBatch(pst);
    }
    private static void executeBatch(PreparedStatement preparedStatement){
        try {
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            System.err.println("Deliveries, executeBatch. Execution failed");
        }

    }
}
