package algorithms;

import models.ResultTask;
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

    private Schedule bestSchedule;
    private Schedule currentSchedule;

    private int fastestTime = Integer.MAX_VALUE;
    private int freeTasksRemainingTime = 0;


    private int previousProcessor = -1;
    private boolean previouslyAdded = false;

    public BranchAndBound(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
    }

    public void run() {
        this.bLevels = GraphUtils.calculateBLevels(graph);
        this.dependents = GraphUtils.calculateDependents(graph);
        this.currentSchedule = new Schedule(new LinkedList<>());
        LinkedList<Integer> freeTasks = GraphUtils.getInitialFreeTasks(graph);

        recurse(freeTasks);
    }

    public int longestCriticalPath(LinkedList<Integer> freeTasks) {
        int longestCriticalPath = 0;
        for (Integer node : freeTasks) {
            longestCriticalPath = Math.max(longestCriticalPath, bLevels[node]);
        }
        return longestCriticalPath;
    }

    public void recurse(LinkedList<Integer> freeTasks) {
        if (freeTasks.isEmpty()) {
            updateBestSchedule();
            return;
        }

        // TODO: Check if we have already come across an equivalent schedule.

        // TODO: Check if we can complete tasks in fixed data task order FTO.

        // Calculate specific metrics at the current search state.
        int earliestFinishTime = currentSchedule.getEarliestFinishTime();
        int latestFinishTime = currentSchedule.getLatestFinishTime();
        int loadBalancedTime = freeTasksRemainingTime / numProcessors;
        int longestCriticalPath = longestCriticalPath(freeTasks);

        // Sort free tasks by bLevel priority.
        freeTasks.sort(Comparator.comparing(node -> bLevels[node]));

        // Go through all the free tasks.
        HashSet<Integer> visited = new HashSet<>();
        for (int i = 0; i < freeTasks.size(); i++) {
            int task = freeTasks.remove();
            int taskWeight = graph.getNode(task).getAttribute("Weight", Double.class).intValue();

            // Check if we have already visited task.
            if (visited.contains(task)) {
                freeTasks.add(task);
                continue;
            }

            // TODO: add equivalent nodes to visited tasks.

            // Ignore current schedule if it cannot become the potential optimal.
            if (!isPotential(earliestFinishTime, latestFinishTime, loadBalancedTime, longestCriticalPath)) {
                freeTasks.add(task);
                continue;
            }

            freeTasksRemainingTime -= taskWeight;

            // For child nodes check if they have no more pending dependents, then add to freeTasks queue.
            List<Edge> childEdges = graph.getNode(task).leavingEdges().collect(Collectors.toList());
            boolean childAdded = false;
            for (Edge edge : childEdges) {
                Node child = edge.getNode1();
                dependents[child.getIndex()]--;
                if (dependents[child.getIndex()] == 0) {
                    freeTasks.add(task);
                    childAdded = true;
                }
            }

            // Calculate max communication cost between processors.
            int maxDataArrivalTime = 0;
            int maxDataArrivalProcessor = 0;
            List<Edge> parentEdges = graph.getNode(task).enteringEdges().collect(Collectors.toList());
            for (Edge edge : parentEdges) {
                Node parent = edge.getNode0();
                int parentStartTime = currentSchedule.getTaskStartTime(parent);
                int parentWeight = parent.getAttribute("Weight", Double.class).intValue();
                int communicationCost = edge.getAttribute("Weight", Double.class).intValue();
                int dataArrivalTime = parentStartTime + parentWeight + communicationCost;

                if (dataArrivalTime >= maxDataArrivalTime) {
                    maxDataArrivalTime = dataArrivalTime;
                    maxDataArrivalProcessor = currentSchedule.getTaskProcessor(parent);
                }
            }

            // Deep copy current free tasks for next recursive iteration.
            LinkedList<Integer> nextFreeTasks = new LinkedList<>(freeTasks);

            // For each processor schedule task on it to recursively generate next partial schedule.
            boolean isIsomorphic = false;
            for (int processor = 0; processor < numProcessors; processor++) {
                int processorFinishTime = currentSchedule.getProcessorFinishTime(processor);

                // Processor normalization, avoid scheduling on isomorphic (empty) processor in the future.
                if (processorFinishTime == 0) {
                    if (isIsomorphic) continue;
                    isIsomorphic = true;
                }

                // Duplicate state pruning. Might conflict with FTO.
                if (!previouslyAdded && processor < previousProcessor) continue;

                // Start time of next task will account max data arrival time if we're on another processor
                int startTime = processorFinishTime;
                if (processor != maxDataArrivalProcessor && maxDataArrivalTime >= startTime) {
                    startTime = maxDataArrivalTime;
                }

                // Prune state if current start time + task b level can't beat current fastest time.
                if (startTime + bLevels[task] >= fastestTime) continue;

                // Make temporary copies of previous values.
                int tempPreviousProcessor = previousProcessor;
                boolean tempPreviouslyAdded = previouslyAdded;
                previousProcessor = processor;
                previouslyAdded = childAdded;

                // Schedule task to current schedule.
                ResultTask resultTask = new ResultTask(graph.getNode(task), startTime, startTime + taskWeight, processor);
                currentSchedule.addTask(resultTask);

                recurse(nextFreeTasks);

                // Backtrack undoing task added.
                currentSchedule.popTask();
                previousProcessor = tempPreviousProcessor;
                previouslyAdded = tempPreviouslyAdded;
            }

            // Backtrack free tasks queue.
            for (Edge edge : childEdges) {
                Node child = edge.getNode1();
                dependents[child.getIndex()]--;
                if (dependents[child.getIndex()] == 1) {
                    freeTasks.removeLast();
                }
            }

            freeTasksRemainingTime += taskWeight;
            freeTasks.add(task);
        }
    }

    private boolean isPotential(int earliestFinishTime, int latestFinishTime, int loadBalancedTime, int longestCriticalPath) {
        boolean loadBalancingConstraint = earliestFinishTime + loadBalancedTime >= fastestTime;
        boolean criticalPathConstraint = earliestFinishTime + longestCriticalPath >= fastestTime;
        boolean latestFinishTimeConstraint = latestFinishTime >= fastestTime;
        return !loadBalancingConstraint && !criticalPathConstraint && !latestFinishTimeConstraint;
    }

    public void updateBestSchedule() {
        int finishTime = currentSchedule.getLatestFinishTime();

        if (finishTime < fastestTime) {
            fastestTime = finishTime;
            bestSchedule = new Schedule(currentSchedule.getTasks());
        }
    }

    public Schedule getBestSchedule() {
        return bestSchedule;
    }
}
