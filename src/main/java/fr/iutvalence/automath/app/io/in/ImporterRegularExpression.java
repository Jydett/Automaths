package fr.iutvalence.automath.app.io.in;

import java.awt.event.ActionEvent;
import javax.swing.text.JTextComponent;
import fr.iutvalence.automath.app.bridge.InterfaceAutoMathBasicGraph;
import fr.iutvalence.automath.app.editor.EditorActions;

/**
 * Handle the conversion of a regex to a graphical automaton
 */
public class ImporterRegularExpression implements Importer {
	
	/**
	 * The graph of the application 
	 */
	private InterfaceAutoMathBasicGraph graph;

	/**
	 * The text area containing the regular expression
	 */
	private JTextComponent internalText;

	/**
	 * A constructor of ImporterRegularExpression, with the parameter graph and the text area
	 * @param graph The graph of application
	 * @param internalText The text area used by user
	 */
	public ImporterRegularExpression(InterfaceAutoMathBasicGraph graph, JTextComponent internalText){
		this.graph = graph;
		this.internalText = internalText;
	}
	
	/**
	 * Convert the string of characters to a graphical automaton
	 */
	public void importAutomaton(){
		this.graph.getModel().beginUpdate();
		try {
			this.graph.deleteAllElements();
			this.graph.importerExpReg(internalText.getText());
			new EditorActions.OrganicAction().actionPerformed(new ActionEvent(internalText, ActionEvent.ACTION_PERFORMED, "Organize"));
		} finally {
			this.graph.getModel().endUpdate();
		}
	}
}
