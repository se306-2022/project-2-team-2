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

public class BranchAndBoundParallel {

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
                int numberOfScheduledTasks = currentSchedule.getNumberOfScheduledTasks();
                if (numberOfScheduledTasks == graph.getNodeCount() && currentSchedule.getFinishTime() < fastestTime) {
                    synchronized (RecursiveWorker.class) {
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
                boolean childBeenAdded = addPotentialChildNodesToFreeTasks(freeTasks, graph.getNode(task));

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
                            new Schedule(currentSchedule.getTasks()),
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

                ForkJoinTask.invokeAll(recursiveWorkers);
            }
        }

        /**
         * Add a tasks child nodes to free tasks queue, if their dependent nodes have been scheduled.
         * @param freeTasks queue of free tasks ready to be scheduled.
         * @param node GraphStream Node, the target node and it's children we want to schedule.
         * @return true/false if a child of the current node was added.
         */
        private boolean addPotentialChildNodesToFreeTasks(LinkedList<Integer> freeTasks, Node node) {
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
    }

    public Schedule getBestSchedule() {
        return bestSchedule;
    }

    public int getFastestTime() {
        return fastestTime;
    }
}
