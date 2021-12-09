package uk.ac.ed.inf;

import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.database.Deliveries;
import uk.ac.ed.inf.database.DerbyDB;

import java.io.CharArrayReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;



public class Flightpath {
    String orderNo;
    double fromLatitude,fromLongitude, toLatitude,toLongitude;
    int angle;

    public Flightpath(String orderNo, ArrayList<LongLat> coordinatePair) {
        this.orderNo = orderNo;
        this.fromLatitude = coordinatePair.get(0).latitude;
        this.fromLongitude = coordinatePair.get(0).longitude;
        this.toLatitude = coordinatePair.get(1).latitude;
        this.toLongitude = coordinatePair.get(1).longitude;
        this.angle = coordinatePair.get(0).getAngle(coordinatePair.get(1));
    }

    /**
     * @param derbyDB
     */
    public static void makeTable(DerbyDB derbyDB){
        String sqlDropQuery = "drop table flightpath";
        String sqlQuery = " CREATE TABLE flightpath( "
                + "orderNo char(8), "
                + "fromLongitude double, "
                +"fromLatitude double,"
                +"angle integer,"
                +"toLongitude double,"
                + "toLatitude double)";
        derbyDB.execute(sqlDropQuery);
        derbyDB.executeUpdate(sqlQuery);
    }

    /**
     * @param derbyDB
     * @param flightpaths
     */
    public static void runBatch(DerbyDB derbyDB, ArrayList<Flightpath> flightpaths){
        String sqlQuery = "insert into flightpath(orderNo,fromLongitude,fromLatitude,angle,toLongitude,toLatitude)" +
                " values (?,?,?,?,?,?)";
        PreparedStatement pst = derbyDB.preparedStatement(sqlQuery);
        for (Flightpath flightpath: flightpaths){
            try {
                pst.setCharacterStream(1,new CharArrayReader(flightpath.orderNo.toCharArray()));
                pst.setDouble(2,flightpath.fromLongitude);
                pst.setDouble(3,flightpath.fromLatitude);
                pst.setInt(4,flightpath.angle);
                pst.setDouble(5,flightpath.toLongitude);
                pst.setDouble(6,flightpath.toLatitude);
                pst.addBatch();
            } catch (SQLException e) {
                System.err.println("Error: Flightpath, runBatch. Insert failed");
            }
        }
        executeBatch(pst);
    }

    /**
     * @param preparedStatement
     */
    private static void executeBatch(PreparedStatement preparedStatement){
        try {
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            System.err.println("Deliveries, executeBatch. Execution failed");
        }

    }
}
