import IO.CLIParser;
import IO.IOParser;
import IO.InputCommand;
import algorithms.Greedy;
import models.ResultTask;
import models.Schedule;
import org.graphstream.graph.Graph;

public class Main {
    private static InputCommand commands;
    public static void main(String[] args) {
        commands = CLIParser.commandLineParser(args);;

        Graph graph = IOParser.read(commands.getInputFile());

        Greedy algorithm = new Greedy(graph, commands.getNumProcessors());

        Schedule result = algorithm.run();

//        IOParser.write(commands.getOutputFile(), graph, result);
    }
}