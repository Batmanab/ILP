package uk.ac.ed.inf;

import uk.ac.ed.inf.database.Order;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class OrderPath {
    Order order;
    ArrayList<LongLat> points = new ArrayList<>();
    LongLat startPos;
    LongLat endPos;
    ArrayList<LongLat> locations = new ArrayList<>();


    public OrderPath(Order order, LongLat startPos, final Menus menuObj) {

        this.order = order;
        this.startPos = startPos;
        this.endPos = What3Words.decode(order.deliverTo);
        for (String item : order.item){
            if (!locations.contains(What3Words.decode(menuObj.getLocationByItem(item)))){
                locations.add(What3Words.decode(menuObj.getLocationByItem(item)));
                //System.out.println(menuObj.getLocationByItem(item));
            }
        }
        locations.add(endPos);
    }
    //moves between startPos and endPos
    public int getMoves() {
        int moves = 0;
        int angle; LongLat nextPos;
        LongLat currentPos = this.startPos;
        for (LongLat location : this.locations){
            LongLat originalStartPos = currentPos;
            ArrayList<LongLat> pathPoints = new ArrayList<>();
            pathPoints.add(currentPos);
            int pathMoves=0;
            do{
                angle = currentPos.getAngle(location);
                nextPos = currentPos.nextPosition(angle);
                System.out.println(location.latitude+","+location.longitude+"   "+nextPos.latitude+","+nextPos.longitude);
                if (NoFlyZones.coordinateOutsideNoFlyZone(nextPos)) {
//                    System.out.println("**distance**"+ currentPos.distanceTo(nextPos));
                    currentPos = nextPos;
                    pathMoves++;
                    pathPoints.add(currentPos);
                    System.out.println("IF");
                }
                else{
                    System.out.println("ELSE");
                    pathPoints.clear();
                    pathPoints.add(originalStartPos);
                    if(originalStartPos.distanceTo(Landmarks.checkpoint1) < originalStartPos.distanceTo(Landmarks.checkpoint2)){
                        pathMoves = (int) (location.distanceTo(Landmarks.checkpoint1)/0.00015) +
                                (int) (originalStartPos.distanceTo(Landmarks.checkpoint1)/0.00015);
                        currentPos = location;
                        pathPoints.add(Landmarks.checkpoint1);
                    }
                    else{
                        pathMoves = (int) (location.distanceTo(Landmarks.checkpoint2)/0.00015)  +
                                (int) (originalStartPos.distanceTo(Landmarks.checkpoint2)/0.00015);
                        currentPos = location;
                        pathPoints.add(Landmarks.checkpoint2);
                    }
                }
                System.out.println("****pathpoints size  " +pathPoints.size());

            }while (!location.closeTo(currentPos));
            System.out.println("****end****");
            points.addAll(pathPoints);
            moves = moves+ pathMoves;
        }


        return moves + locations.size();
    }

}
