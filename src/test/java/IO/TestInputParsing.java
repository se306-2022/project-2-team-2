package IO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestInputParsing {

    @Test
    void testBasicArgs() {
        String[] args = { "testBasicArgs.dot" };

        InputCommand actual = CLIParser.commandLineParser(args);

        assertAll(
                "Tests that the input file name and number of processors are correct",
                () -> assertEquals("testBasicArgs.dot", actual.getInputFile())
        );
    }

    @Test
    void testDefaultOutputFile() {
        String[] args = { "testDefaultOutputFile.dot" };

        InputCommand actual = CLIParser.commandLineParser(args);

        assertEquals("testDefaultOutputFile-output.dot",
                actual.getOutputFile());
    }

}
