package uk.ac.ed.inf;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import org.apache.lucene.geo.Polygon;
import org.apache.lucene.geo.Polygon2D;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;


public class NoFlyZones {
    private static ArrayList<Polygon2D> allNoFlyZones = getNoFlyZones();



    private static ArrayList<Polygon2D> getNoFlyZones() {
        //JSONArray a = (JSONArray) parser.parse(new FileReader("D:\\Year 3\\website\\buildings\\no-fly-zones.geojson"));

        String jsonData = null;
        try {
            jsonData = new String(Files.readAllBytes(Paths.get("D:\\Year 3\\website\\buildings\\no-fly-zones.geojson")));
        } catch (IOException e) {
            System.err.println("NoFlyZone File not found");
        }

        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(jsonData);

        JsonArray features = (JsonArray) object.get("features");
        ArrayList<Polygon2D> allNoFlyZones = new ArrayList<>();
        for (JsonElement feature : features){
            String geometry = feature.getAsJsonObject().get("geometry").getAsJsonObject().toString();
            Polygon[] polygons = new Polygon[0];
            try {
                polygons = Polygon.fromGeoJSON(geometry);
            } catch (ParseException e) {
                System.err.println("NoFlyZone: Parse exception");
            }
            //System.out.println(Arrays.toString(polygons));
            Polygon2D noFlyZonePolygon = Polygon2D.create(polygons);
            allNoFlyZones.add(noFlyZonePolygon);
//            System.out.println(polygons.length);
//            System.out.println(noFlyZonePolygon.contains(55.9452577,-3.1899375));
        }
        return allNoFlyZones;
    }
    public static boolean coordinateOutsideNoFlyZone(LongLat xy){
        for (Polygon2D noFlyZone : allNoFlyZones){
            if (noFlyZone.contains(xy.latitude, xy.longitude)){
                return false;

            }
        }
        return true;
    }

    public static boolean coordinatesOutsideNoFlyZone(ArrayList<LongLat> pathCoordinates){

        for (LongLat pathCoordinate: pathCoordinates){
            if (!coordinateOutsideNoFlyZone(pathCoordinate)){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println(coordinateOutsideNoFlyZone(new LongLat(55.944855495445886,-3.1883241425501816)));

    }


}
