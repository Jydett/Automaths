package fr.iutvalence.automath.app.io.in.helper;

import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class XMLHelper {

    public static void importFromXML(Document document, FiniteStateAutomatonGraph graph, boolean clearBeforeImport) {
        Map<Integer,Object> allStates = new HashMap<>();
        Element root = document.getDocumentElement();
        graph.getModel().beginUpdate();
        if (clearBeforeImport) graph.deleteAllElements();

        Element etatsNode = (Element) root.getElementsByTagName("liste_etats").item(0);
        NodeList listEtat = etatsNode.getElementsByTagName("etat");
        int nbEtat = listEtat.getLength();
        for (int etat_index = 0; etat_index < nbEtat; etat_index++) {
            if (listEtat.item(etat_index).getNodeType() == Node.ELEMENT_NODE) {
                Element state = (Element) listEtat.item(etat_index);
                String name = (state.getElementsByTagName("nom").item(0).getTextContent());
                int id = Integer.parseInt(state.getAttribute("id"));
                int x = Optional.ofNullable(state.getElementsByTagName("cooX").item(0)).map(Node::getTextContent).map(Integer::parseInt).orElse(0);
                int y = Optional.ofNullable(state.getElementsByTagName("cooY").item(0)).map(Node::getTextContent).map(Integer::parseInt).orElse(0);
                boolean initial = Optional.ofNullable(state.getElementsByTagName("beginState").item(0)).map(Node::getTextContent).map(Boolean::parseBoolean).orElse(false);
                boolean accept = Optional.ofNullable(state.getElementsByTagName("finalState").item(0)).map(Node::getTextContent).map(Boolean::parseBoolean).orElse(false);
                NodeList nodeStyle = state.getElementsByTagName("style");
                if (nodeStyle.getLength() > 0) {
                    allStates.put(id, addState(name, x, y, accept, initial, graph,
                        nodeStyle.item(0).getTextContent()));
                } else {
                    allStates.put(id, addState(name, x, y, accept, initial, graph));
                }
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
                graph.addEdge(Optional.ofNullable(link.getElementsByTagName("caractere").item(0)).map(Node::getTextContent).orElse(""),
                    allStates.get(etat_depart), allStates.get(etat_arr));
            }
        }
        graph.getModel().endUpdate();
    }

    private static Object addState(String id, double x, double y, boolean accept, boolean initial, FiniteStateAutomatonGraph graph) {
        return addState(id, x, y, accept, initial, graph, null);
    }

    private static Object addState(String id, double x, double y, boolean accept, boolean initial, FiniteStateAutomatonGraph graph, String style) {
        if (style == null) {
            style = "";
        } else if (style.length() > 0 && style.charAt(style.length() - 1) != ';') {
            style = style + ";";
        }
        if (accept && initial) {
            style = style + FiniteStateAutomatonGraph.STYLE_FINAL_BEGIN_STATE;
        } else if (accept) {
            style = style + FiniteStateAutomatonGraph.STYLE_FINAL_STATE;
        } else if (initial) {
            style = style + FiniteStateAutomatonGraph.STYLE_BEGIN_STATE;
        } else {
            style = style + FiniteStateAutomatonGraph.STYLE_DEFAULT_STATE;
        }
        return graph.insertState(graph.getDefaultParent(), id, x, y, style, accept, initial);
    }
}
