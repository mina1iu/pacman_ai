# Pac-Man AI & Graph Pathfinding

A Java-based Pac-Man AI that autonomously navigates a maze using custom graph data structures and advanced pathfinding algorithms. This project features a priority-based state machine for the AI agent, custom implementations of a Hash Map and a Min-Heap Priority Queue, and a modified Dijkstra's algorithm for non-backtracking pathfinding.

## 🚀 Key Features

* **Autonomous Pac-Man AI (`PacMannAI`)**: A dynamic decision-making agent that evaluates its environment to shift priorities between collecting items (dots/pellets), fleeing from chasing ghosts, and pursuing fleeing ghosts.
* **Graph Representation (`MazeGraph`)**: Models the 2D game map as a directed, edge-weighted graph, dynamically generating vertices and edges using Breadth-First Search (BFS).
* **Advanced Pathfinding (`Pathfinding`)**: Implements Dijkstra's algorithm specifically modified to enforce a strict **non-backtracking** constraint, ensuring smooth and realistic movement.
* **Custom Data Structures**:
    * `MinPQueue`: A highly efficient binary min-heap priority queue supporting O(log N) priority updates, essential for optimal pathfinding.
    * `ProbingPacMap`: A custom Hash Map implementation utilizing linear probing for collision resolution, tombstone markers for deletions, and dynamic capacity resizing.
* **Performance Optimization**: Utilizes memoization (caching) to store previously computed shortest-path distances between graph nodes, drastically reducing redundant computations during gameplay.

## 📁 Project Structure

* **`PacMannAI.java`**: The core AI logic. Evaluates the state of the game (ghost distances, item locations) to determine the optimal next edge to traverse.
* **`MazeGraph.java`**: Converts the 2D tile grid into a navigable directed graph, calculating edge weights based on elevation changes (uphill/downhill).
* **`Pathfinding.java`**: Contains the logic for `shortestNonBacktrackingPath`, applying Dijkstra's algorithm with specialized constraints.
* **`MinPQueue.java`**: Array-backed binary min-heap priority queue used for managing the pathfinding frontier.
* **`ProbingPacMap.java` / `PacMap.java`**: Custom hash table implementation used for state tracking and indexing within the priority queue.
* **`Vertex.java` / `WeightedEdge.java`**: Interfaces defining the structure of the graph components.

## 🧠 AI State Logic

The AI determines its next move based on a strict priority hierarchy:
1.  **Fleeing**: If a *Chasing* ghost is within 10 units, prioritize reaching safety (finding the farthest vertex) or grabbing a nearby power pellet to turn the tables.
2.  **Chasing**: If a *Fleeing* ghost is within 15 units, aggressively route towards it to consume it.
3.  **Foraging**: If no immediate threats or targets are nearby, route to the nearest dot or power pellet.
