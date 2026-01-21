package graph;

import graph.PacMap;
import graph.ProbingPacMap;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * A min priority queue of distinct elements of type `KeyType` associated with (extrinsic) double
 * priorities. Supports updating the priorities of elements currently in the queue, and guarantees
 * O(log N) performance for all modifying operations, where N is the queue size.
 */
public class MinPQueue<KeyType> {

    /**
     * Pairs an element `key` with its associated priority `priority`.
     */
    private record Entry<KeyType>(KeyType key, double priority) { }

    /**
     * ArrayList representing a binary min-heap of element-priority pairs.  Satisfies
     * `heap.get(i).priority() >= heap.get((i-1)/2).priority()` for all `i` in `[1..heap.size())`.
     */
    private final ArrayList<Entry<KeyType>> heap;

    /**
     * Associates each element in the queue with its index in `heap`.  Satisfies
     * `heap.get(index.get(e)).key().equals(e)` if `e` is an element in the queue.
     * Only maps elements that are in the queue (`index.size() == heap.size()`).
     */
    private final PacMap<KeyType, Integer> index;


    /**
     * Create an empty queue.
     */
    public MinPQueue() {
        index = new ProbingPacMap<>();
        heap = new ArrayList<>();
    }

    /**
     * Return whether this queue contains no elements.
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Return the number of elements contained in this queue.
     */
    public int size() {
        return heap.size();
    }

    /**
     * Return an element associated with the smallest priority in this queue.  This is the same
     * element that would be removed by a call to `remove()` (assuming no mutations in between).
     * Throws a `NoSuchElementException` if this queue is empty.
     */
    public KeyType peek() {
        // Propagate exception from `List::getFirst()` if empty.
        return heap.getFirst().key();
    }

    /**
     * Return the minimum priority associated with an element in this queue.  Throws
     * a `NoSuchElementException` if this queue is empty.
     */
    public double minPriority() {
        return heap.getFirst().priority();
    }

    /**
     * Swap the `Entry`s at indices `i` and `j` in `heap`, updating `index` accordingly.  Requires
     * `0 <= i,j < heap.size()`.
     */
    private void swap(int i, int j) {
        Entry<KeyType> temp1 = heap.get(i);
        Entry<KeyType> temp2 = heap.get(j);

        heap.set(i, temp2);
        heap.set(j, temp1);

        index.put(temp1.key(), j);
        index.put(temp2.key(), i);

    }

    /**
     * Restores the min-heap property by moving the entry at index `i` down the heap
     * until both its children (if any) have priorities greater than or equal to its own.
     * This is done by comparing the entry at index `i` with its smallest child 'small'. The
     * variable 'small' is found by comparing the 'left' child and 'right' child to see which
     * is smaller. If 'small' has a smaller priority, the two entries are swapped,
     * and `bubbleDown` is called recursively on the index of 'small'.
     * Requires: 0 <= i < heap.size().
     */
    private void bubbleDown(int i) {
        int size = heap.size();
        int left = 2 * i + 1;

        if (left >= size) {
            return;
        }

        int small = left;
        int right = 2 * i + 2;

        if (right < size &&
                heap.get(right).priority() < heap.get(left).priority()) {
            small = right;
        }

        if (heap.get(i).priority() <= heap.get(small).priority()) {
            return;
        }
        swap(i, small);
        bubbleDown(small);
    }

    /**
     * Recursively restores the min-heap invariant by swapping the entry at index `i`
     * up until its parent's priority <= its own. This method recursively checks the parent and child priority,
     * swapping their locations in the heap if the child has lower priority than the parent entry and
     * continuing the recursive stack. The recursive call returns once the child priority is greater than the
     * parent or when 'i', the current index is equal to zero.
     * Requires: 0 <= i < heap.size().
     */
    private void bubbleUp(int i) {
        if (i == 0) {
            return;
        }
        int parentIndex = (i - 1) / 2;
        if (heap.get(i).priority() >= heap.get(parentIndex).priority()) {
            return;
        }
        swap(i, parentIndex);
        bubbleUp(parentIndex);
    }


    /**
     * Add element `key` to this queue, associated with priority `priority`.  Requires `key` is not
     * contained in this queue.
     */
    private void add(KeyType key, double priority) {
        Entry<KeyType> entry = new Entry<>(key, priority);
        heap.add(entry);
        int i = heap.size() - 1;
        index.put(key, i);

        bubbleUp(i);
    }

    /**
     * Change the priority associated with element `key` to `priority`.  Requires that `key` is
     * contained in this queue.
     */
    private void update(KeyType key, double priority) {
        assert index.containsKey(key);
        int i = index.get(key);
        Entry<KeyType> entry = new Entry<>(key, priority);
        double oldPriority = heap.get(i).priority();
        heap.set(i, entry);

        if (priority > oldPriority) {
            bubbleDown(i);
        }
        else if (priority < oldPriority) {
            bubbleUp(i);
        }
    }

    /**
     * If `key` is already contained in this queue, change its associated priority to `priority`.
     * Otherwise, add it to this queue with that priority.
     */
    public void addOrUpdate(KeyType key, double priority) {
        if (!index.containsKey(key)) {
            add(key, priority);
        } else {
            update(key, priority);
        }
    }

    /**
     * Remove and return the element associated with the smallest priority in this queue.  If
     * multiple elements are tied for the smallest priority, an arbitrary one will be removed.
     * Throws NoSuchElementException if this queue is empty.
     */

    public KeyType remove() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException();
        }

        KeyType min = heap.get(0).key();
        int lastIndex = heap.size()-1;

        if (lastIndex > 0) {
            swap (0, lastIndex);
        }

        heap.remove(lastIndex);
        index.remove(min);


        if (!heap.isEmpty()) {
            bubbleDown(0);
        }
        return min;
    }

}