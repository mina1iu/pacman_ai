package model;
import graph.MazeGraph;
import java.awt.*;

/**
 * Represents Inky, the cyan ghost
 *
 * Delay: 6 seconds (6000 ms)
 * Chase: Targets a vertex T such that Pac-Man's vertex (P) is the
 * midpoint between Blinky's vertex (B) and T. (T = 2*P - B)
 * Flee: Targets the southwest corner (2, model.height() - 3).
 */
public class Inky extends Ghost{
    /**
     * Constructs a new Inky ghost by calling the Ghost super constructor with the parameter 'model', the game model.
     */
    public Inky(GameModel model) {
        super(model, Color.CYAN, 6000);
    }

    @Override
    protected MazeGraph.MazeVertex target() {
        if (state == GhostState.FLEE) {
            // Flee to the "southwest" corner at (2, model.height() - 3)
            return model.graph().closestTo(2, model.height() - 3);
        }

        //CHASE
        MazeGraph.IPair pacLoc = model.pacMann().nearestVertex().loc();
        MazeGraph.IPair blinkyLoc = model.blinky().nearestVertex().loc();

        // midpoint calc
        int targetI = (2 * pacLoc.i()) - blinkyLoc.i();
        int targetJ = (2 * pacLoc.j()) - blinkyLoc.j();

        return model.graph().closestTo(targetI, targetJ);
    }

}