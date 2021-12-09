package uk.ac.ed.inf;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 Class responsible for Conversions of What3Words into Long,Lat coordinates by taking values of the 3words and their
 corresponding coordinates and populating a hashmap with these values.
 //retrieves what3words data through (folder- ) and stores it in a map of word1.word2.word3 : LongLat obj
 There are 2 static methods of the class.
 */
public class What3Words {
    private static HashMap<String, LongLat> w3wMap = getWhat3WordsMap();

    /**
     * @param location : the what3words location in form of a string (e.g "army.grapes.monkeys")
     * @return : it's corresponding coordinates as a LongLat object
     */
    public static LongLat decode(String location){
        return w3wMap.get(location);
    }
    /**
     * @return : The final HashMap with - key: What3Words string (i.e., "apple.grape.orange" etc)
     * and - value: LongLat coordinates of the corresponding string extracted from the json.
     * This static method is called in the constructor
     * and the member variable w3wMap is assigned this HashMap<String,LongLat>
     */
    private static HashMap<String,LongLat> getWhat3WordsMap(){
        HashMap<String,LongLat> w3wMapTemp = new HashMap<>();
        try {
            List<Path> collect = Files.find(Paths.get("website/words"), 30, (path, fileAttributes) -> fileAttributes.isRegularFile()).collect(Collectors.toList());
            //extract data using gson from folder, create hashmap with final string from path and lat long values
            for (Path path: collect) {
                String jsonData = new String(Files.readAllBytes(path));
                JsonParser parser = new JsonParser();
                JsonObject object = (JsonObject) parser.parse(jsonData);
                JsonObject coordinates = (JsonObject) object.get("coordinates");
                String key = StringUtils.substringBetween(path.toString(), "website\\words\\", "\\details.json").replace('\\', '.');
                w3wMapTemp.put(key, new LongLat(coordinates.get("lng").getAsDouble(), coordinates.get("lat").getAsDouble()));
            }
        }
        catch (Exception e){
            System.err.println("What3Words.java error");

        }
        return w3wMapTemp;

    }
}
