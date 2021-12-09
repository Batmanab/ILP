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


/**
 * This class deals with parsing no-fly zones coordinate data from the geojson file
 * and the class Polygon2d is used to create an ArrayList of Polygon2d elements with all the no-fly zones.
 * There's two static methods.
 */
public class NoFlyZones {
    private static ArrayList<Polygon2D> allNoFlyZones = getNoFlyZones();

    /**
     * The function reads the geojson file from the folder, and then populates the arraylist with all no fly zones
     * each of Polygon2D type.
     * @return : An ArrayList of Polygon2D elements, made using the coordinates in the geojson file - no-fly-zones.geojson
     * and using the Polygon2D class. This contains all the polygon2d objects on the map,
     * which are No-Fly zones and are then assigned to member variable allNoFlyZones.
     */
    private static ArrayList<Polygon2D> getNoFlyZones() {
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
            //a polygon array called - polygon, is created
            Polygon[] polygons = new Polygon[0];
            try {
                //polygons array is populated
                polygons = Polygon.fromGeoJSON(geometry);
            } catch (ParseException e) {
                System.err.println("NoFlyZone: Parse exception");
            }
            //polygons array is passed into Polygon2D class to create a noflyzone Polygon of Polygon2D type.
            Polygon2D noFlyZonePolygon = Polygon2D.create(polygons);
            //all the zones are added to the Polygon2D Arraylist - allNoFlyZones
            allNoFlyZones.add(noFlyZonePolygon);
        }
        return allNoFlyZones;
    }

    /**
     * Function takes a LongLat object as a parameter, searches through the member variable
     * - ArrayList<Polygon2D> allNoFlyZones and returns whether the LongLat object lies inside any of the
     * no-fly zones. The '.contains' function of Polygon2D class is used.
     * @param xy is a LongLat object, passed to check if it exists in the no-fly zone while taking flight
     * @return a boolean value - whether the point (LongLat object) is inside the no fly zone.
     * If the point is inside the no-fly zone- it returns false
     * If the point is not in any of the no-fly zones- it returns true
     * Hence the name ' coordinateOutsideNoFlyZone ' is carefully selected - so it is in line with english interpretation
     * wherever it is called.
     */
    public static boolean coordinateOutsideNoFlyZone(LongLat xy){
        for (Polygon2D noFlyZone : allNoFlyZones){
            if (noFlyZone.contains(xy.latitude, xy.longitude)){
                return false;

            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println(coordinateOutsideNoFlyZone(new LongLat(-3.1883241425501816,55.944855495445886)));

    }


}
