package fr.iutvalence.automath.app.io.in;

import com.mxgraph.layout.mxParallelEdgeLayout;
import fr.iutvalence.automath.app.io.in.helper.XMLHelper;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import lombok.Setter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Contains the action to convert the XML file to graphical automaton
 */
public class ImporterXML implements Importer {

	/**
	 * The graph of this instance for create the image
	 */
	private final FiniteStateAutomatonGraph graph;
	@Setter
	private String file;

	/**
	 * A parser for XML
	 */
	private final DocumentBuilder builder;

	/**
	 * A constructor of ImporterXML, with the parameter graph
	 * @param graph The graph of application
	 * @throws ParserConfigurationException Indicates a serious configuration error
	 */
	public ImporterXML(FiniteStateAutomatonGraph graph) throws ParserConfigurationException {
		this.graph = graph;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		this.builder = factory.newDocumentBuilder();
	}
	
	/**
	 * Convert the XML file to the graphical automaton
	 * @param clearBeforeImport 	<code>true</code> if the current graph is reset;
     *                  <code>false</code> otherwise.
	 * @throws SAXException Encapsulate a general SAX error or warning
	 * @throws IOException Signals that an I/O exception of some sort has occurred
	 */
	public void importAutomaton(boolean clearBeforeImport) throws Exception {
		if (file.equals("cancel")) return;//TODO wtf
		Document document;
		document = builder.parse(new File(file));
		XMLHelper.importFromXML(document, graph, clearBeforeImport);
		new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());
	}
}
