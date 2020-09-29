package fr.iutvalence.automath.app.io.out;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.util.mxCellRenderer;

import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.bridge.InterfaceAutomaton;
import fr.iutvalence.automath.app.view.handler.TaskWorkerHandler;
import fr.iutvalence.automath.launcher.view.utils.FilePreviewerWithWorker;

/**
 * ExportImageSinceXMLRunnable is the class that contains the action to convert the (.xml) file to png, it implements the Runnable interface
 * <p>This class and a Thread that allows to build an image of the graph for {@link FilePreviewerWithWorker} in to file</p>
 */
public class ExportImageSinceXMLRunnable implements Runnable {
	
	/**
	 * The graph of this instance for create the image
	 */
	private FiniteStateAutomatonGraph graph;
	
	/**
	 * The path of the image that we want conversion
	 */
	private String path;
	
	/**
	 * The graphic object that we want to send the constructed image
	 */
	private FilePreviewerWithWorker jfileChooser;

	/**
	 * A constructor of ExportPDF, with the parameter path of the file, the JFileChooser redefine and the Automate Interface 
	 * @param path The path to saving the file
	 * @param jfileChooser The file chooser with preview
	 * @param automate An interface to manipulate the automaton 
	 */
	public ExportImageSinceXMLRunnable(String path, FilePreviewerWithWorker jfileChooser, InterfaceAutomaton automate){
		this.graph = new FiniteStateAutomatonGraph(automate);
		this.path = path;
		this.jfileChooser = jfileChooser;
	}
	
	/**
 	 * Convert the graph to BuggeredImage and save the result to an (.pdf) file with the specified path in parameter
	 * @param file The path to saving the file
	 * @return The Image of the file choose in xml
	 * @throws ParserConfigurationException
	 * @throws SAXException 
	 * @throws IOException
	 */
	public BufferedImage exportAutomate(String file) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(file));
		Element root = document.getDocumentElement();
		graph.getModel().beginUpdate();
		graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
		
		Element etatsNode = (Element) root.getElementsByTagName("liste_etats").item(0);
		NodeList listEtat = etatsNode.getElementsByTagName("etat");
		int nbEtat = listEtat.getLength();
		Map<Integer,Object> allStates = new HashMap<>();
		for (int etat_index = 0; etat_index < nbEtat; etat_index++) {
			if (listEtat.item(etat_index).getNodeType() == Node.ELEMENT_NODE) {
				Element state = (Element) listEtat.item(etat_index);
			    String name = (state.getElementsByTagName("nom").item(0).getTextContent());
			    int id = Integer.parseInt(state.getAttribute("id"));
			    int x = Integer.parseInt(state.getElementsByTagName("cooX").item(0).getTextContent());
			    int y = Integer.parseInt(state.getElementsByTagName("cooY").item(0).getTextContent());
			    boolean initial = Boolean.parseBoolean(state.getElementsByTagName("beginState").item(0).getTextContent());
			    boolean accept = Boolean.parseBoolean(state.getElementsByTagName("finalState").item(0).getTextContent());
			    allStates.put(id,addState(name, x, y, accept, initial, graph));
			}
			}
			Element linkNodes = (Element) root.getElementsByTagName("liste_liens").item(0);
			NodeList listLink = linkNodes.getElementsByTagName("lien");
			int nbLinks = listLink.getLength();
			for (int etat_index = 0; etat_index < nbLinks; etat_index++) {
			 if(listLink.item(etat_index).getNodeType() == Node.ELEMENT_NODE) {
				 Element link = (Element) listLink.item(etat_index);
				 int etat_depart = Integer.parseInt(link.getElementsByTagName("etat_depart").item(0).getTextContent());
				 int etat_arr = Integer.parseInt(link.getElementsByTagName("etat_arr").item(0).getTextContent());
				 addEdge(link.getElementsByTagName("caractere").item(0).getTextContent(),allStates.get(etat_depart),allStates.get(etat_arr), graph);
			 }
			}
			graph.getModel().endUpdate();
			new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());
		return mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
	}
	
	/**
	 * Redefinition of method {@link FiniteStateAutomatonGraph#insertEdge}, to add a transition in the graph
	 * @param cs The name of the transition
	 * @param source The source of the transition
	 * @param target The target of the transition
	 * @param graph The graph that will be modified
	 */
	private void addEdge(String cs, Object source, Object target, FiniteStateAutomatonGraph graph){
		graph.insertEdge(graph.getDefaultParent(), null,
			cs, source, target, FiniteStateAutomatonGraph.styleTransition);
	}
	
	/**
	 * Redefinition of method A in the Class {@link FiniteStateAutomatonGraph#insertState}, for the add a state in the graph 
	 * @param id The name of the state
	 * @param x The position on the abscissa
	 * @param y The position on the ordinate
	 * @param accept 	<code>true</code> if the state is a state of arrival; 
     *                  <code>false</code> otherwise.
	 * @param initial	<code>true</code> if the state is a starting state; 
     *                  <code>false</code> otherwise.
	 * @param graph The graph that will be modified
	 * @return The object implemented by <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxICell}</a>
	 */
	private Object addState(String id, double x, double y, boolean accept, boolean initial, FiniteStateAutomatonGraph graph) {
		String style;
		if (accept && initial) {
			style = FiniteStateAutomatonGraph.styleFinalBeginState;
		} else if (accept) {
			style = FiniteStateAutomatonGraph.styleFinalState;
		} else if (initial) {
			style = FiniteStateAutomatonGraph.styleBeginState;
		} else {
			style = FiniteStateAutomatonGraph.styleDefaultState;
		}
		return graph.insertState(graph.getDefaultParent(), id, x, y, style, accept, initial);
	}
	
	/**
	 * Call by {@link TaskWorkerHandler} to generate the image and apply it to the window in parallel
	 */
     public void run() {
         BufferedImage buf = null;
         try {
        	 buf = exportAutomate(path);
        } catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		 this.jfileChooser.loadImage(buf, path);
     }
}
