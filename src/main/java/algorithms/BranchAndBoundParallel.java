package algorithms;

import models.Schedule;
import org.graphstream.graph.Graph;
import utils.GraphUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class BranchAndBoundParallel {

    private ForkJoinPool forkJoinPool;

    private final Graph graph;
    private final int numProcessors;
    private Schedule bestSchedule;
    private int fastestTime = Integer.MAX_VALUE;
    private int[] bLevels;
    private int[] dependents;
    private List<List<Integer>> equivalentTasksList;
    private int statesSearched = 0;
    private final HashSet<Integer> visitedSchedules = new HashSet<>();

    public BranchAndBoundParallel(Graph graph, int numProcessors) {
        this.numProcessors = numProcessors;
        this.graph = graph;
    }

    public void run() {
        // Preprocessing
        this.bLevels = GraphUtils.calculateBLevels(graph);
        this.dependents = GraphUtils.calculateDependents(graph);
        this.equivalentTasksList = GraphUtils.getEquivalentTasksList(graph);
        this.fastestTime = new Greedy(graph, numProcessors).run().getFinishTime();

        // Initial recursive action in pool.
        LinkedList<Integer> freeTasks = GraphUtils.getInitialFreeTasks(graph);
        Schedule initialSchedule = new Schedule(new LinkedList<>());
        forkJoinPool.invoke(new RecursiveWorker(initialSchedule, freeTasks, false));

        System.out.println("Sates searched: " + statesSearched);
    }

    private class RecursiveWorker extends RecursiveAction {

        private Schedule currentSchedule;
        private LinkedList<Integer> freeTasks;
        private boolean previousChildBeenAdded;

        private RecursiveWorker(Schedule schedule, LinkedList<Integer> freeTasks, boolean previousChildBeenAdded) {
            this.currentSchedule = schedule;
            this.freeTasks = freeTasks;
            this.previousChildBeenAdded = previousChildBeenAdded;
        }

        @Override
        protected void compute() {
            // add b&b recurse method.
        }
    }
}
