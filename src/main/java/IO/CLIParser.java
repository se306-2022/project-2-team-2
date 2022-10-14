package IO;

import org.apache.commons.cli.*;

public class CLIParser {
    private static Options options = new Options();

    private static String header;

    private static String footer;
    private static void init() {
        options.addOption(
                Option.builder("o").required(false).longOpt("output")
                        .hasArg(true).argName("Output file name")
                        .desc("Specify the file name of the output of the program")
                        .build());

        options.addOption(
                Option.builder("p").required(false).longOpt("parallel")
                        .hasArg(true).argName("Parallel")
                        .desc("Run the program in parallel with the specified number of cores")
                        .build());

        options.addOption(
                Option.builder("v").required(false)
                        .hasArg(false).longOpt("visualisation")
                        .desc("Run the GUI Visualisation")
                        .build());

        options.addOption(
                Option.builder("h").required(false).longOpt("help")
                        .hasArg(false).desc("Run the help menu of the program")
                        .build());

        header =
                "This program takes a .dot file as input, containing task information, "
                        + "and produces the optimal schedule for these tasks on a specified number of processors.";

        footer = "If there are any problems contact project-2-team-2.";
    }

    public static InputCommand commandLineParser(String[] args) {
        init();

        if (args.length < 2) {
            error(new String[] { "Missing input arguments" });
        }

        InputCommand inputCommand = new InputCommand();
        String inputFile = args[0];
        CommandLine commandLineOption = parseOptions(args);

        inputCommand.setInputFile(inputFile);

        inputCommand.setNumProcessors(parseNumProcessors(args));

        if (commandLineOption.hasOption('o')) {
            String outputFile = getOptionValueString(commandLineOption, 'o');
            inputCommand.setOutputFile(outputFile);
            inputCommand.setCustomOutputFile(true);

        }

        if (commandLineOption.hasOption('h')) {
            help();
        }

        if (commandLineOption.hasOption('p')) {
            inputCommand.setParallel(true);
            int numCores = getOptionValueInt(commandLineOption, 'p');
            inputCommand.setNumParallelCores(numCores);
        }

        if (commandLineOption.hasOption('v')) {
            inputCommand.setVisual(true);
        }

        return inputCommand;
    }

    private static int parseNumProcessors(String[] args) {
        try {
            return Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number of processors.");
            System.exit(1);
        }

        return -1;
    }

    private static CommandLine parseOptions(String[] args) {
        CommandLine commandLineOptions = null;
        try {
            commandLineOptions = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            /* Add an Error option later in the future. */
        }
        return commandLineOptions;
    }

    private static String getOptionValueString(CommandLine commandLine, char option) {
        return commandLine.getOptionValue(option);
    }

    private static int getOptionValueInt(CommandLine commandLine, char option) {
        try {
            String intString = commandLine.getOptionValue(option);
            return Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing option value of integer type.");
            help();
            System.exit(1);
        }

        return -1;
    }

    private static void error(String[] messages) {
        for (String msg : messages) {
            System.err.println(msg);
            help();
        }
        System.exit(1);
    }

    private static void help() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("scheduler [INPUT-FILE] [NUM_PROCESSORS] ", header, options, footer, true);
    }
}
