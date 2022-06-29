package fr.iutvalence.automath.app.io.in.helper;

import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.util.mxCellRenderer;
import fr.iutvalence.automath.app.bridge.IAutomatonOperator;
import fr.iutvalence.automath.app.io.in.helper.XMLHelper;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.view.handler.TaskWorkerHandler;
import fr.iutvalence.automath.app.view.utils.FilePreviewerWithWorker;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * ExportImageSinceXMLRunnable is the class that contains the action to convert the (.xml) file to png, it implements the Runnable interface
 * <p>This class and a Thread that allows to build an image of the graph for {@link FilePreviewerWithWorker} in to file</p>
 */
public class XMLGraphRenderer implements Runnable {

    /**
     * The graph of this instance for create the image
     */
    private final FiniteStateAutomatonGraph graph;

    /**
     * The path of the image that we want conversion
     */
    private final String path;

    /**
     * The graphic object that we want to send the constructed image
     */
    private final FilePreviewerWithWorker fileChooser;

    /**
     * A constructor of ExportPDF, with the parameter path of the file, the JFileChooser redefine and the Automate Interface
     *
     * @param path         The path to saving the file
     * @param fileChooser The file chooser with preview
     * @param automate     An interface to manipulate the automaton
     */
    public XMLGraphRenderer(String path, FilePreviewerWithWorker fileChooser, IAutomatonOperator automate) {
        this.graph = new FiniteStateAutomatonGraph(automate);
        this.path = path;
        this.fileChooser = fileChooser;
    }

    /**
     * Render the graph contained in the image file whose path is the first parameter
     *
     * @param file The path from where to read the graph
     * @return The Image of the file
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public BufferedImage exportAutomate(String file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(file));
        XMLHelper.importFromXML(document, graph, true);
        new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());
        return mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
    }

    /**
     * Call by {@link TaskWorkerHandler} to generate the image and apply it to the window in parallel
     */
    public void run() {
        BufferedImage buf = null;
        try {
            buf = exportAutomate(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.fileChooser.loadImage(buf, path);
    }
}
