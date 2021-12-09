package uk.ac.ed.inf;
import uk.ac.ed.inf.database.Order;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//One function, takes initial position, days orders - return order id with maximum p/moves
//function takes order ID, performs the move, changes the initial position to new position after the move- push database

//moves function- takes starting position + ending position, returns number of moves and returns array of positions
//in flightpath

/**
 *
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
     * @param Orders
     * @param menus
     * @return
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
    public ArrayList<OrderPath> getFinalOrders(){
        return this.finalOrders;
    }
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

   private void setMovesUsed(int moves){
        this.movesUsed = moves;
   }

    // takes datewise object, schedules orders chronologically and returns the correct schedule (list?) of order
    //by calculating price/distance and keeping in mind 1500 moves per day.
    public static void main(String[] args) {
        System.out.println(What3Words.decode("army.monks.grapes"));
    }

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
    public String getGeoJson(){
       ArrayList<Flightpath> flightpathsList = getAllPathPoints();
       StringBuilder geoJsonBuilder= new StringBuilder("[" + flightpathsList.get(0).fromLongitude + "," +
               flightpathsList.get(0).fromLatitude + "]");
       for (Flightpath flightpath : flightpathsList){
           geoJsonBuilder.append(",[").append(flightpath.toLongitude).append(",").append(flightpath.toLatitude).append("]");
       }
       return geoJsonBuilder.toString();
    }

}
