package uk.ac.ed.inf;
import uk.ac.ed.inf.database.Order;
import java.util.ArrayList;

public class OrderPath {
    int returnMoves;
    ArrayList<LongLat> returnPath = new ArrayList<>();
    Order order;
    ArrayList<LongLat> points = new ArrayList<>();
    LongLat startPos;
    LongLat endPos;
    ArrayList<LongLat> locations = new ArrayList<>();
    int moves ;

    public OrderPath(Order order, LongLat startPos, final Menus menuObj) {

        this.order = order;
        this.startPos = startPos;
        this.endPos = What3Words.decode(order.deliverTo);
        for (String item : order.item){
            if (!locations.contains(What3Words.decode(menuObj.getLocationByItem(item)))){
                locations.add(What3Words.decode(menuObj.getLocationByItem(item)));
            }
        }
        locations.add(endPos);
        setPath();
        setReturnPath();
    }
    //moves between startPos and endPos
    private void setReturnPath(){
        LongLat currentPos = this.endPos,nextPos;
        do{
            nextPos = currentPos.nextPosition(currentPos.getAngle(Landmarks.appletonTower));
            if(NoFlyZones.coordinateOutsideNoFlyZone(nextPos) && nextPos.isConfined()){
                currentPos = nextPos;
                this.returnMoves ++;
                this.returnPath.add(currentPos);
            }
            else {
                this.returnPath.clear();
                if (this.endPos.distanceTo(Landmarks.checkpoint1)<this.endPos.distanceTo(Landmarks.checkpoint2)){
                    this.returnMoves =  (int) (this.endPos.distanceTo(Landmarks.checkpoint1)/0.00015) +
                            (int) (Landmarks.appletonTower.distanceTo(Landmarks.checkpoint1)/0.00015);
                    this.returnPath.add(Landmarks.checkpoint1);
                }
                else{
                    this.returnMoves =  (int) (this.endPos.distanceTo(Landmarks.checkpoint2)/0.00015) +
                            (int) (Landmarks.appletonTower.distanceTo(Landmarks.checkpoint2)/0.00015);
                    this.returnPath.add(Landmarks.checkpoint2);
                }
            }
        }while(!currentPos.closeTo(Landmarks.appletonTower));
        this.returnPath.add(Landmarks.appletonTower);
    }

    /**
     *
     */
    private void setPath() {
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
                if (NoFlyZones.coordinateOutsideNoFlyZone(nextPos)) {

                    currentPos = nextPos;
                    pathMoves++;
                    pathPoints.add(currentPos);
                }
                else{
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
            }while (!location.closeTo(currentPos));
            points.addAll(pathPoints);
            moves = moves+ pathMoves;
            }
        this.moves = moves + locations.size();
    }

}
