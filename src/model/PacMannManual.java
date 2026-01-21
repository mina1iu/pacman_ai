package model;

import graph.MazeGraph.MazeEdge;
import graph.MazeGraph.MazeVertex;
import graph.MazeGraph.Direction;

/**
 * Represents a Pac-Man actor controlled by manual player input.
 * This class translates player commands from the GameModel into movement.
 */
public class PacMannManual extends PacMann {
    /**
     * Constructs a new manually-controlled Pac-Man with the parameter 'g', for the GameModel this pacman belongs to
     */
    public PacMannManual(GameModel g) {
        super(g);
    }

    /**
     * Determines the next edge to traverse based on the player's last command.
     * The logic first tries to move in the direction of the most recent command
     * If the intended direction is not possible, it attempts to continue moving in the current direction of travel.
     * If neither is possible it returns null to stop at the vertex.
     */
    public MazeEdge nextEdge(){

        MazeVertex v = this.nearestVertex();

        Direction intendedDir = model.playerCommand();

        if (intendedDir != null) {
            MazeEdge edge = v.edgeInDirection(intendedDir);
            if (edge != null) {
                return edge;   // direction exists
            }
        }
        // continue in the direction
        MazeEdge current = this.location().edge();
        if (current != null) {
            Direction currDir = current.direction();
            MazeEdge fallback = v.edgeInDirection(currDir);
            if (fallback != null) {
                return fallback;  // continue straight
            }
        }

        // don't move to a new edge
        return null;
    }
}