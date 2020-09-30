package fr.iutvalence.automath.app.io.out;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.StateInfo;
import fr.iutvalence.automath.app.model.TransitionInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ExportXML is the class that contains the action to convert the graphical automaton to XML file
 */
public class ExportXML implements Exporter {

    /**
     * An editable document
     */
    private final Document document;
    /**
     * The root element of XML
     */
    private final Element racine;

    private final Transformer transformer;

    /**
     * The map to contain all states of automaton
     */
    private final Map<mxCell, Integer> allStates;

    private FiniteStateAutomatonGraph automaton;

    /**
     * A constructor of ExportXML, with the parameter graph, and that raises an exception
     *
     * @param automaton The graph of application
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     */
    public ExportXML(FiniteStateAutomatonGraph automaton) throws ParserConfigurationException, TransformerConfigurationException {
        this.automaton = automaton;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.document = builder.newDocument();
        this.racine = document.createElement("root");
        document.appendChild(racine);
        allStates = new HashMap<>();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        this.transformer = transformerFactory.newTransformer();
    }


    /**
     * Convert the graph to XML and save the result to an (.xml) file with the specified path in parameter
     *
     * @param file The path to saving the file
     */
    public void exportAutomate(String file) {
        Element stateList = document.createElement("liste_etats");
        racine.appendChild(stateList);
        int i = 0;
        for (mxCell state : automaton.getAllState()) {
            Element xmlState = document.createElement("etat");
            xmlState.setAttribute("id", String.valueOf(i));
            Element nom = document.createElement("nom");
            Element cooX = document.createElement("cooX");
            Element cooY = document.createElement("cooY");
            Element beginState = document.createElement("beginState");
            Element finalState = document.createElement("finalState");
            xmlState.appendChild(nom);
            xmlState.appendChild(cooX);
            xmlState.appendChild(cooY);
            xmlState.appendChild(beginState);
            xmlState.appendChild(finalState);

            boolean initial = ((StateInfo) state.getValue()).isStarting();
            boolean accepted = ((StateInfo) state.getValue()).isAccepting();

            nom.appendChild(document.createTextNode(((StateInfo) state.getValue()).getLabel()));
            cooX.appendChild(document.createTextNode(String.valueOf((int) state.getGeometry().getX())));
            cooY.appendChild(document.createTextNode(String.valueOf((int) state.getGeometry().getY())));
            beginState.appendChild(document.createTextNode(String.valueOf(initial)));
            finalState.appendChild(document.createTextNode(String.valueOf(accepted)));
            double rotation = mxUtils.getDouble(automaton.getCellStyle(state), mxConstants.STYLE_ROTATION, 0.0D);
            if (rotation != 0.0D) {
                Element style = document.createElement("style");
                xmlState.appendChild(style);
                style.appendChild(document.createTextNode(mxConstants.STYLE_ROTATION + "=" + rotation));
            }
            allStates.put(state, i);
            stateList.appendChild(xmlState);
            i++;
        }

        Element liste_liens = document.createElement("liste_liens");
        racine.appendChild(liste_liens);
        for (mxCell trainsition : automaton.getAllTransition()) {
            Element lien = document.createElement("lien");
            Element etat_depart = document.createElement("etat_depart");
            Element caractere = document.createElement("caractere");
            Element etat_arr = document.createElement("etat_arr");

            lien.appendChild(etat_depart);
            lien.appendChild(caractere);
            lien.appendChild(etat_arr);

            etat_depart.appendChild(document.createTextNode(String.valueOf(allStates.get(trainsition.getSource()))));
            caractere.appendChild(document.createTextNode(String.valueOf(((TransitionInfo) trainsition.getValue()).getLabel())));
            etat_arr.appendChild(document.createTextNode(String.valueOf(String.valueOf(allStates.get(trainsition.getTarget())))));
            liste_liens.appendChild(lien);
        }

        DOMSource source = new DOMSource(document);

        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            transformer.transform(source, new StreamResult(outputStream));
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
