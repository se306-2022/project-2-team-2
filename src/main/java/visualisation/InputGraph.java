package visualisation;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;

public class InputGraph extends FxViewer {
    private Graph graph;

    public InputGraph(Graph graph, Viewer.ThreadingModel threadingModel) {
        super(graph, threadingModel);
        this.graph = graph;
        initialiseLabels();
    }

    public void initialiseLabels() {
        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId() + "");
            node.setAttribute("ui.style", "text-alignment: center;\n"
                    +"\tstroke-mode: plain; stroke-color:grey; stroke-width: 3px;"
                    + "\tfill-mode: plain; fill-color: grey;\n"
                    + "\tsize: 30px, 30px;\n"
                    + "\ttext-size: 15px; text-color: black; text-style: bold;\n");
        }
    }

}
