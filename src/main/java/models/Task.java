package models;

import java.util.List;

public class Task {

    public int id;
    private List<Edge> inEdges;
    private List<Edge> outEdges;
    private int weight;
    private int bottomLevel;

    public Task (int id, List<Edge> inEdges, List<Edge> outEdges, int weight, int bottomLevel) {
        this.id = id;
        this.inEdges = inEdges;
        this.outEdges = outEdges;
        this.weight = weight;
        this.bottomLevel = bottomLevel;
    }

    public void setBottomLevelComputation(int bottomLevelComputation) {
        this.bottomLevel = bottomLevelComputation;
    }

    public List<Edge> getInEdges() {
        return inEdges;
    }

    public List<Edge> getOutEdges() {
        return outEdges;
    }

    public int getWeight() {
        return weight;
    }
}
