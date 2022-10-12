import IO.CLIParser;
import IO.IOParser;
import IO.InputCommand;
import algorithms.BranchAndBound;
import algorithms.BranchAndBoundParallel;
import algorithms.Greedy;
import models.ResultTask;
import models.Schedule;
import org.graphstream.graph.Graph;

public class Main {
    private static InputCommand commands;

    public static void main(String[] args) {
        commands = CLIParser.commandLineParser(args);

        Graph graph = IOParser.read(commands.getInputFile());

        Schedule algorithm = getAlgorithm(commands.isParallel());

        IOParser.write(commands.getOutputFile(), graph, algorithm);

    }
    public static Schedule getAlgorithm(boolean isParallel) {

        Graph graph = IOParser.read(commands.getInputFile());

//        if (isParallel) {
//            int numParallelCores = -1;
//            try {
//                numParallelCores = commands.getNumParallelCores();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            if (numParallelCores > 1) {
//                BranchAndBoundParallel algorithmParallel = new BranchAndBoundParallel(graph, numParallelCores);
//                algorithmParallel.run();
//                // TODO: Return best schedule for bnb parallel when Matthew finishes.
//                return algorithmParallel;
//            }
//        }

        BranchAndBound algorithmSequential = new BranchAndBound(graph, commands.getNumProcessors());
        algorithmSequential.run();

        return algorithmSequential.getBestSchedule();
    }
}