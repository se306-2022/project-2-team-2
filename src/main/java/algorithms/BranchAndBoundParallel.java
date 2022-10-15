package algorithms;

import models.Schedule;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import utils.GraphUtils;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

public class BranchAndBoundParallel extends Algorithm {

    private ForkJoinPool forkJoinPool;

    private final Graph graph;
    private final int numProcessors;
    private Schedule bestSchedule;
    private int fastestTime = Integer.MAX_VALUE;
    private int[] bLevels;
    private List<List<Integer>> equivalentTasksList;
    private final HashSet<Integer> visitedSchedules = new HashSet<>();

    public BranchAndBoundParallel(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
    }

    public void run() {
        // Preprocessing
        this.bLevels = GraphUtils.calculateBLevels(graph);
        this.equivalentTasksList = GraphUtils.getEquivalentTasksList(graph);
        this.fastestTime = new Greedy(graph, numProcessors).run().getFinishTime();

        // Initial recursive action in pool.
        LinkedList<Integer> freeTasks = GraphUtils.getInitialFreeTasks(graph);
        int[] dependents = GraphUtils.calculateDependents(graph);
        Schedule initialSchedule = new Schedule(new LinkedList<>());
        forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new RecursiveWorker(initialSchedule, freeTasks, dependents, false));

        setDone();
    }

    private class RecursiveWorker extends RecursiveAction {

        private Schedule currentSchedule;
        private LinkedList<Integer> freeTasks;
        private boolean previousChildBeenAdded;
        private int[] dependents;

        private RecursiveWorker(Schedule schedule, LinkedList<Integer> freeTasks, int[] dependents, boolean previousChildBeenAdded) {
            this.currentSchedule = schedule;
            this.freeTasks = freeTasks;
            this.previousChildBeenAdded = previousChildBeenAdded;
            this.dependents = dependents;
        }

        @Override
        protected void compute() {
            // If no more free tasks, check if complete schedule and if finish time beats the fastest time.
            if (freeTasks.isEmpty()) {
                synchronized (RecursiveWorker.class) {
                    int numberOfScheduledTasks = currentSchedule.getNumberOfScheduledTasks();
                    if (numberOfScheduledTasks == graph.getNodeCount() && currentSchedule.getFinishTime() < fastestTime) {
                        bestSchedule = new Schedule(new LinkedList<>(currentSchedule.getTasks()));
                        fastestTime = currentSchedule.getFinishTime();
                    }
                }

                return;
            }

            synchronized (RecursiveWorker.class) {
                // Prune #1: check if we have visited equivalent schedule already using hashes.
                if (visitedSchedules.contains(currentSchedule.hashCode())) return;
                visitedSchedules.add(currentSchedule.hashCode());
            }


            // Sort free tasks by bLevel priority.
            freeTasks.sort(Comparator.comparing(node -> bLevels[node]));

            // Go through all the free tasks.
            HashSet<Integer> visited = new HashSet<>();
            for (int i = 0; i < freeTasks.size(); i++) {
                List<RecursiveWorker> recursiveWorkers = new ArrayList<>();

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

                    RecursiveWorker recursiveWorker = new RecursiveWorker(
                            new Schedule(new LinkedList<>(currentSchedule.getTasks())),
                            new LinkedList<>(nextFreeTasks),
                            Arrays.copyOf(dependents, dependents.length),
                            previousChildBeenAdded);

                    recursiveWorkers.add(recursiveWorker);

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

                invokeAll(recursiveWorkers);
            }
        }

    }

    public Schedule getBestSchedule() {
        return bestSchedule;
    }

    public int getFastestTime() {
        return fastestTime;
    }
}
