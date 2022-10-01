package IO;

import org.apache.commons.cli.*;

public class CLIParser {
    private static Options options = new Options();


    private static void init() {
        options.addOption(
                Option.builder("o").required(false)
                        .hasArg(true).argName("Output file name")
                        .desc("Specify the file name of the output of the program")
                        .build());
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

    private static void error(String[] messages) {
        for (String msg : messages) {
            System.err.println(msg);
        }
        System.exit(1);
    }
}
