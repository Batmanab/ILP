package uk.ac.ed.inf;
import uk.ac.ed.inf.database.Order;
import java.util.ArrayList;

/**
 * The OrderPath class calculates the number of moves and the pathpoints for - One particular order in a day.
 */
public class OrderPath {
    int returnMoves;
    ArrayList<LongLat> returnPath = new ArrayList<>();
    Order order;
    ArrayList<LongLat> points = new ArrayList<>();
    LongLat startPos;
    LongLat endPos;
    ArrayList<LongLat> locations = new ArrayList<>();
    int moves ;

    /**
     * @param order the order object
     * @param startPos starting position of the order
     * @param menuObj a final Menus object
     *                This constructor assigns value for each repetition ( i.e., for each order, out of a day's orders)
     *                by changing the starting position at each step.
     */
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

    /**
     * Similar logic to setPath() function is used. It is used to assign the return path, from any point to Appleton Tower.
     * If no-fly zones are met, it travels through the nearest checkpoint.
     */
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
     * This function adds coordinates to the 'points' attribute after each valid move in the direction of the restaurant/delivery location.
     * It checks if a move is valid by checking if it falls inside the no-fly zone and also if it's inside the allowed confined zone.
     * It continues to add points by going along the angle to the location (could be restaurant, or delivery)
     * If a no-fly zone is met, it transcribes back to starting location, and takes the route of checkpoints.

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
                //checks if move is valid
                if (NoFlyZones.coordinateOutsideNoFlyZone(nextPos) && nextPos.isConfined()) {

                    currentPos = nextPos;
                    pathMoves++;
                    pathPoints.add(currentPos);
                }
                //if not valid, it starts from the initial location again and travels through checkpoint.
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
            //adds the pathPoints to the class attribute.
            points.addAll(pathPoints);
            moves = moves+ pathMoves;
            }
        //As the drone hovering over a restaurant, and also hovering over delivery location counts as 1 move each, it's the same as
        //counting the number of restaurants and the delivery location for a particular order and add it to the total number of moves.
        this.moves = moves + locations.size();
    }

}
