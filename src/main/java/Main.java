import IO.CLIParser;
import IO.InputCommand;

public class Main {

    private static InputCommand commands;
    public static void main(String[] args) {
        commands = CLIParser.commandLineParser(args);;
    }
}