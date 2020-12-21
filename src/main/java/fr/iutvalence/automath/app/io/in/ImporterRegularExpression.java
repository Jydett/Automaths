package fr.iutvalence.automath.app.io.in;

import fr.iutvalence.automath.app.bridge.OperableGraph;
import fr.iutvalence.automath.app.editor.EditorActions;

import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

/**
 * Handle the conversion of a regex to a graphical automaton
 */
public class ImporterRegularExpression implements Importer {
	
	/**
	 * The graph of the application 
	 */
	private final OperableGraph graph;

	/**
	 * The text area containing the regular expression
	 */
	private final JTextComponent internalText;

	/**
	 * A constructor of ImporterRegularExpression, with the parameter graph and the text area
	 * @param graph The graph of application
	 * @param internalText The text area used by user
	 */
	public ImporterRegularExpression(OperableGraph graph, JTextComponent internalText){
		this.graph = graph;
		this.internalText = internalText;
	}
	
	/**
	 * Convert the string of characters to a graphical automaton
	 */
	public void importAutomaton(boolean clearBefore) {
		this.graph.getModel().beginUpdate();
		try {
			if (clearBefore) this.graph.deleteAllElements();
			this.graph.importerExpReg(internalText.getText());
			new EditorActions.OrganicAction().actionPerformed(new ActionEvent(internalText, ActionEvent.ACTION_PERFORMED, "Organize"));
		} finally {
			this.graph.getModel().endUpdate();
		}
	}
}
