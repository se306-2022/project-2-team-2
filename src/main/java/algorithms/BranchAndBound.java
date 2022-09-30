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
    public BranchAndBound() {}

    public State greedy(Graph graph, int numProcessors) {
        int numTasks = graph.getNodeCount();

        Queue<Node> initialFreeNodes = new LinkedList<>();
        for (int i = 0; i < numTasks; i++) {
            Node node = graph.getNode(i);
            if (node.getInDegree() == 0) {
                initialFreeNodes.add(node);
            }
        }

        State initialState = new State(initialFreeNodes, numProcessors);

        dfs(initialState);

        return bestSchedule;
    }

    public void dfs(State state) {
        if (state.getFreeNodes().isEmpty()) {
            this.bestSchedule = state;
            return;
        }

        for (Node node : state.getFreeNodes()) {
            state.getFreeNodes().remove(node);
            for (Processor processor : state.getProcessors()) {

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
