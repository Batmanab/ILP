package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;

public class Menus {
    private String machine;
    private String port;
    private static final HttpClient client = HttpClient.newHttpClient();

    public Menus (String machine, String port){
        this.machine = machine;
        this.port = port;
        shopMenus = getShopMenus();

        for (ShopMenu shopMenu : shopMenus) {
            for (MenuItem menuItem : shopMenu.menu) {
                itemPriceMap.put(menuItem.item, menuItem.pence);
                itemLocationMap.put(menuItem.item, shopMenu.location);
                itemShopMap.put(menuItem.item,shopMenu.name);

            }
        }
    }

    private HashMap<String, Integer> itemPriceMap = new HashMap<>() ;
    private HashMap<String, String> itemLocationMap= new HashMap<>() ;
    private HashMap<String, String> itemShopMap= new HashMap<>() ;

    private ArrayList<ShopMenu> getShopMenus()  {

        String urlString = "http://" + machine + ":" + port + "/menus/menus.json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();
        try{
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            //Arraylist of data type ShopMenu is created which parses data from the website database using Gson
            Type listType = new TypeToken<ArrayList<ShopMenu>>() {}.getType();
            ArrayList<ShopMenu> menus = new Gson().fromJson(response.body(), listType);

            return menus;

        }else {
            return new ArrayList<ShopMenu>() ;
        }} catch (Exception e){
            return new ArrayList<ShopMenu>() ;
        }


    }

    /**
     *
     * @param items variable number of strings- names of items that have been ordered
     * @return total price of all times + delivery charge of 50p
     * @throws IOException
     * @throws InterruptedException
     */
    public int getDeliveryCost(String...items) throws IOException, InterruptedException {

        //program proceeds when status code is 200
        if (shopMenus.size()!=0) {
            int finalPence =0;

            for (String item : items) {
                finalPence+= itemPriceMap.get(item);

            }


            //delivery charge of 50 pence is added.
            return finalPence+50;

        } else {
            System.out.println("Connection failed");
            return 0;
        }

    }

}
