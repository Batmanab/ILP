package uk.ac.ed.inf.database;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Order {
    public String orderNo;
    public Date deliveryDate;
    public String customer;
    public String deliverTo;
    public ArrayList<String> item = new ArrayList<>();

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }


    public static ArrayList<Order> retrieveOrdersByDate(DerbyDB db,Date deliveryDate) throws SQLException {
        ResultSet ordersByDate = db.selectByDate("SELECT distinct orderno, Customer, Deliverto from orders WHERE DELIVERYDATE =?", deliveryDate);
        ArrayList<Order> orders = new ArrayList<>();
        while (ordersByDate.next()) {
            ResultSet itemsByOrderNo = db.select("Select distinct item from orders natural join orderdetails where orderno='" + ordersByDate.getString("orderno")+"'");
            Order order = new Order();
            order.customer = ordersByDate.getString("customer");
            order.orderNo = ordersByDate.getString("orderNo");
            order.deliverTo = ordersByDate.getString("deliverTo");
            order.deliveryDate = deliveryDate;
            while (itemsByOrderNo.next()) {
                order.item.add(itemsByOrderNo.getString("item"));
            }
            //String[] arr = order.item.toArray(new String[0]);
            //System.out.println(menus.getDeliveryCost(arr));
            orders.add(order);
        }

        return orders;
    }
}
