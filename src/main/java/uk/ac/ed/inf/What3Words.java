package uk.ac.ed.inf;

//retrieves what3words data through (folder- ) and stores it in a map of first.second.third : (lat,lng)

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class What3Words {
    private static HashMap<String, LongLat> w3wMap = getWhat3WordsMap();

    public static LongLat decode(String location){
        return w3wMap.get(location);
    }

    private static HashMap<String,LongLat> getWhat3WordsMap(){
        HashMap<String,LongLat> w3wMapTemp = new HashMap<>();
        try {

            List<Path> collect = Files.find(Paths.get("website/words"), 30, (path, fileAttributes) -> fileAttributes.isRegularFile()).collect(Collectors.toList());
            for (Path path: collect) {

                String jsonData = new String(Files.readAllBytes(path));

                JsonParser parser = new JsonParser();
                JsonObject object = (JsonObject) parser.parse(jsonData);

                JsonObject coordinates = (JsonObject) object.get("coordinates");

                String key = StringUtils.substringBetween(path.toString(), "website\\words\\", "\\details.json").replace('\\', '.');
                w3wMapTemp.put(key, new LongLat(coordinates.get("lng").getAsDouble(), coordinates.get("lat").getAsDouble()));


                //extract data using gson, create hashmap with final string from path and lat long values
            }
        }
        catch (Exception e){
            System.err.println("What3Words.java error");

        }
        return w3wMapTemp;

    }
}
