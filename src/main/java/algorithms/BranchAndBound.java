package algorithms;

import models.Processor;
import models.State;
import models.Task;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

public class BranchAndBound {

    private State bestSchedule;

    private Graph graph;

    private int numProcessors;

    private int[] bLevels;

    public BranchAndBound(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
        this.bLevels = getBLevels(graph);
    }

    /**
     * Returns array of bLevels indexed by node.
     * bLevels are calculated by summing computation costs to exit node and taking the maximum.
     *
     * @param graph GraphStream graph object.
     * @return int[] array of bLevels index by node.
     */
    public int[] getBLevels(Graph graph) {
        int numTasks = graph.getNodeCount();
        int[] bLevels = new int[numTasks];

        for (int node = 0; node < numTasks; node++) {
            calculateBLevelsDFS(graph, bLevels, node);
        }

        return bLevels;
    }

    public int calculateBLevelsDFS(Graph graph, int[] bLevels, int node) {
        if (bLevels[node] != 0) {
            return bLevels[node];
        }

        // If there are no child nodes, we are at an exit task and bLevel is it's own weight.
        List<Edge> outEdges = graph.getNode(node).leavingEdges().collect(Collectors.toList());
        if (outEdges.isEmpty()) {
            bLevels[node] = (Integer) graph.getNode(node).getAttribute("Weight");
            return bLevels[node];
        }

        int maxLength = 0;
        for (Edge edge : outEdges) {
            Node childNode = edge.getNode1();
            int childNodeBLevel = calculateBLevelsDFS(graph, bLevels, childNode.getIndex());
            maxLength = Math.max(maxLength, childNodeBLevel);
        }

        bLevels[node] = maxLength + (Integer) graph.getNode(node).getAttribute("Weight");
        return bLevels[node];
    }

    public void dfs(State state) {
        if (state.getFreeNodes().isEmpty()) {
            this.bestSchedule = state; // Need to deep copy
            // Note: state, processor, task model might make this painful.
            return;
        }

        for (Node node : state.getFreeNodes()) {
            state.getFreeNodes().remove(node);
            for (Processor processor : state.getProcessors()) {

                // Might be redundant if we just want earliest possible processor time.
                // Free task queue contains only tasks we can possibily schedule.
                List<Edge> inEdges = node.enteringEdges().collect(Collectors.toList());
                for (Edge edge : inEdges) {
                    Node parentNode = edge.getNode0();
                    // Get max parent starting time.
                    // Include transfer time if in another processor.
                }

                int startTime = 0;
                int finishTime = startTime + Integer.parseInt(node.getAttribute("cost").toString());
                Task task = new Task(node, startTime, finishTime, processor.getId());
                processor.addTask(task);

                List<Edge> outEdges = node.leavingEdges().collect(Collectors.toList());
                for (Edge edge : outEdges) {
                    Node childNode = edge.getNode1();
                    // if parent nodes have been scheduled.
                    // Add child nodes to freeTasks.
                }

                dfs(state);

                processor.removeTask(task);
                for (Edge edge : outEdges) {
                    Node childNode = edge.getNode1();
                    // Remove child nodes from freeTasks.
                }
            }
            state.getFreeNodes().add(node);
        }
    }


}
