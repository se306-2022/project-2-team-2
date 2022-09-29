package IO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLIParser {
    private static Options options = new Options();

    // This can be implemented later
    private static void init() {
        options.addOption(
                Option.builder("o").required(false)
                        .hasArg(true).argName("Output file name")
                        .desc("Specify the file name of the output of the program")
                        .build());

    }

    public static InputCommand commandLineParser(String[] args) {
        init();

        InputCommand inputCommand = new InputCommand();
        String inputFile = args[0];
        CommandLine commandLineOption = parseOptions(args);
        inputCommand.setInputFile(inputFile);

        if (commandLineOption.hasOption('o')) {
            String outputFile = getOptionValueString(commandLineOption, 'o');
            inputCommand.setOutputFile(outputFile);
            inputCommand.setCustomOutputFile(true);

        }

        return inputCommand;
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
}
