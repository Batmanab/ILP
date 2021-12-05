package uk.ac.ed.inf;

import uk.ac.ed.inf.database.DerbyDB;
import uk.ac.ed.inf.database.Order;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
/*
First ->
Create date wise and order wise data structure from this

Second -> use File stream stack overflow
LatLng <-> What3 Module
 */
public class App {
    //    public static void main( String[] args ) throws SQLException {
//        System.out.println( "Hello World!" );
//        DerbyDB db = new DerbyDB("jdbc:derby://localhost:1527/derbyDB");
//        ResultSet results = db.select("select * from orders natural join orderdetails fetch first 2 rows only") ;
//        ArrayList<Order> orders = new ArrayList<>();
//        while (results.next()) {
//            Order order = new Order();
//            order.customer = results.getString("customer");
//            order.orderNo = results.getString("orderNo");
//            order.deliverTo = results.getString("deliverTo");
//            order.deliveryDate = results.getDate("deliveryDate");
//            order.item = results.getString("item");
//
//            orders.add(order);
//        }
//        System.out.println(orders.get(1).toString());
//
//    }

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        Menus menus = new Menus("localhost", "9898");

        DerbyDB db = new DerbyDB("jdbc:derby://localhost:1527/derbyDB");
        ResultSet deliveryDatesFromDB = db.select("SELECT DISTINCT DELIVERYDATE FROM ORDERS ORDER BY DELIVERYDATE");
        ArrayList<Date> deliveryDates = new ArrayList<>();
        while (deliveryDatesFromDB.next()) {
            deliveryDates.add(deliveryDatesFromDB.getDate("DeliveryDate"));
        }
        for (Date deliveryDate : deliveryDates) {
            ArrayList<Order> orders = Order.retrieveOrdersByDate(db, deliveryDate);
            //OrderPath dayOrder = new OrderPath(orders.get(0), Landmarks.appletonTower, menus);
            Scheduler schedule = new Scheduler(deliveryDate, orders, menus );
            for(OrderPath path: schedule.getScheduledOrders()){
                System.out.println(path.order);
                System.out.println(path.getMoves());
//                for (LongLat point : path.points){
//                    System.out.println(point.latitude +","+ point.longitude + ",red");
//                }
                System.out.println("*****************");
            }
            break;

        }
    }
}