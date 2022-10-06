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
    private int remainingTime = 0;

    private int[] dependents;

    public BranchAndBound(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
    }

    public Schedule run() {
        this.bLevels = Utils.calculateBLevels(graph);
        this.dependents = getDependents(graph);
        this.freeTasks = getFreeTasks(graph);
        this.currentSchedule = new Schedule(new ArrayList<>());

        recurse(freeTasks);
        return bestSchedule;
    }

    public LinkedList<Node> getFreeTasks(Graph graph) {
        LinkedList<Node> freeTasks = new LinkedList<>();
        for (int i = 0; i < graph.getNodeCount(); i++) {
            if (graph.getNode(i).getInDegree() == 0) {
                freeTasks.add(graph.getNode(i));
            }
        }

        return freeTasks;
    }

    public int[] getDependents(Graph graph) {
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

        // Check if we have already come across an equivalent schedule.

        // Check if we can complete tasks in fixed data task order FTO.

        int earliestFinishTime = currentSchedule.getEarliestFinishTime();
        int latestFinishTime = currentSchedule.getLatestFinishTime();
        int loadBalancedTime = remainingTime / numProcessors;
        int longestCriticalPath = longestCriticalPath(freeTasks);

        HashSet<Integer> seen = new HashSet<>();
        for (Node node : freeTasks) {
            Node task = freeTasks.remove();
            int taskIndex = graph.getNode(task.getId()).getIndex();

            if (seen.contains(taskIndex)) {
                freeTasks.add(task);
                continue;
            }

            // Add equivalent nodes to candidate tasks.

            // Ignore current schedule if it cannot become the potential optimal.
            if (!isPotential(earliestFinishTime, latestFinishTime, loadBalancedTime, longestCriticalPath)) {
                freeTasks.add(task);
                continue;
            }


            remainingTime -= graph.getNode(taskIndex).getAttribute("Weight", Double.class).intValue();

            // For child nodes check if they have no more pending dependents, then add to freeTasks queue.
            List<Edge> childEdges = graph.getNode(taskIndex).leavingEdges().collect(Collectors.toList());
            for (Edge edge : childEdges) {
                Node child = edge.getNode1();
                int childIndex = graph.getNode(child.getId()).getIndex();
                dependents[childIndex]--;
                if (dependents[childIndex] == 0) {
                    freeTasks.add(child);
                }
            }

            // Communication costs between processors

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
