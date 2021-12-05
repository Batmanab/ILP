package uk.ac.ed.inf;
import java.lang.Math;
import java.util.ArrayList;

public class LongLat {
    double longitude; //this is x
    double latitude; //y
    //constructor
    public LongLat(double x, double y) {

        this.longitude = x;
        this.latitude = y;
    }
    //function returns true if position coordinates is confined within the allowed zone.
    public boolean isConfined() {
        if (longitude<-3.184319 && longitude>-3.192473) {
            if(latitude<55.946233 && latitude>55.94261 ) {
                return true;
            }
            return false;
        }
        return false;
    }
    //returns euclidean distance between the two points using Pythag helper function
    public double distanceTo(LongLat obj) {
        return Pythag(this.longitude, this.latitude, obj.longitude, obj.latitude);
    }

    //here, if distance is less than 0.00015 degrees then, the function returns true
    public boolean closeTo(LongLat obj) {
        if (this.distanceTo(obj) < 0.00015) {
            return true;

        }
        return false;
    }

    //when drone hovers, and doesn't change position- it returns the same coordinates.
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
            LongLat nextPosition = new LongLat(this.longitude,this.latitude);
            return nextPosition;
        }
        //new coordinates are found using trigonometry rations (angles are turned into radians)
        //logic is (new y, new x) = (old y + sin(angle)*0.0015, old x + cos(angle)*0.00015)
        double a = 0.00015 * Math.sin((angle * Math.PI)/180) + this.latitude; // new y is sin(angle) + initial latitude
        double b = 0.00015* Math.cos((angle * Math.PI)/180) + this.longitude;
        //System.out.println(a+ "" + b);

        LongLat nextPosition = new LongLat(b,a);
        return nextPosition;
    }
    public ArrayList<LongLat> getPathCoordinates(LongLat endPos, int angle){
        ArrayList<LongLat> pathCoordinates = new ArrayList<>();
        LongLat currentPos = new LongLat(this.longitude,this.latitude);

        do{
             currentPos = currentPos.nextPosition(angle);
             pathCoordinates.add(currentPos);

        }while(!currentPos.closeTo(endPos));

        return pathCoordinates;

    }

    public int getAngle(LongLat destCoordinate){
        return (int) Math.round(Math.toDegrees(Math.atan2(destCoordinate.latitude - this.latitude, destCoordinate.longitude-this.longitude)));
    }

    //helper method to calculate distance between two points
    public static double Pythag(double x1, double y1, double x2, double y2){

        double distance = Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
        return distance;
    }


}
