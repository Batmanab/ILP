package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.ac.ed.inf.database.Deliveries;
import uk.ac.ed.inf.database.DerbyDB;
import uk.ac.ed.inf.database.Order;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This class integrates all the other classes.
 * When given a machine and port, it retrieves a menus object from the Menus class. DerbyDB object is created using jdbcString
 * The results are pushed into the Deliveries and Flightpath database.
 */

public class App {

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        for (String arg : args) {
            System.out.println(arg);
        }
        return ;
//        long startTime = System.nanoTime();
//        Menus menus = new Menus("localhost", args[3]);
//        DerbyDB db = new DerbyDB("jdbc:derby://localhost:"+ args[4] +"/derbyDB");
//        //This retrieves delivery dates from orders
//        ResultSet deliveryDatesFromDB = db.select("SELECT DISTINCT DELIVERYDATE FROM ORDERS ORDER BY DELIVERYDATE");
//        ArrayList<Date> deliveryDates = new ArrayList<>();
//        Deliveries.makeTable(db);
//        Flightpath.makeTable(db);
//        while (deliveryDatesFromDB.next()) {
//            deliveryDates.add(deliveryDatesFromDB.getDate("DeliveryDate"));
//        }
//        for (Date deliveryDate : deliveryDates) {
//            if(!deliveryDate.toString().equals(args[2] + "-" + args[1] + "-" + args[0])){
//                continue;
//            }
//            ArrayList<Order> orders = Order.retrieveOrdersByDate(db, deliveryDate);
//
//            //checks if order has >1 and <4 items
//            orders.removeIf(order -> order.item.size() > 4 || order.item.size() < 1);
//
//            Scheduler schedule = new Scheduler(deliveryDate, orders, menus );
//            ArrayList<Deliveries> deliveries = new ArrayList<>();
//
//            for(OrderPath path: schedule.getFinalOrders()){
//                deliveries.add(new Deliveries(path.order.orderNo,
//                        path.order.deliverTo,menus.getDeliveryCost(path.order.item.toArray(new String[0]))));
//                System.out.println(path.order);
//                System.out.println(path.moves);
//                System.out.println(path.points);
//
//            }
//            //pushing to database table called deliveries, using Deliveries class.
//            Deliveries.runBatch(db, deliveries);
//            //pushing to database table called flightpath, using Flightpath class.
//            Flightpath.runBatch(db,schedule.getAllPathPoints());
//            System.out.println(schedule.getAsLineString());
//            System.out.println(deliveryDate.toLocalDate());
//            //System.out.println(deliveryDate.toString());
//            if((deliveryDate.toLocalDate().getDayOfMonth() == deliveryDate.toLocalDate().getMonthValue()) &&
//                    (deliveryDate.toLocalDate().getYear() == 2022)){
//                makeGeoJson(schedule.getAsLineString(),deliveryDate);
//            }
//
//        }
//        System.out.println("Time elapsed (in seconds) : "+TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

    }

    /**
     * @param lineStringCoordinates string value of coordinates, of a particular day, suitable for geojson.
     * @param date the date for which the flightpath is to be plotted.
     */
    public static void makeGeoJson(String lineStringCoordinates, Date date){
        try {
            Path collect = Paths.get("Testing/all.geojson");
            String jsonData = new String(Files.readAllBytes(collect));
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(jsonData);
            JsonArray features = object.get("features").getAsJsonArray();

            String finalString = "{ \"type\": \"Feature\", \"geometry\": { \"type\": \"LineString\", \"coordinates\":" +
                    lineStringCoordinates + "}, \"properties\": { \"stroke\": \"#808080\"}}";

            //System.out.println(finalString);
            features.add(parser.parse(finalString));
            //System.out.println(features);
            object.add("features", features);

            String fileName = "drone-"+ date.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +".geojson";
            FileWriter newFile = new FileWriter("Output/" + fileName);
            newFile.write(String.valueOf(object));
            newFile.flush();
            newFile.close();

        } catch (IOException e) {
            System.err.println("geoJson file not found");
        }


    }
}