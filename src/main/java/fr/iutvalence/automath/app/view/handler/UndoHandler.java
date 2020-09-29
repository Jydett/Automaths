package fr.iutvalence.automath.app.view.handler;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.view.panel.GUIPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * A management of the interaction history on the graph
 */
public class UndoHandler extends mxUndoManager {

	/**
	 * The number of action tracked by the undoManager
	 * aka the number of undo you can perform in a raw
	 */
	private final static int HISTORY_SIZE = 20;
	private List<mxUndoableEdit> historySave;
	private int indexOfNextAddSave;
	private int sizeSave;

	/**
	 * Tells if undo is allowed in the current state of the application
	 * if you want to forbid undo, just call setUndoAuthorisation;
	 */
	private boolean undoAllowed;

	public UndoHandler() {
		super(HISTORY_SIZE);
		undoAllowed = true;
		sizeSave = HISTORY_SIZE;
		historySave = new ArrayList<>(HISTORY_SIZE);
		indexOfNextAdd = 0;
	}


	/**
	 * Instantiation of the history graph manager
	 */
	public void initialize(mxGraph graph, GUIPanel editor) {
		// An interface for tracking graphical interaction
		mxEventSource.mxIEventListener changeTracker = (source, evt) -> {
			editor.setModified(true);
			editor.updateTitle();
		};

		//An interface for the follow-up of the history
		mxEventSource.mxIEventListener undoHandler1 =
			((source, evt) -> this.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit")));

		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);
		graph.getModel().addListener(mxEvent.UNDO, undoHandler1);
		graph.getView().addListener(mxEvent.UNDO, undoHandler1);

		mxEventSource.mxIEventListener undoHandler = (source, evt) -> {
			List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) evt
					.getProperty("edit")).getChanges();
			graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
		};
		this.addListener(mxEvent.UNDO, undoHandler);
		this.addListener(mxEvent.REDO, undoHandler);
	}

	@Override
	public void undo() {
		if (undoAllowed) {
			super.undo();
		}
	}

	public void setUndoAllowed(boolean value) {
		this.undoAllowed = value;
		if (value) {
			copyHistory(historySave, super.history);
			super.indexOfNextAdd = indexOfNextAddSave;
			super.size = sizeSave;
		} else {
			copyHistory(super.history, historySave);
			indexOfNextAddSave = super.indexOfNextAdd;
			sizeSave = super.size;
		}
	}

	public void copyHistory(List<mxUndoableEdit> source, List<mxUndoableEdit> destination) {
		destination.clear();
		destination.addAll(source);
	}
}
