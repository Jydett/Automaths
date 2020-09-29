package fr.iutvalence.automath.app.io.in;

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

import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;

/**
 * Contains the action to convert the XML file to graphical automaton
 */
public class ImporterXML {

	/**
	 * The graph of this instance for create the image
	 */
	private FiniteStateAutomatonGraph graphicalAutomaton;

	/**
	 * A parser for XML
	 */
	private final DocumentBuilder builder;

	/**
	 * The map to contain all states of automaton
	 */
	private Map<Integer,Object> allStates;

	/**
	 * A constructor of ImporterXML, with the parameter graph
	 * @param graphicalAutomaton The graph of application
	 * @throws ParserConfigurationException Indicates a serious configuration error
	 */
	public ImporterXML(FiniteStateAutomatonGraph graphicalAutomaton) throws ParserConfigurationException {
		this.graphicalAutomaton = graphicalAutomaton;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		this.builder = factory.newDocumentBuilder();
		allStates = new HashMap<>();
	}
	
	/**
	 * Convert the XML file to the graphical automaton with the specified path in parameter
	 * @param file The path to saving the file
	 * @param isClear 	<code>true</code> if the current graph is reset; 
     *                  <code>false</code> otherwise.
	 * @throws SAXException Encapsulate a general SAX error or warning
	 * @throws IOException Signals that an I/O exception of some sort has occurred
	 */
	public void importAutomaton(String file, boolean isClear) throws SAXException, IOException{
		if (file.equals("cancel")) return;
		Document document = builder.parse(new File(file));
		Element root = document.getDocumentElement();
		graphicalAutomaton.getModel().beginUpdate();
		if (isClear) graphicalAutomaton.deleteAllElements();
		
		Element etatsNode = (Element) root.getElementsByTagName("liste_etats").item(0);
		NodeList listEtat = etatsNode.getElementsByTagName("etat");
		int nbEtat = listEtat.getLength();
		for (int etat_index = 0; etat_index < nbEtat; etat_index++) {
			if (listEtat.item(etat_index).getNodeType() == Node.ELEMENT_NODE) {
				Element state = (Element) listEtat.item(etat_index);
				String name = (state.getElementsByTagName("nom").item(0).getTextContent());
				int id = Integer.parseInt(state.getAttribute("id"));
				int x = Integer.parseInt(state.getElementsByTagName("cooX").item(0).getTextContent());
				int y = Integer.parseInt(state.getElementsByTagName("cooY").item(0).getTextContent());
				boolean initial = Boolean.parseBoolean(state.getElementsByTagName("beginState").item(0).getTextContent());
				boolean accept = Boolean.parseBoolean(state.getElementsByTagName("finalState").item(0).getTextContent());
				allStates.put(id,graphicalAutomaton.addState(name, x, y, accept, initial));
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
			     graphicalAutomaton.addEdge(link.getElementsByTagName("caractere").item(0).getTextContent(), allStates.get(etat_depart), allStates.get(etat_arr));
			}
		}
		graphicalAutomaton.getModel().endUpdate();
		new mxParallelEdgeLayout(graphicalAutomaton).execute(graphicalAutomaton.getDefaultParent());
	}
}
