package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.ac.ed.inf.database.Deliveries;
import uk.ac.ed.inf.database.DerbyDB;
import uk.ac.ed.inf.database.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class integrates all the other classes.
 * When given a machine and port, it retrieves a menus object from the Menus class. DerbyDB object is created using jdbcString
 * The results are pushed into the Deliveries and Flightpath database.
 */

public class App {

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        Menus menus = new Menus("localhost", "9898");
        DerbyDB db = new DerbyDB("jdbc:derby://localhost:1527/derbyDB");
        //This retrieves delivery dates from orders
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
            //pushing to database table called deliveries, using Deliveries class.
            Deliveries.runBatch(db, deliveries);
            //pushing to database table called flightpath, using Flightpath class.
            Flightpath.runBatch(db,schedule.getAllPathPoints());
            System.out.println(schedule.getAsLineString());
            makeGeoJson(schedule.getAsLineString());
            break;
        }

    }
    public static void makeGeoJson(String lineStringCoordinates){
        try {
            Path collect = Paths.get("Testing/all.geojson");
            String jsonData = new String(Files.readAllBytes(collect));

            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(jsonData);
            JsonArray features = object.get("features").getAsJsonArray();

            String finalString = "{ \"type\": \"Feature\", \"geometry\": { \"type\": \"LineString\", \"coordinates\": [" +
                    lineStringCoordinates + "] }, \"properties\": { \"stroke\": \"#808080\"}}";

            //System.out.println(finalString);
            features.add(parser.parse(finalString));
            //System.out.println(features);
            object.add("features", features);

            System.out.println(object);

        } catch (IOException e) {
            System.err.println("geoJson file not found");
        }


    }
}