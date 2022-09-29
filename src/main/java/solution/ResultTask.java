package solution;

import org.graphstream.graph.Node;

public class Task {
    Node node;
    int startTime;
    int finishTime;
    int processor;

    public Task(Node node, int startTime, int finishTime, int processor) {
        this.node = node;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.processor = processor;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public Node getNode() {
        return node;
    }

    public int getProcessor() {
        return processor;
    }
}
