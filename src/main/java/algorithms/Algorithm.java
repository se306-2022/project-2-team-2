package algorithms;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import models.Schedule;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public abstract class Algorithm {
    protected Graph graph;
    protected Schedule bestSchedule;
    protected int statesSearched = 0;
    protected int bestFinishTime;
    protected volatile boolean isDone = false;

    public void setDone() {
        isDone = true;
    }

    /**
     * Add a tasks child nodes to free tasks queue, if their dependent nodes have been scheduled.
     * @param freeTasks queue of free tasks ready to be scheduled.
     * @param node GraphStream Node, the target node and it's children we want to schedule.
     * @return true/false if a child of the current node was added.
     */
    protected boolean addPotentialChildNodesToFreeTasks(LinkedList<Integer> freeTasks, Node node, int[] dependents) {
        boolean isChildAdded = false;
        List<Node> childNodes = node.leavingEdges().map(Edge::getNode1).collect(Collectors.toList());
        for (Node child : childNodes) {
            dependents[child.getIndex()]--;
            if (dependents[child.getIndex()] == 0) {
                freeTasks.add(child.getIndex());
                isChildAdded = true;
            }
        }
        return isChildAdded;
    }

    /**
     * Calculates minimum possible start time, taking into account target processor and communication costs.
     * @param node GraphStream Node object, the task node we want to schedule.
     * @param currentSchedule the current partial schedule we have.
     * @param processor the target processor we are trying to schedule the task onto.
     * @param currentStartTime the target processor finish time, minimum time calculated can't be less than this.
     * @return earliest possible start time.
     */
    protected int minimumStartTime(Node node, Schedule currentSchedule, int processor, int currentStartTime) {
        List<Edge> parentEdges = node.enteringEdges().collect(Collectors.toList());

        int potentialStartTime = 0;
        for (Edge parentEdge : parentEdges) {
            Node parent = parentEdge.getNode0();
            int parentStartTime = currentSchedule.getTaskStartTime(parent);
            int parentWeight = parent.getAttribute("Weight", Double.class).intValue();
            int parentFinishTime = parentStartTime + parentWeight;
            if (processor != currentSchedule.getTaskProcessor(parent)) {
                parentFinishTime += parentEdge.getAttribute("Weight", Double.class).intValue();
            }

            potentialStartTime = Math.max(potentialStartTime, parentFinishTime);
        }

        return Math.max(potentialStartTime, currentStartTime);
    }

    public abstract void run();

    public Schedule getBestSchedule() {
        return bestSchedule;
    }

    public int getStatesSearched() {
        return statesSearched;
    }

    public void setStatesSearched() {
        this.statesSearched++;
    }

    public int getBestFinishTime() {
        return bestFinishTime;
    }

}
