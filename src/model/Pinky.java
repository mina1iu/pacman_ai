package model;

import graph.MazeGraph;

import java.awt.*;

import graph.MazeGraph.MazeVertex;

import java.awt.Color;

/**
 * Represents Pinky, the pink ghost
 *
 * Delay: 4 seconds (4000 ms)
 * Chase: Targets the vertex closestTo 3 units in front of Pac-Man.
 * Flee: Targets the northeast corner (model.width() - 3, 2).
 */
public class Pinky extends Ghost {

    /**
     * Constructs a new Pinky ghost by calling the Ghost super constructor with the parameter 'model', the game model.
     */
    public Pinky(GameModel model) {
        super(model, Color.PINK, 4000);
    }

    @Override
    protected MazeGraph.MazeVertex target() {
        if (state == GhostState.FLEE) {
            return model.graph().closestTo(model.width() - 3, 2);
        }
        // chase
        PacMann pacMann = model.pacMann();
        MazeGraph.IPair pacLoc = pacMann.nearestVertex().loc();
        MazeGraph.Direction pacDir = pacMann.location().edge().direction();

        int i = pacLoc.i();
        int j = pacLoc.j();

        switch (pacDir) { // checking direction
            case UP:
                j -= 3;
                break;
            case DOWN:
                j += 3;
                break;
            case LEFT:
                i -= 3;
                break;
            case RIGHT:
                i += 3;
                break;
        }
        return model.graph().closestTo(i, j);
    }
}