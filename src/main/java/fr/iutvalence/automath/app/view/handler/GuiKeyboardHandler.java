package fr.iutvalence.automath.app.view.handler;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.util.mxGraphActions;

import fr.iutvalence.automath.app.model.Header;
import fr.iutvalence.automath.app.model.Header.UserProfile;
import fr.iutvalence.automath.app.editor.EditorActions;

/**
 * Copyright (c) 2008, Gaudenz Alder
 */
public class GuiKeyboardHandler extends mxKeyboardHandler {

	public GuiKeyboardHandler(mxGraphComponent graphComponent)
	{
		super(graphComponent);
	}

	/**
	 * Return JTree's input map.
	 */
	protected InputMap getInputMap(int condition) {
		InputMap map = super.getInputMap(condition);
		if (condition == JComponent.WHEN_FOCUSED && map != null) {
			map.put(KeyStroke.getKeyStroke("control S"), "save");
			map.put(KeyStroke.getKeyStroke("control shift S"), "saveAs");
			map.put(KeyStroke.getKeyStroke("control N"), "new");
			map.put(KeyStroke.getKeyStroke("control O"), "open");
			map.put(KeyStroke.getKeyStroke("control shift O"), "opentemplate");
			map.put(KeyStroke.getKeyStroke("control Z"), "undo");
			map.put(KeyStroke.getKeyStroke("control Y"), "redo");
			map.put(KeyStroke.getKeyStroke("control shift V"), "selectVertices");
			map.put(KeyStroke.getKeyStroke("control shift E"), "selectEdges");
		}
		return map;
	}

	/**
	 * Return the mapping between JTree's input map and JGraph's actions.
	 */
	protected ActionMap createActionMap() {
		ActionMap map = super.createActionMap();
		if (Header.getInstanceOfHeader().getModCode() == UserProfile.CLASSIC) {
			map.put("save", new EditorActions.SaveAction(false));
			map.put("saveAs", new EditorActions.SaveAction(true));
			map.put("new", new EditorActions.NewAction());
			map.put("open", new EditorActions.OpenActionWithPreviewer());
			map.put("opentemplate", new EditorActions.OpenTemplate());
		} else {
			map.put("save", new EditorActions.SaveExamAction(false));
			map.put("saveAs", new EditorActions.SaveExamAction(true));	
		}
		map.put("new", new EditorActions.NewAction());
		map.put("delete", new EditorActions.DeleteAction());
		map.put("undo", new EditorActions.HistoryAction(true));
		map.put("redo", new EditorActions.HistoryAction(false));
		map.put("selectVertices", mxGraphActions.getSelectVerticesAction());
		map.put("selectEdges", mxGraphActions.getSelectEdgesAction());
		return map;
	}

}
