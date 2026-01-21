package model;
import graph.MazeGraph.MazeEdge;
import graph.MazeGraph.MazeVertex;
import graph.Pathfinding;

import java.util.*;

import model.Ghost.GhostState;
import model.GameModel.Item;

/**
 * Represents an AI-controlled Pac-Man.
 */
public class PacMannAI extends PacMann {

    /**
     * The distance at which the AI will
     * consider a chasing ghost a risk and run away.
     */
    private static final int fleeDist = 10;

    /**
     * The distance at which the AI will change its path
     * to chase a fleeing ghost.
     */
    private static final int chaseDist = 15;

    /**
     * Stores the full path the AI is currently following, used by guidancePath()
     * for visualization.
     */
    private List<MazeEdge> currentPath;

    /**
     * Constructs an AI Pac-Man in this GameModel 'model'
     */
    public PacMannAI(GameModel model) {
        super(model);
        this.currentPath = Collections.emptyList();
    }

    /**
     * Calculates and returns the next edge for the AI to traverse based on its priority-based state logic.
     */
    @Override
    public MazeEdge nextEdge() {
        MazeVertex myLocation = nearestVertex();

        if (currentPath != null && !currentPath.isEmpty()) {
            MazeEdge nextStep = currentPath.get(0);

            if (nextStep.tail().equals(myLocation)) {
                currentPath.remove(0);
                return nextStep;
            }
        }

        MazeVertex targetVertex = null;
        MazeVertex nearestChasingGhost = findNearestGhost(myLocation, GhostState.CHASE);
        if (nearestChasingGhost != null) {
            double dist = getDistance(myLocation, nearestChasingGhost);
            if (dist <= fleeDist) {
                if (dist <= 5) {
                    targetVertex = findNearestItem(myLocation, Item.PELLET);
                }
                if (targetVertex == null) {
                    targetVertex = findFarthestVertex(myLocation, nearestChasingGhost);
                }
            }
        }

        if (targetVertex == null) {
            MazeVertex nearestFleeingGhost = findNearestGhost(myLocation, GhostState.FLEE);
            if (nearestFleeingGhost != null) {
                double dist = getDistance(myLocation, nearestFleeingGhost);
                if (dist <= chaseDist) {
                    targetVertex = nearestFleeingGhost;
                }
            }
        }

        if (targetVertex == null) {
            targetVertex = findNearestItem(myLocation, Item.DOT);
            if (targetVertex == null) {
                targetVertex = findNearestItem(myLocation, Item.PELLET);
            }
        }

        if (targetVertex == null) {
            this.currentPath = Collections.emptyList();
            return null;
        }

        List<MazeEdge> path = Pathfinding.shortestNonBacktrackingPath(
                myLocation,
                targetVertex,
                null
        );

        if (path == null || path.isEmpty()) {
            this.currentPath = Collections.emptyList();
            return null;
        } else {
            this.currentPath = new ArrayList<>(path);
            return this.currentPath.remove(0);
        }
    }

    /**
     * Returns the path the AI is currently on.
     */
    @Override
    public List<MazeEdge> guidancePath() {
        return (currentPath != null) ? currentPath : Collections.emptyList();
    }


    /**
     * Finds and returns the nearest item from the MazeVertex 'from' to dot or pellet to given location
     */
    private MazeVertex findNearestItem(MazeVertex from, Item item) {
        MazeVertex target = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (MazeVertex v : model.graph().vertices()) {
            if (model.itemAt(v) == item) {
                double dist = getDistance(from, v);
                if (dist > 0 && dist < minDistance) {
                    minDistance = dist;
                    target = v;
                }
            }
        }
        return target;
    }

    /**
     * Finds the nearest ghost in a state 'FLEE' or 'CHASE' from the MazeVertex 'from'.
     */
    private MazeVertex findNearestGhost(MazeVertex from, GhostState state) {
        MazeVertex target = null;
        double minDistance = Double.POSITIVE_INFINITY;

        List<Ghost> ghosts = new ArrayList<>();
        for (Actor a : model.actors()) {
            if (a instanceof Ghost g && g.state() == state) {
                ghosts.add(g);
            }
        }

        for (Ghost g : ghosts) {
            MazeVertex ghostLocation = g.nearestVertex();
            double dist = getDistance(from, ghostLocation);
            if (dist > 0 && dist < minDistance) {
                minDistance = dist;
                target = ghostLocation;
            }
        }
        return target;
    }

    /**
     * Finds the safest vertex from the Pac-Man's current location. The variable
     * 'pacManLocation' is the AI's current location.
     * 'ghostLocation' is the location of the threatening ghost.
     * This method returns the vertex that maximizes the path distance from the ghost.
     */
    private MazeVertex findFarthestVertex(MazeVertex pacManLocation, MazeVertex ghostLocation) {
        MazeVertex safestNeighbor = null;
        double maxDistFromGhost = Double.NEGATIVE_INFINITY;

        for (MazeEdge edge : pacManLocation.outgoingEdges()) {
            MazeVertex neighbor = edge.head();


            double distFromGhost = getDistance(neighbor, ghostLocation);

            if (distFromGhost > maxDistFromGhost) {
                maxDistFromGhost = distFromGhost;
                safestNeighbor = neighbor;
            }
        }
        return safestNeighbor;
    }

    /**
     * Helper method to calculate the shortest path distance between two vertices.
     */
    private double getDistance(MazeVertex src, MazeVertex dst) {
        if (src.equals(dst)) {
            return 0.0;
        }

        Pair<MazeVertex, MazeVertex> key = new Pair<>(src, dst);
        if (distanceC.containsKey(key)) {
            return distanceC.get(key);
        }

        List<MazeEdge> path = Pathfinding.shortestNonBacktrackingPath(src, dst, null);

        double distance;
        if (path == null) {
            distance = Double.POSITIVE_INFINITY;
        } else {
            distance = 0.0;
            for (MazeEdge edge : path) {
                distance += edge.weight();
            }
        }

        distanceC.put(key, distance);

        return distance;
    }



    /**
     * Maps (src, dst) pair to the shortest distance.
     */
    private final Map<Pair<MazeVertex, MazeVertex>, Double> distanceC = new HashMap<>();

    /**
     * Immutable pair class used as a key for the 'distanceC'.
     */
    private static class Pair<L, R> {
        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return left.equals(pair.left) && right.equals(pair.right);
        }

        @Override
        public int hashCode() {
            return 31 * left.hashCode() + right.hashCode();
        }
    }}