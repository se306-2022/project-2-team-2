package IO;

import models.ResultTask;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.IOException;

/**
 * The IOHandler class uses the GraphStream Library to read the dot file containing digraph and
 * write the graph with the updated schedule to a dot file
 */
public class IOParser {

    /**
     * Reads the graph contained in the dot file
     *
     * @param inputFileName the name of the dot file
     * @return a graph
     */
    public static Graph read(String inputFileName) {
        Graph graph = new DefaultGraph("graph");
        FileSource fileSource = new FileSourceDOT();

        fileSource.addSink(graph);
        try {
            fileSource.readAll(inputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileSource.removeSink(graph);
        }
        return graph;
    }

    /**
     * Writes the graph with the updated schedule with start time and processor to a dot file
     *
     * @param outputFileName the name of the output dot file
     * @param graph          the initial graph returned from reading the dot file
     * @param resultTasks    the schedule
     */
    public static void write(String outputFileName, Graph graph, ResultTask[] resultTasks) {

        FileSink fileSink = new FileSinkDOT(true);

        for (int i = 0; i < graph.getNodeCount(); i++) {
            ResultTask task = resultTasks[i];
            Node n = graph.getNode(i);
            n.setAttribute("Weight", task.getFinishTime() - task.getStartTime());
            n.setAttribute("Start", task.getStartTime());
            n.setAttribute("Processor", task.getProcessor() + 1);
        }

        try {
            fileSink.writeAll(graph, outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
