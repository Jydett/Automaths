package fr.iutvalence.automath.app.view.menu;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.app.editor.EditorActions;
import fr.iutvalence.automath.app.editor.EditorActions.DeleteAction;
import fr.iutvalence.automath.app.editor.EditorActions.ReorderAction;
import fr.iutvalence.automath.app.editor.EditorActions.SetFinalAction;
import fr.iutvalence.automath.app.editor.EditorActions.SetInitialAction;
import fr.iutvalence.automath.app.model.StateInfo;
import fr.iutvalence.automath.app.view.panel.CellDescriptorPane;
import fr.iutvalence.automath.app.view.panel.GUIPanel;

import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;
/**
 * It serves to have a menu when the user do a right click on the automate
 */
public class PopUpMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	/**The description about a component*/
	public CellDescriptorPane descriptor;
	/** The GUI interface pannel*/
	private final GUIPanel editor;

	/**
	 * Build the popup menu about a state
	 * @param editor
	 * @param cell
	 * @param type
	 */
	public PopUpMenu(GUIPanel editor,mxCell cell,TargetType type) {
    	super();
    	this.editor = editor;
    	this.descriptor = editor.getCellDescriptorPanel();
    	
    	update(cell,type);
    }
    /**
     * Return a GUIPanel
     * @return editor
     */
    public GUIPanel getEditor() {
		return editor;
	}
	/**
	 * Update or get properties about the state, a transition or a GraphComponent 
	 * @param cell
	 * @param type
	 */
	private void update(mxCell cell, TargetType type) {
		switch(type) {
			case State :
				StateInfo stInfo =(StateInfo)cell.getValue();
		    	add(new SetInitialAction(mxResources.get("SetInitial"))).setSelected(stInfo.isStarting());
		    	add(new SetFinalAction(mxResources.get("SetFinal"))).setSelected(stInfo.isAccepting());
				addSeparator();
				addReorderAction();
				addSeparator();
				addCopyCutPasteActions();
				addDeleteAction();
				break;
			case Transition :
				add(new EditorActions.ReverseAction(mxResources.get("Reverse")));
				addSeparator();
				addReorderAction();
				addSeparator();
				addCopyCutPasteActions();
				addDeleteAction();
				break;
			case GraphComponent :
		        add(editor.bind(mxResources.get("Paste"), TransferHandler.getPasteAction(),"/img/icon/paste.gif"));
		}
	}

	private void addReorderAction() {
		add(new ReorderAction(mxResources.get("ToFront"), false));
		add(new ReorderAction(mxResources.get("ToBack"), true));
	}

	private void addDeleteAction() {
		add(editor.bind(mxResources.get("Delete"), new DeleteAction(), "/img/icon/delete.gif"));
	}

	private void addCopyCutPasteActions() {
		add(editor.bind(mxResources.get("Copy"), TransferHandler.getCopyAction(), "/img/icon/copy.gif"));
		add(editor.bind(mxResources.get("Cut"), TransferHandler.getCutAction(), "/img/icon/cut.gif"));
		add(editor.bind(mxResources.get("Paste"), TransferHandler.getPasteAction(), "/img/icon/paste.gif"));
	}

	/**
	 * Enumeration of targetType
	 */
	public enum TargetType {
		GraphComponent, State, Transition
	}
	
	
}