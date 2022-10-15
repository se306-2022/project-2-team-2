package solution;

import IO.IOParser;
import algorithms.Algorithm;
import algorithms.BranchAndBound;
import models.Schedule;
import org.graphstream.graph.Graph;

public class SolutionThread extends Thread {
    private Graph graph;
    private int processors;
    private String outputFile;
    private Algorithm solution;

    public SolutionThread(Algorithm solution, Graph graph, int processors, String outputFile) {
        super();
        this.solution = solution;
        this.graph = graph;
        this.processors = processors;
        this.outputFile = outputFile;
    }

    public void run() {
        Schedule result;

        if (processors == 1) {
            BranchAndBound branchAndBound = new BranchAndBound(graph, processors);
            result = branchAndBound.getBestSchedule();

        } else {
            solution.run();
            Schedule optimal = solution.getBestSchedule();

        }

//        IOParser();
    }

    public int getStateCount() {
        return solution.getStateCount();
    }

    public int getCurrentBest() {
        return solution.getBestFinishTime();
    }

    public Schedule getBestSchedule() {
        return solution.getBestSchedule();
    }
}
