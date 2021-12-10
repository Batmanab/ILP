package uk.ac.ed.inf;
import uk.ac.ed.inf.database.Order;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * This class uses the OrderPath class, to iteratively select the best order out of a number of orders on a particular date and
 * get it's corresponding flightpaths. This is iteratively continued until all the orders of the day are complete.
 * As soon as one order is complete, it uses the same algorithm again, but with a new starting point (the last delivery location)
 * The Scheduler Class is the main class which schedules and ranks orders for a day, to maximize the revenue generated for the least
 * number of moves taken.
 */
public class Scheduler {
    public Date date;
    int noOfMoves = 1500;
    public int movesUsed;
    private final ArrayList<OrderPath> finalOrders;

    public Scheduler(Date date, ArrayList<Order> allOrders, final Menus menus) {
        this.date = date;
        this.finalOrders = makeFinalOrdersSchedule(allOrders,menus);
    }
    /**
     * @param Orders ArrayList<Order> which is a list of order objects for a particular date.
     * @param menus the menus object, which contains the properties of the Menus class. It's constant
     *              for a constant menu. Any changes in the menu.json file will reflect changes here.
     * @return ArrayList<OrderPath> which is the chronological order (without the return path to Appleton Tower)
     * of all the orders and their coordinate paths, in decreasing order of the Price/Move value.
     */
    private ArrayList<OrderPath> scheduleOrders(ArrayList<Order> Orders, Menus menus) {
        ArrayList<Order> allOrders = (ArrayList<Order>) Orders.clone();

        LongLat currentStartingPoint = Landmarks.appletonTower;

        ArrayList<OrderPath> ordersByPM = new ArrayList<>() ;
        while (allOrders.size() >0 ){

            OrderPath bestOrder = bestOrderPath(currentStartingPoint,allOrders,menus);

            currentStartingPoint = What3Words.decode(bestOrder.order.deliverTo);
            //the order with max P/Moves value is removed and rest of the list is passed through function again
            allOrders.remove(bestOrder.order);

            ordersByPM.add(bestOrder);
        }
        return ordersByPM;
    }

    /**
     * It is used to pass the finalOrders arraylist.
     */
    public ArrayList<OrderPath> getFinalOrders(){
        return this.finalOrders;
    }

    /**
     * @param startingPoint a longlat starting point
     * @param orders takes all the orders of the day
     * @param menus menus object
     * @return returns the OrderPath object corresponding to the object, with the maximum Price/Moves value.
     */
    //returns order with max P/Moves value
    //will use orderpath class and getdeliveryprice
    private OrderPath bestOrderPath (LongLat startingPoint,ArrayList<Order> orders,Menus menus) {
        double maxPM = 0.0;
        OrderPath maxPMOrderPath = null;
        try{
            for (Order order: orders){
                OrderPath orderPath = new OrderPath(order, startingPoint,menus);
                int moves = orderPath.moves;
                int deliveryCost = menus.getDeliveryCost(orderPath.order.item.toArray(new String[0]));
                double pricePerMove = (double) deliveryCost/moves;
                if (pricePerMove>maxPM){
                    maxPM = pricePerMove;
                    maxPMOrderPath = orderPath;
                }
            }
        }
        catch (Exception E){ System.err.println("Scheduler, getPriceMove");

        }
        return maxPMOrderPath;
    }

    /**
     * @param allOrders ArrayList<Order> which consists of all the orders in a chronological order.
     * @param menus a menus object, to access some properties.
     * @return the final schedule for the day, including the return path from the final order to Appleton Tower.
     * This keeps in check that we don't cross 1500 moves per day.
     */
    //final order schedule for a day- including return moves to appletontower
   public ArrayList<OrderPath> makeFinalOrdersSchedule(ArrayList<Order> allOrders, Menus menus){
        ArrayList<OrderPath> scheduledOrders = scheduleOrders(allOrders, menus);
        ArrayList<OrderPath> finalOrderList = new ArrayList<>();
       for (OrderPath order : scheduledOrders){

           int orderMoves = order.moves + order.returnMoves;
           if (orderMoves< this.noOfMoves){
               finalOrderList.add(order);
               this.noOfMoves = this.noOfMoves - order.moves;
           }
       }
       int movesUsed =  1500 - noOfMoves + finalOrderList.get(finalOrderList.size()-1).returnMoves;
       setMovesUsed(movesUsed);
       return finalOrderList;
   }

    /**
     * @param moves takes the number of moves used, and assigns it to the class attribute.
     *              This is broken into a function as it is called iteratively inside a loop.
     */
   private void setMovesUsed(int moves){
        this.movesUsed = moves;
   }

    // takes datewise object, schedules orders chronologically and returns the correct schedule (list?) of order
    //by calculating price/distance and keeping in mind 1500 moves per day.
    public static void main(String[] args) {
        System.out.println(What3Words.decode("army.monks.grapes"));
    }

    /**
     * @return ArrayList<Flightpath>, which is used to push to the database under the flightpath table.
     * It is because, flightpath table requires data in a particular format, where the coordinates have to be paired
     * as 'from' an 'to' for each move. The flighpathsList ArrayList is in a format we can use easily to meet the database
     * requirements.
     */
    public ArrayList<Flightpath> getAllPathPoints(){
        ArrayList<Flightpath> flightpathsList = new ArrayList<>();
        for (OrderPath order : this.finalOrders){
            LongLat startCoordinate = order.points.get(0);
            ArrayList<LongLat> coordinatePairs = new ArrayList<>();
            for (LongLat orderPoint :order.points.subList(1,order.points.size()-1)){
                coordinatePairs.add(startCoordinate);
                coordinatePairs.add(orderPoint);
                flightpathsList.add(new Flightpath(order.order.orderNo,coordinatePairs));
                coordinatePairs.clear();
                startCoordinate = orderPoint;
            }
            if (this.finalOrders.indexOf(order) == this.finalOrders.size()-1){
                startCoordinate = order.returnPath.get(0);
                for (LongLat orderPoint : order.returnPath){
                    coordinatePairs.add(startCoordinate);
                    coordinatePairs.add(orderPoint);
                    flightpathsList.add(new Flightpath(order.order.orderNo,coordinatePairs));
                    coordinatePairs.clear();
                    startCoordinate = orderPoint;
                }
            }
        }
        return flightpathsList;

    }

    /**
     * @return the string, containing all coordinate pairs for a particular day (moves wise) to be used to write onto
     * the geojson file as a linestring.
     */
    public String getAsLineString(){
        ArrayList<LongLat> scheduledCoordinates = new ArrayList<>();
       for (OrderPath orderPath : this.finalOrders){
           scheduledCoordinates.addAll(orderPath.points);
       }
       scheduledCoordinates.addAll(this.finalOrders.get(this.finalOrders.size()-1).returnPath);
       return String.valueOf(scheduledCoordinates);
    }

}
