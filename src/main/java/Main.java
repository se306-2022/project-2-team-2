import IO.CLIParser;
import IO.IOParser;
import IO.InputCommand;
import algorithms.Algorithm;
import algorithms.BranchAndBound;
import algorithms.BranchAndBoundParallel;
import algorithms.Greedy;
import com.sun.javafx.application.PlatformImpl;
import javafx.stage.Stage;
import models.Schedule;
import org.graphstream.graph.Graph;
import solution.SolutionThread;
import visualisation.VisualizationApplication;

public class Main {
    private static InputCommand commands;
    private static String type;

    public static void main(String[] args) {

        commands = CLIParser.commandLineParser(args);

        Graph graph = IOParser.read(commands.getInputFile());

        // KEEP getSolution & getAlgorithm separate. NEVER run together, dangerous bug somewhere.
        if (commands.isVisual()) {
            Algorithm solution = getSolution(commands.isParallel(), graph, commands.getNumProcessors());
            runVisual(solution, commands.getNumProcessors(), commands.getInputFile(), commands.getOutputFile(), type);
        } else {
            Schedule algorithm = getAlgorithm(commands.isParallel());
            IOParser.write(commands.getOutputFile(), graph, algorithm);
        }
    }

    public static Schedule getAlgorithm(boolean isParallel) {

        int numProcessors = commands.getNumProcessors();
        Graph graph = IOParser.read(commands.getInputFile());

        if (numProcessors == 1) {
            Greedy algorithmGreedy = new Greedy(graph, numProcessors);
            type = "greedy";
            algorithmGreedy.run();
            return algorithmGreedy.getBestSchedule();
        }

        if (isParallel) {
            int numParallelCores = -1;
            try {
                numParallelCores = commands.getNumParallelCores();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (numParallelCores > 1) {
                BranchAndBoundParallel algorithmParallel = new BranchAndBoundParallel(graph, numProcessors, numParallelCores);
                type = "parallel";
                algorithmParallel.run();
                return algorithmParallel.getBestSchedule();
            }
        }

        BranchAndBound algorithmSequential = new BranchAndBound(graph, commands.getNumProcessors());
        type = "sequential";
        algorithmSequential.run();
        return algorithmSequential.getBestSchedule();
    }

    private static Algorithm getSolution(boolean isParallel, Graph graph, int numProcessors) {
        Algorithm solution;

        if (numProcessors == 1) {
            Greedy algorithmGreedy = new Greedy(graph, numProcessors);
            type = "greedy";
            return algorithmGreedy;
        }

        if (isParallel) {
            int numParallelCores = -1;
            try {
                numParallelCores = commands.getNumParallelCores();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (numParallelCores > 1) {
                solution = new BranchAndBoundParallel(graph, numProcessors,numParallelCores);
                type = "parallel";
                return solution;
            }
        }

        solution = new BranchAndBound(graph, numProcessors);
        return solution;
    }

    private static void runVisual(Algorithm solution, int numProcessors, String inputFile, String outputFile, String type) {
        PlatformImpl.startup(() -> {

            VisualizationApplication visualization = new VisualizationApplication();
            SolutionThread solutionThread = new SolutionThread(solution, numProcessors, type);

            try {
                visualization.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }

            visualization.setUpArgs(solutionThread, inputFile, outputFile);
        });
    }
}