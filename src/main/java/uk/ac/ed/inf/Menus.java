package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.io.*;
import java.io.IOException;

public class Menus {
    private String machine;
    private String port;
    private static final HttpClient client = HttpClient.newHttpClient();

    public Menus (String machine, String port){
        this.machine = machine;
        this.port = port;
    }

    /**
    // This function takes two arguments- the names which are in the online order (input) and list of all item names (itemPrice),
     whenever the input string matches the name of the item in the menu database, it adds it's price to 'fin'
     @param input is list of items in the order
     @param itemsPrice is list of items in the database
     function returns the total price of all the items. It's a helper function.
    */
    private static int crossCheck(ArrayList<String> input, ArrayList<MenuArray> itemsPrice) {
        int fin = 0;

        for(int i = 0; i< input.size();i++) {

            for (int k = 0; k < itemsPrice.size(); k++ ) {
                if(input.get(i).equals(itemsPrice.get(k).item)) {

                    fin = fin + itemsPrice.get(k).pence;
                    break;
                }
            }
        }


        return fin;
    }

    /**
     *
     * @param args variable number of strings- names of items that have been ordered
     * @return total price of all times + delivery charge of 50p
     * @throws IOException
     * @throws InterruptedException
     */
    public int getDeliveryCost(String...args) throws IOException, InterruptedException {
        //request
        String urlString = "http://" + machine + ":" + port + "/menus/menus.json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();

        //response
        ArrayList<String> n = new ArrayList<>();


        for(String arg: args){
            n.add(arg);
        }



        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //program proceeds when status code is 200
        if (response.statusCode() == 200) {
            int finalPence =0;

            //Arraylist of data type MenusDB is created which parses data from the website database using Gson
            Type listType = new TypeToken<ArrayList<MenusDB>>() {}.getType();
            ArrayList<MenusDB> menus = new Gson().fromJson(response.body(), listType);

            //this creates an ArrayList of data type MenuArray called itemsPrice with just the names of the items across the entire database-menus.json and prices
            ArrayList<MenuArray> itemsPrice = new ArrayList<MenuArray>();
            for(int i =0; i< menus.size(); i++){
                for(int k=0; k < menus.get(i).menu.size() ; k++){
                    itemsPrice.add(menus.get(i).menu.get(k));
                }
            }



            finalPence = finalPence + crossCheck(n, itemsPrice);

            //delivery charge of 50 pence is added.
            return finalPence+50;

        } else {
            System.out.println("Connection failed");
            return 0;
        }

    }

}
