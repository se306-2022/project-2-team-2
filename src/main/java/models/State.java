package models;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * State represents partial solution.
 */
public class State {

    private List<Processor> processors = new ArrayList<>();
    private Queue<Node> freeNodes;

    public State(Queue<Node> freeNodes, int numProcessors) {
        this.freeNodes = freeNodes;
        for (int id = 0; id < numProcessors; id++) {
            processors.add(new Processor(id));
        }
    }

    public Queue<Node> getFreeNodes() {
        return freeNodes;
    }

    public List<Processor> getProcessors() {
        return processors;
    }

    public int getCurrentTime() {
        int currentTime = 0;
        for (Processor processor : processors) {
            currentTime = Math.max(currentTime, processor.getCurrentTime());
        }
        return currentTime;
    }
}
