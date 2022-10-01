import IO.CLIParser;
import IO.IOParser;
import IO.InputCommand;
import algorithms.Greedy;
import models.ResultTask;
import org.graphstream.graph.Graph;

public class Main {
    private static InputCommand commands;
    public static void main(String[] args) {
        commands = CLIParser.commandLineParser(args);;

        Greedy algorithm = new Greedy();
        Graph graph = IOParser.read(commands.getInputFile());

        ResultTask[] result = algorithm.
                GreedyScheduler(graph, commands.getNumProcessors());

        IOParser.write(commands.getOutputFile(), graph, result);
    }
}