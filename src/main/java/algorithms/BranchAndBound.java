package algorithms;

import models.Schedule;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import utils.GraphUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BranchAndBound {
    private final Graph graph;
    private final int numProcessors;

    private int[] bLevels;
    private int[] dependents;
    private List<List<Integer>> equivalentTasksList;

    private Schedule bestSchedule;
    private Schedule currentSchedule;

    private int fastestTime = Integer.MAX_VALUE;
    private int tasksRemainingTime;
    private boolean previousIsChildAdded = false;

    public BranchAndBound(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
    }

    public void run() {
        this.bLevels = GraphUtils.calculateBLevels(graph);
        this.dependents = GraphUtils.calculateDependents(graph);
        this.tasksRemainingTime = GraphUtils.getTasksTotalTime(graph);
        this.equivalentTasksList = GraphUtils.getEquivalentTasksList(graph);
        this.currentSchedule = new Schedule(new LinkedList<>());
        LinkedList<Integer> freeTasks = GraphUtils.getInitialFreeTasks(graph);

        recurse(freeTasks);
    }

    /**
     * Recursive method for dfs traversing search space.
     * @param freeTasks takes in the queue of free tasks available on each recursive call.
     */
    public void recurse(LinkedList<Integer> freeTasks) {
        // If no more free tasks, check if complete schedule and if finish time beats the fastest time.
        if (freeTasks.isEmpty()) {
            int finishTime = currentSchedule.getLatestFinishTime();
            int numberOfScheduledTasks = currentSchedule.getNumberOfScheduledTasks();
            if (numberOfScheduledTasks == graph.getNodeCount() && currentSchedule.getLatestFinishTime() < fastestTime) {
                bestSchedule = new Schedule(new LinkedList<>(currentSchedule.getTasks()));
                fastestTime = finishTime;
            }

            return;
        }

        // TODO: Check if we have already come across an equivalent schedule. Can be done with hashing.

        // Calculate specific metrics at the current search state.
        int earliestFinishTime = currentSchedule.getEarliestFinishTime();
        int latestFinishTime = currentSchedule.getLatestFinishTime();
        int loadBalancedTime = (int) Math.ceil(tasksRemainingTime / (double) numProcessors);
        int longestTaskComputeTime = longestTaskComputeTime(freeTasks);

        // Sort free tasks by bLevel priority.
        freeTasks.sort(Comparator.comparing(node -> bLevels[node]));

        // Go through all the free tasks.
        HashSet<Integer> visited = new HashSet<>();
        for (int i = 0; i < freeTasks.size(); i++) {
            int task = freeTasks.remove();
            int taskWeight = graph.getNode(task).getAttribute("Weight", Double.class).intValue();
            tasksRemainingTime -= taskWeight;

            // Prune #1: check if we have already visited task.
            if (visited.contains(task)) {
                freeTasks.add(task);
                continue;
            }
            visited.addAll(equivalentTasksList.get(task));

            // Prune #2: ignore current partial schedule if it cannot become the potential optimal.
            if (!isPotential(earliestFinishTime, latestFinishTime, loadBalancedTime, longestTaskComputeTime)) {
                freeTasks.add(task);
                continue;
            }

            // For child nodes check if they have no more pending dependents, then add to freeTasks queue.
            List<Edge> childEdges = graph.getNode(task).leavingEdges().collect(Collectors.toList());
            boolean isChildAdded = false;
            for (Edge edge : childEdges) {
                Node child = edge.getNode1();
                dependents[child.getIndex()]--;
                if (dependents[child.getIndex()] == 0) {
                    freeTasks.add(child.getIndex());
                    isChildAdded = true;
                }
            }

            // Deep copy current free tasks for next recursive iteration.
            LinkedList<Integer> nextFreeTasks = new LinkedList<>(freeTasks);

            // For each processor schedule task on it to recursively generate next partial schedule.
            boolean isIsomorphic = false;
            for (int processor = 0; processor < numProcessors; processor++) {
                int processorFinishTime = currentSchedule.getProcessorFinishTime(processor);

                // Prune #3: processor normalization, avoid scheduling on isomorphic (empty) processor in the future.
                if (processorFinishTime == 0) {
                    if (isIsomorphic) continue;
                    isIsomorphic = true;
                }

                // Prune #4: ignore duplicate states pruning.
                if (!previousIsChildAdded && processor < currentSchedule.getLastProcessor()) continue;

                // Prune #5: if current start time + task b level can't beat current fastest time.
                int startTime = minimumStartTime(graph.getNode(task), currentSchedule, processor, processorFinishTime);
                if (startTime + bLevels[task] >= fastestTime) continue;

                // Make temporary copies of previous values.
                boolean tempPreviousIsChildAdded = previousIsChildAdded;
                previousIsChildAdded = isChildAdded;

                // Schedule task to current schedule.
                currentSchedule.addTask(graph.getNode(task), startTime, startTime + taskWeight, processor);
                recurse(nextFreeTasks);

                // Backtrack undoing task added.
                currentSchedule.popTask();
                previousIsChildAdded = tempPreviousIsChildAdded;
            }

            // Backtrack free tasks queue.
            for (Edge edge : childEdges) {
                Node child = edge.getNode1();
                dependents[child.getIndex()]++;
                if (dependents[child.getIndex()] == 1) {
                    freeTasks.removeLast();
                }
            }

            tasksRemainingTime += taskWeight;
            freeTasks.add(task);
        }
    }

    /**
     * Helper method for getting the longest compute time taken, or the highest bLevel value in task queue.
     * @param freeTasks queue of free tasks available for scheduling.
     * @return longest time taken through the critical path.
     */
    public int longestTaskComputeTime(LinkedList<Integer> freeTasks) {
        int longestCriticalPath = 0;
        for (Integer node : freeTasks) {
            longestCriticalPath = Math.max(longestCriticalPath, bLevels[node]);
        }
        return longestCriticalPath;
    }

    /**
     * Helper method checks if the current partial schedule is worth further investigation by checking constraints
     * against the current fastest finish time.
     * @param earliestFinishTime the earliest processor finish time in current partial schedule.
     * @param latestFinishTime the latest processor finish time in the current partial schedule.
     * @param loadBalancedTime the load balanced time across all processors.
     * @param longestTaskComputeTime the longest possible time through the task graph (compute time)
     * @return true/false if the current schedule is worth pursuing.
     */
    private boolean isPotential(int earliestFinishTime, int latestFinishTime, int loadBalancedTime, int longestTaskComputeTime) {
        boolean loadBalancingConstraint = earliestFinishTime + loadBalancedTime >= fastestTime;
        boolean criticalPathConstraint = earliestFinishTime + longestTaskComputeTime >= fastestTime;
        boolean latestFinishTimeConstraint = latestFinishTime >= fastestTime;
        return !loadBalancingConstraint && !criticalPathConstraint && !latestFinishTimeConstraint;
    }

    private int minimumStartTime(Node node, Schedule currentSchedule, int processor, int currentStartTime) {
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

    public Schedule getBestSchedule() {
        return bestSchedule;
    }

    public int getFastestTime() {
        return fastestTime;
    }
}
