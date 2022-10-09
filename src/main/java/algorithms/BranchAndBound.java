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
    private int statesSearched = 0;

    // TODO: use a greedy algorithm to get initial bound fastestTime.
    private int fastestTime = Integer.MAX_VALUE;
    private boolean previousIsChildAdded = false;

    public BranchAndBound(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
    }

    public void run() {
        this.bLevels = GraphUtils.calculateBLevels(graph);
        this.dependents = GraphUtils.calculateDependents(graph);
        this.equivalentTasksList = GraphUtils.getEquivalentTasksList(graph);
        this.currentSchedule = new Schedule(new LinkedList<>());
        LinkedList<Integer> freeTasks = GraphUtils.getInitialFreeTasks(graph);

        recurse(freeTasks);

        System.out.println("Sates searched: " + statesSearched);
    }

    /**
     * Recursive method for dfs traversing search space.
     * @param freeTasks takes in the queue of free tasks available on each recursive call.
     */
    public void recurse(LinkedList<Integer> freeTasks) {
        statesSearched++;

        // If no more free tasks, check if complete schedule and if finish time beats the fastest time.
        if (freeTasks.isEmpty()) {
            int numberOfScheduledTasks = currentSchedule.getNumberOfScheduledTasks();
            if (numberOfScheduledTasks == graph.getNodeCount() && currentSchedule.getFinishTime() < fastestTime) {
                bestSchedule = new Schedule(new LinkedList<>(currentSchedule.getTasks()));
                fastestTime = currentSchedule.getFinishTime();
            }

            return;
        }

        // TODO: Check if we have already come across an equivalent schedule. Can be done with hashing.

        // Sort free tasks by bLevel priority.
        freeTasks.sort(Comparator.comparing(node -> bLevels[node]));

        // Go through all the free tasks.
        HashSet<Integer> visited = new HashSet<>();
        for (int i = 0; i < freeTasks.size(); i++) {
            int task = freeTasks.remove();
            int taskWeight = graph.getNode(task).getAttribute("Weight", Double.class).intValue();

            // Prune #1: check if we have already visited task.
            if (visited.contains(task)) {
                freeTasks.add(task);
                continue;
            }

            visited.addAll(equivalentTasksList.get(task));

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

                // Prune #2: processor normalization, avoid scheduling on isomorphic (empty) processor in the future.
                if (processorFinishTime == 0) {
                    if (isIsomorphic) continue;
                    isIsomorphic = true;
                }

                // Prune #3: ignore duplicate states pruning.
                if (!previousIsChildAdded && processor < currentSchedule.getLastProcessor()) continue;

                // Prune #4 (MOST IMPORTANT): ignore if start time + task b level can't beat current fastest time.
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

            freeTasks.add(task);
        }
    }

    /**
     * Calculates minimum possible start time, taking into account target processor and communication costs.
     * @param node GraphStream Node object, the task node we want to schedule.
     * @param currentSchedule the current partial schedule we have.
     * @param processor the target processor we are trying to schedule the task onto.
     * @param currentStartTime the target processor finish time, minimum time calculated can't be less than this.
     * @return earliest possible start time.
     */
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
