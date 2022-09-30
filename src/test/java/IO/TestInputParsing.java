package IO;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestInputParsing {

    @Test
    void testBasicArgs() {
        String[] args = { "testBasicArgs.dot" };

        InputCommand actual = CLIParser.commandLineParser(args);

        assertEquals("testBasicArgs.dot", actual.getInputFile());
    }

    @Test
    void testDefaultOutputFile() {
        String[] args = { "testDefaultOutputFile.dot" };

        InputCommand actual = CLIParser.commandLineParser(args);

        assertEquals("testDefaultOutputFile-output.dot",
                actual.getOutputFile());
        System.out.print(actual.getOutputFile());

    }
}
