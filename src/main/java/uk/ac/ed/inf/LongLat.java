package uk.ac.ed.inf;
import java.lang.Math;
import java.util.ArrayList;

/**
 *This class deals with all calculations and operations on coordinates.
 * The LongLat object has attributes longitude and latitude.
 */
public class LongLat {
    double longitude; //this is x
    double latitude; //y
    //constructor
    public LongLat(double x, double y) {

        this.longitude = x;
        this.latitude = y;
    }

    /**
     * @return the longitude,latitude as a string. This is used
     * in making entries into the final GeoJson file.
     */
    public String toString(){
        return "[" + this.longitude + "," + this.latitude +"]";
    }

    /**
     * @return boolean value: checks whether a given LongLat object
     * is inside the allowed zone.
     */
    public boolean isConfined() {
        if (longitude<-3.184319 && longitude>-3.192473) {
            return latitude < 55.946233 && latitude > 55.94261;
        }
        return false;
    }

    /**
     * @param obj Takes a LongLat object: to which the distance is to be calculated.
     * @return euclidean distance between the two LongLat objects using Pythag helper function
     */

    public double distanceTo(LongLat obj) {
        return Pythag(this.longitude, this.latitude, obj.longitude, obj.latitude);
    }

    /**
     * @param x1 longitude 1  (LongLat obj1)
     * @param y1 latitude 1   (LongLat obj1)
     * @param x2 longitude 2 (LongLat obj2)
     * @param y2 latitude 2  (LongLat obj2)
     * @return euclidean distance between two pair of points.
     * This is used as a helper function.
     */
    public static double Pythag(double x1, double y1, double x2, double y2){

        return Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
    }

    /**
     * @param obj Takes a LongLat object
     * @return boolean value : if the 2nd LongLat object is in the proximity
     * (distance of less than 0.00015 degrees) of the 1st object.
     */
    public boolean closeTo(LongLat obj) {
        return this.distanceTo(obj) < 0.00015;
    }

    /**
     * @param angle : angle between the initial position, and the next position.
     * @return returns the next position along the direction of the angle given.
     * Each move is 0.00015 degrees, and next position is calculated accordingly.
     * if drone hovers (junk value of -999) it returns the same position.
     */
    public LongLat nextPosition (int angle){

        //rounding off to the nearest multiple of  10
        int remainderAngle = angle%10;
        if(remainderAngle>=5){
            angle = angle + (10 - remainderAngle);
        }
        else{
            angle = angle - remainderAngle;
        }

        if (angle == -999){
            return new LongLat(this.longitude, this.latitude);
        }
        //new coordinates are found using trigonometry rations (angles are turned into radians)
        //logic is (new y, new x) = (old y + sin(angle)*0.0015, old x + cos(angle)*0.00015)
        double a = 0.00015 * Math.sin((angle * Math.PI)/180) + this.latitude; // new y is sin(angle) + initial latitude
        double b = 0.00015* Math.cos((angle * Math.PI)/180) + this.longitude;
        //System.out.println(a+ "" + b);

        return new LongLat(b,a);
    }

    /**
     * @param endPos the second LongLat object
     * @param angle angle between the two LongLat objects
     * @return ArrayList<LongLat> containing all the LongLat object points between the two LongLat objects,
     * along the direction of the angle, (separated by a 'move' i.e., nextPosition function is used)
     */
    public ArrayList<LongLat> getPathCoordinates(LongLat endPos, int angle){
        ArrayList<LongLat> pathCoordinates = new ArrayList<>();
        LongLat currentPos = new LongLat(this.longitude,this.latitude);

        do{
             currentPos = currentPos.nextPosition(angle);
             pathCoordinates.add(currentPos);

        }while(!currentPos.closeTo(endPos));

        return pathCoordinates;

    }

    /**
     * @param destCoordinate takes a second LongLat object
     * @return the angle between the two LongLat objects using the java math class.
     */
    public int getAngle(LongLat destCoordinate){
        return (int) Math.round(Math.toDegrees(Math.atan2(destCoordinate.latitude - this.latitude, destCoordinate.longitude-this.longitude)));
    }





}
