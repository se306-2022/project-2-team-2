package models;

public class Edge {
    private final Task from;
    private final Task to;
    private final int weight;

    public Edge(Task from, Task to, int weight) {
        this.from = from;
        this.to = to;   
        this.weight = weight;
    }

    public Task getFrom() {
        return from;
    }

    public Task getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }
}
