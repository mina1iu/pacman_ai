package graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import graph.MinPQueue;
import java.util.LinkedList;

public class Pathfinding {

    /**
     * Represents a path ending at `lastEdge.end()` along with its length (distance).
     */
    record PathEnd<E extends WeightedEdge<?>>(double distance, E lastEdge) { }

    /**
     * Returns a list of `E` edges comprising the shortest non-backtracking simple path from vertex
     * `src` to vertex `dst`. A non-backtracking path never contains two consecutive edges between
     * the same two vertices (e.g., v -> w -> v). As a part of this requirement, the first edge in
     * the returned path cannot back-track `previousEdge` (when `previousEdge` is not null). If
     * there is not a non-backtracking path from `src` to `dst`, then null is returned. Requires
     * that if `previousEdge != null` then `previousEdge.head().equals(src)`.
     */
    public static <V extends Vertex<E>, E extends WeightedEdge<V>> List<E> shortestNonBacktrackingPath(
            V src, V dst, E previousEdge) {

        Map<V, PathEnd<E>> paths = pathInfo(src, previousEdge);
        return paths.containsKey(dst) ? pathTo(paths, src, dst) : null;
    }

    /**
     * Returns a map that associates each vertex reachable from `src` along a non-backtracking path
     * with a `PathEnd` object. The `PathEnd` object summarizes relevant information about the
     * shortest non-backtracking simple path from `src` to that vertex. A non-backtracking path
     * never contains two consecutive edges between the same two vertices (e.g., v -> w -> v). As a
     * part of this requirement, the first edge in the returned path cannot backtrack `previousEdge`
     * (when `previousEdge` is not null). Requires that if `previousEdge != null` then
     * `previousEdge.head().equals(src)`.
     */
    static <V extends Vertex<E>, E extends WeightedEdge<V>> Map<V, PathEnd<E>> pathInfo(V src,
                                                                                        E previousEdge) {

        Map<V, PathEnd<E>> pathInfo = new HashMap<>();
        MinPQueue<V> frontier = new MinPQueue<>();


        pathInfo.put(src, new PathEnd<>(0.0, null));
        frontier.addOrUpdate(src, 0.0);

        // dijkstra
        while (!frontier.isEmpty()) {
            V u = frontier.remove();
            PathEnd<E> uPath = pathInfo.get(u);
            double distToU = uPath.distance();
            E edgeToU = uPath.lastEdge();

            V predecessor = null;
            if (u.equals(src)) {
                if (previousEdge != null) {
                    predecessor = previousEdge.tail();
                }
            } else if (edgeToU != null) {
                predecessor = edgeToU.tail();
            }


            for (E edge : u.outgoingEdges()) {
                V v = edge.head();

                // nonbacking constraint
                if (predecessor != null && v.equals(predecessor)) {
                    continue;
                }

                // 5. Relaxation step (modified to use addOrUpdate)
                double newDist = distToU + edge.weight();
                PathEnd<E> vPath = pathInfo.get(v);

                // undiscovered
                if (vPath == null || newDist < vPath.distance()) {
                    pathInfo.put(v, new PathEnd<>(newDist, edge));
                    // add it to queue
                    frontier.addOrUpdate(v, newDist);
                }
            }
        }
        return pathInfo;
    }

    /**
     * Return the list of edges in the shortest non-backtracking path from `src` to `dst`, as
     * summarized by the given `pathInfo` map. Requires `pathInfo` conforms to the specification as
     * documented by the `pathInfo` method; it must contain the last edge on the shortest
     * non-backtracking simple paths from `src` to all reachable vertices.
     */
    static <V, E extends WeightedEdge<V>> List<E> pathTo(Map<V, PathEnd<E>> pathInfo, V src, V dst) {
        LinkedList<E> edges = new LinkedList<>();
        V currentVertex = dst;

        while (!currentVertex.equals(src)) {
            PathEnd<E> pathEnd = pathInfo.get(currentVertex);
            if (pathEnd == null) {
                break;
            }

            E edge = pathEnd.lastEdge();
            if (edge == null) {
                break;
            }

            edges.addFirst(edge);
            currentVertex = edge.tail();
        }
        return edges;
    }
}