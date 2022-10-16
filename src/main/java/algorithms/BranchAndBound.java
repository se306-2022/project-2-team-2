package algorithms;

import models.Schedule;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import utils.GraphUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BranchAndBound extends Algorithm {
    private final Graph graph;
    private final int numProcessors;
    private Schedule currentSchedule;
    private Schedule bestSchedule = new Schedule(new LinkedList<>());
    private int[] bLevels;
    private int[] dependents;
    private List<List<Integer>> equivalentTasksList;
    private final HashSet<Integer> visitedSchedules = new HashSet<>();
    private int fastestTime = Integer.MAX_VALUE;
    private boolean previousChildBeenAdded = false;

    public BranchAndBound(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
    }

    /**
     * Main executable method of the DFS branch and bound algorithm.
     */
    public void run() {
        // Preprocessing
        this.bLevels = GraphUtils.calculateBLevels(graph);
        this.dependents = GraphUtils.calculateDependents(graph);
        this.equivalentTasksList = GraphUtils.getEquivalentTasksList(graph);

        Greedy greedy = new Greedy(graph, numProcessors);
        greedy.run();
        this.fastestTime = greedy.getBestSchedule().getFinishTime();

        this.currentSchedule = new Schedule(new LinkedList<>());
        this.bestSchedule = new Schedule(new LinkedList<>());
        LinkedList<Integer> freeTasks = GraphUtils.getInitialFreeTasks(graph);

        recurse(freeTasks);

        setDone();
        System.out.println("Algorithm Sequential Completed.");
    }

    /**
     * Recursive method for dfs traversing search space.
     * @param freeTasks takes in the queue of free tasks available on each recursive call.
     */
    public void recurse(LinkedList<Integer> freeTasks) {
        setStatesSearched();

        // If no more free tasks, check if complete schedule and if finish time beats the fastest time.
        if (freeTasks.isEmpty()) {
            int numberOfScheduledTasks = currentSchedule.getNumberOfScheduledTasks();
            if (numberOfScheduledTasks == graph.getNodeCount() && currentSchedule.getFinishTime() < fastestTime) {
                bestSchedule = new Schedule(new LinkedList<>(currentSchedule.getTasks()));
                fastestTime = currentSchedule.getFinishTime();
            }

            return;
        }

        // Prune #1: check if we have visited equivalent schedule already using hashes.
        if (visitedSchedules.contains(currentSchedule.hashCode())) return;
        visitedSchedules.add(currentSchedule.hashCode());

        // Sort free tasks by bLevel priority.
        freeTasks.sort(Comparator.comparing(node -> bLevels[node]));

        // Go through all the free tasks.
        HashSet<Integer> visited = new HashSet<>();
        for (int i = 0; i < freeTasks.size(); i++) {
            int task = freeTasks.remove();
            int taskWeight = graph.getNode(task).getAttribute("Weight", Double.class).intValue();

            // Prune #2: check if we have already visited task.
            if (visited.contains(task)) {
                freeTasks.add(task);
                continue;
            }

            visited.addAll(equivalentTasksList.get(task));

            // For child nodes check if they have no more pending dependents, then add to freeTasks queue.
            boolean childBeenAdded = addPotentialChildNodesToFreeTasks(freeTasks, graph.getNode(task), dependents);

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
                if (!previousChildBeenAdded && processor < currentSchedule.getLastProcessor()) continue;

                // Prune #5 (MOST IMPORTANT): ignore if start time + task b level can't beat current fastest time.
                int startTime = minimumStartTime(graph.getNode(task), currentSchedule, processor, processorFinishTime);
                if (startTime + bLevels[task] >= fastestTime) continue;

                // Make temporary copies of previous values.
                boolean tempPreviousIsChildAdded = previousChildBeenAdded;
                previousChildBeenAdded = childBeenAdded;

                // Schedule task to current schedule.
                currentSchedule.addTask(graph.getNode(task), startTime, startTime + taskWeight, processor);
                recurse(nextFreeTasks);

                // Backtrack undoing task added.
                currentSchedule.popTask();
                previousChildBeenAdded = tempPreviousIsChildAdded;
            }

            // Backtrack free tasks queue.
            List<Node> childNodes = graph.getNode(task).leavingEdges().map(Edge::getNode1).collect(Collectors.toList());
            for (Node child : childNodes) {
                dependents[child.getIndex()]++;
                if (dependents[child.getIndex()] == 1) freeTasks.removeLast();
            }

            freeTasks.add(task);
        }
    }

    public Schedule getBestSchedule() {
        return bestSchedule;
    }

    public int getFastestTime() {
        return fastestTime;
    }

    public int getStatesSearched() {
        return statesSearched;
    }
}
