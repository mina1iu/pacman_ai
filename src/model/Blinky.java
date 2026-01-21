package model;
import graph.MazeGraph.MazeVertex;
import java.awt.Color;

/**
 * Represents Blinky, the red ghost)
 *
 * Delay: 2 seconds (2000 ms)
 * Chase: Targets Pac-Man's nearestVertex().
 * Flee: Targets the northwest corner (2, 2).
 */
public class Blinky extends Ghost {
    /**
     * Constructs a new Blinky ghost by calling the Ghost super constructor with the parameter 'model', the game model.
     */
    public Blinky(GameModel model) {
        super(model, Color.RED, 2000);
    }
    @Override
    protected MazeVertex target(){
        if (state == GhostState.FLEE) {
            return model.graph().closestTo(2, 2);
        }
        else{
            return model.pacMann().nearestVertex();
        }
    }
}