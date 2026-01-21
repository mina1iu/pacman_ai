package model;

import graph.MazeGraph.MazeVertex;
import graph.MazeGraph;

import java.awt.*;
import java.util.Random;

/**
 * Represents Clyde, the orange ghost
 * Delay: 8 seconds (8000 ms)
 * Chase: If Euclidean distance to Pac-Man is >= 10, target Pac-Man.
 * If distance is < 10, target a random vertex.
 * Flee: Targets the southeast corner (model.width() - 3, model.height() - 3).
 */
public class Clyde extends Ghost {
    /**
     * A random number generator for Clyde's behavior.
     */
    private final Random random;

    /**
     * Constructs a new Clyde ghost by calling the Ghost super constructor with the parameter 'model', the game model.
     */
    public Clyde(GameModel model, Random random) {

        super(model, Color.ORANGE, 8000);
        this.random = random;
    }

    @Override
    protected MazeGraph.MazeVertex target() {
        if (state == Ghost.GhostState.FLEE) {
            return model.graph().closestTo(model.width() - 3, model.height() - 3);
        }

        //CHASE
        MazeVertex pacVertex = model.pacMann().nearestVertex();
        MazeVertex clydeVertex = this.nearestVertex();

        MazeGraph.IPair pacLoc = pacVertex.loc();
        MazeGraph.IPair clydeLoc = clydeVertex.loc();

        double distI = pacLoc.i() - clydeLoc.i();
        double distJ = pacLoc.j() - clydeLoc.j();
        double dist = Math.sqrt(distI * distI + distJ * distJ);


        if (dist >= 10) {
            return pacVertex;
        } else {
            int randX = random.nextInt(model.width());
            int randY = random.nextInt(model.width());
            return model.graph().closestTo(randX, randY);
        }


    }
}