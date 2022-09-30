package IO;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestInputParsing {

    @Test
    public void testBasicArgs() {
        String[] args = { "testBasicArgs.dot", "3" };

        InputCommand actual = CLIParser.commandLineParser(args);

        assertEquals("testBasicArgs.dot", actual.getInputFile());
        assertEquals(3, actual.getNumProcessors());
    }

    @Test
    public void testDefaultOutputFile() {
        String[] args = { "testDefaultOutputFile.dot", "3" };

        InputCommand actual = CLIParser.commandLineParser(args);

        assertEquals("testDefaultOutputFile-output.dot",
                actual.getOutputFile());
        System.out.print(actual.getOutputFile());

    }
}
