package uk.ac.ed.inf;
import uk.ac.ed.inf.database.Order;

import java.awt.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//One function, takes initial position, days orders - return order id with maximum p/moves
//function takes order ID, performs the move, changes the initial position to new position after the move- push database

//moves function- takes starting position + ending position, returns number of moves and returns array of positions
//in flightpath

public class Scheduler {
    public Date date;
    private final ArrayList<OrderPath> scheduledOrders;

    public Scheduler(Date date, ArrayList<Order> allOrders, final Menus menus) {
        this.date = date;
        this.scheduledOrders = scheduleOrders(allOrders, menus);
    }


    //takes all orders, returns arraylist of scheduled orders
    private ArrayList<OrderPath> scheduleOrders(ArrayList<Order> Orders, Menus menus) {
        ArrayList<Order> allOrders = (ArrayList<Order>) Orders.clone();

        //initialize starting point as appleton tower
        LongLat currentStartingPoint = Landmarks.appletonTower;


        //remove currentOrder from allOrders arraylist
        //run while loop until allOrders is empty

        ArrayList<OrderPath> ordersByPM = new ArrayList<>() ;

        while (allOrders.size() >0 ){
            //get order with max P/Moves value
            OrderPath bestOrder = bestOrderPath(currentStartingPoint,allOrders,menus);
            //ordersByPM.put(singleOrderPath,orderPricePerMove);

            //adds the order with max P/Moves value to scheduled list
            //ordersByDay.add(maxOrder);
            //starting point for next iteration
            currentStartingPoint = What3Words.decode(bestOrder.order.deliverTo);
            //the order with max P/Moves value is removed and rest of the list is passed through function again

            allOrders.remove(bestOrder.order);
//            System.out.println(bestOrder.order);
//            System.out.println("Movess"+bestOrder.getMoves());
//            for(LongLat point: bestOrder.points){
//                System.out.println(point.latitude +","+ point.longitude + ",red");
//            }
            ordersByPM.add(bestOrder);


        }

        return ordersByPM;

    }
    public ArrayList<OrderPath> getScheduledOrders(){
        return this.scheduledOrders;
    }

    //returns order with max P/Moves value
    //will use orderpath class and getdeliveryprice
    private OrderPath bestOrderPath (LongLat startingPoint,ArrayList<Order> orders,Menus menus) {
        double maxPM = 0.0;
        OrderPath maxPMOrderPath = null;
        try{
            for (Order order: orders){
                OrderPath orderPath = new OrderPath(order, startingPoint,menus);
                int moves = orderPath.getMoves();
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

    private int moves(LongLat initialPoint, LongLat finalPoint){
    return 1;
    }

    public void priceByDistance(){

    }




    // takes datewise object, schedules orders chronologically and returns the correct schedule (list?) of order
    //by calculating price/distance and keeping in mind 1500 moves per day.
    public static void main(String[] args) {
        System.out.println(What3Words.decode("army.monks.grapes"));
    }


}
