package uk.ac.ed.inf;

import java.util.ArrayList;


/**
 * This is a class, which is used as a data type in Menus.java
 * This allows us to access the data inside menus, of menus.json file as an arrayList
 */
public class ShopMenu {
    String name;
    String location;
    ArrayList<MenuItem> menu = new ArrayList<>();
}
