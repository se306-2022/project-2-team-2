package algorithms;

import models.Schedule;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class BranchAndBound {
    private Graph graph;
    private int numProcessors;
    private int[] bLevels;
    private LinkedList<Node> freeTasks;
    private Schedule bestSchedule;
    private Schedule currentSchedule;
    private int bestFinishTime = Integer.MAX_VALUE;
    private int freeTasksRemainingTime = 0;

    private int[] dependents;

    public BranchAndBound(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
    }

    public Schedule run() {
        this.bLevels = Utils.calculateBLevels(graph);
        this.dependents = calculateDependents(graph);
        this.freeTasks = getInitialFreeTasks(graph);
        this.currentSchedule = new Schedule(new ArrayList<>());

        recurse(freeTasks);

        return bestSchedule;
    }

    public LinkedList<Node> getInitialFreeTasks(Graph graph) {
        LinkedList<Node> freeTasks = new LinkedList<>();
        for (int i = 0; i < graph.getNodeCount(); i++) {
            if (graph.getNode(i).getInDegree() == 0) {
                freeTasks.add(graph.getNode(i));
            }
        }

        return freeTasks;
    }

    public int[] calculateDependents(Graph graph) {
        int[] dependents = new int[graph.getNodeCount()];
        for (int i = 0; i < graph.getNodeCount(); i++) {
            dependents[i] = graph.getNode(i).getInDegree();
        }
        return dependents;
    }

    /**
     * Calculate the longest critical path amongst the freeTasks. This will be the lower bound.
     * @param freeTasks
     * @return
     */
    public int longestCriticalPath(List<Node> freeTasks) {
        int longestCriticalPath = 0;
        for (Node task : freeTasks) {
            int taskIndex = graph.getNode(task.getId()).getIndex();
            longestCriticalPath = Math.max(longestCriticalPath, bLevels[taskIndex]);
        }
        return longestCriticalPath;
    }

    public void recurse(LinkedList<Node> freeTasks) {
        if (freeTasks.isEmpty()) {
            bestSchedule = currentSchedule;
            return;
        }

        // TODO: Check if we have already come across an equivalent schedule?

        // TODO: Check if we can complete tasks in fixed data task order FTO?

        // Calculate specific metrics at the current search state.
        int earliestFinishTime = currentSchedule.getEarliestFinishTime();
        int latestFinishTime = currentSchedule.getLatestFinishTime();
        int loadBalancedTime = freeTasksRemainingTime / numProcessors;
        int longestCriticalPath = longestCriticalPath(freeTasks);

        // Sort free tasks by bLevel priority.
        freeTasks.sort(Comparator.comparing(n -> bLevels[n.getIndex()]));

        // Go through free tasks.
        HashSet<Integer> visited = new HashSet<>();
        for (Node node : freeTasks) {
            Node task = freeTasks.remove();
            int taskIndex = graph.getNode(task.getId()).getIndex();

            // Check if we have already visited task.
            if (visited.contains(taskIndex)) {
                freeTasks.add(task);
                continue;
            }

            // TODO: add equivalent nodes to visited tasks?

            // Ignore current schedule if it cannot become the potential optimal.
            if (!isPotential(earliestFinishTime, latestFinishTime, loadBalancedTime, longestCriticalPath)) {
                freeTasks.add(task);
                continue;
            }

            freeTasksRemainingTime -= graph.getNode(taskIndex).getAttribute("Weight", Double.class).intValue();

            // For child nodes check if they have no more pending dependents, then add to freeTasks queue.
            List<Edge> childEdges = graph.getNode(taskIndex).leavingEdges().collect(Collectors.toList());
            for (Edge edge : childEdges) {
                Node child = edge.getNode1();
                dependents[child.getIndex()]--;
                if (dependents[child.getIndex()] == 0) {
                    freeTasks.add(child);
                }
            }

            // Communication costs between processors.
            int maxDataArrivalTime = 0;
            int secondMaxDataArrivalTime = 0;
            int maxDataArrivalProcessor = 0;
            List<Edge> parentEdges = graph.getNode(taskIndex).enteringEdges().collect(Collectors.toList());
            for (Edge edge : parentEdges) {
                Node parent = edge.getNode0();
                int parentStartTime = currentSchedule.getTaskStartTime(parent);
                int parentWeight = parent.getAttribute("Weight", Double.class).intValue();
                int communicationCost = edge.getAttribute("Weight", Double.class).intValue();

                int dataArrivalTime = parentStartTime + parentWeight + communicationCost;
                if (dataArrivalTime >= maxDataArrivalTime) {

                }
            }

            // Recurse

            // Backtrack
        }
    }

    private boolean isPotential(int earliestFinishTime, int latestFinishTime, int loadBalancedTime, int longestCriticalPath) {
        boolean loadBalancingConstraint = earliestFinishTime + loadBalancedTime >= bestFinishTime;
        boolean criticalPathConstraint = earliestFinishTime + longestCriticalPath >= bestFinishTime;
        boolean latestFinishTimeConstraint = latestFinishTime >= bestFinishTime;
        return !loadBalancingConstraint && !criticalPathConstraint && !latestFinishTimeConstraint;
    }

    public int[] getbLevels() {
        return bLevels;
    }
}
