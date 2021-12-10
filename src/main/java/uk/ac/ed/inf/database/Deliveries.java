package uk.ac.ed.inf.database;
import java.io.CharArrayReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class deals with creating the table 'deliveries' in the derby database.
 */
public class Deliveries {
    String orderNo;
    String deliveredTo;
    int costInPence;

    public Deliveries(String orderNo, String deliveredTo, int costInPence) {
        this.orderNo = orderNo;
        this.deliveredTo = deliveredTo;
        this.costInPence = costInPence;
    }

    /**
     * @param derbyDB : the derbyDB object is passed, and functions from the DerbyDB class are used to operate the sql query.
     */
    public static void makeTable(DerbyDB derbyDB){
        String sqlDropQuery = "drop table deliveries";
        String sqlQuery = " CREATE TABLE deliveries( "
                + "orderNo char(8), "
                + "deliveredTo VARCHAR(19), "
                + "costInPence int)";
        derbyDB.execute(sqlDropQuery);
        derbyDB.executeUpdate(sqlQuery);
    }

    /**
     * @param derbyDB derbyDb object
     * @param deliveries has all the deliveries for a single day
     *                    it compiles the preparedstatement for each delivery, as a batch.
     */

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

    /**
     * @param preparedStatement takes the precompiled prepared statement and executes the batch at once.
     */
    private static void executeBatch(PreparedStatement preparedStatement){
        try {
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            System.err.println("Deliveries, executeBatch. Execution failed");
        }

    }
}
