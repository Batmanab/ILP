package uk.ac.ed.inf;

import uk.ac.ed.inf.database.Deliveries;
import uk.ac.ed.inf.database.DerbyDB;
import uk.ac.ed.inf.database.Order;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class integrates all the other classes.
 * When given a machine and port, it retrieves a menus object from the Menus class. DerbyDB object is created using jdbcString
 * The results are pushed into the Deliveries and Flightpath database.
 */

public class App {

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        Menus menus = new Menus("localhost", "9898");

        DerbyDB db = new DerbyDB("jdbc:derby://localhost:1527/derbyDB");
        //This retrieves order datewise from the derbyDB database.
        ResultSet deliveryDatesFromDB = db.select("SELECT DISTINCT DELIVERYDATE FROM ORDERS ORDER BY DELIVERYDATE");
        ArrayList<Date> deliveryDates = new ArrayList<>();
        Deliveries.makeTable(db);
        Flightpath.makeTable(db);
        while (deliveryDatesFromDB.next()) {
            deliveryDates.add(deliveryDatesFromDB.getDate("DeliveryDate"));
        }
        for (Date deliveryDate : deliveryDates) {
            ArrayList<Order> orders = Order.retrieveOrdersByDate(db, deliveryDate);

            //checks if order has >1 and <4 items
            orders.removeIf(order -> order.item.size() > 4 || order.item.size() < 1);

            Scheduler schedule = new Scheduler(deliveryDate, orders, menus );
            ArrayList<Deliveries> deliveries = new ArrayList<>();

            for(OrderPath path: schedule.getFinalOrders()){
                deliveries.add(new Deliveries(path.order.orderNo,
                        path.order.deliverTo,menus.getDeliveryCost(path.order.item.toArray(new String[0]))));
                System.out.println(path.order);
                System.out.println(path.moves );
            }
            Deliveries.runBatch(db, deliveries);
            Flightpath.runBatch(db,schedule.getAllPathPoints());
            System.out.println(schedule.getGeoJson());
        }

    }
}