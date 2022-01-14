package fr.iutvalence.automath.app.view.handler;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import fr.iutvalence.automath.app.editor.EditorActions;
import fr.iutvalence.automath.app.model.Header;
import fr.iutvalence.automath.app.model.Header.UserProfile;
import org.snt.autorex.utils.Tuple;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2008, Gaudenz Alder
 */
public class GuiKeyboardHandler {

	private final Map<KeyStroke, Tuple<String, Action>> customs = new HashMap<>();

	protected void installKeyboardActions(mxGraphComponent graphComponent) {
		InputMap inputMap = this.getInputMap(1);
		SwingUtilities.replaceUIInputMap(graphComponent, 1, inputMap);
		inputMap = this.getInputMap(0);
		SwingUtilities.replaceUIInputMap(graphComponent, 0, inputMap);
		SwingUtilities.replaceUIActionMap(graphComponent, this.createActionMap());
	}

	/**
	 * Return JTree's input map.
	 */
	protected InputMap getInputMap(int condition) {
		InputMap map = null;
		if (condition == 1) {
			map = (InputMap)UIManager.get("ScrollPane.ancestorInputMap");
		} else if (condition == 0) {
			map = new InputMap();
			map.put(KeyStroke.getKeyStroke("F2"), "edit");
			map.put(KeyStroke.getKeyStroke("DELETE"), "delete");
			map.put(KeyStroke.getKeyStroke("UP"), "selectParent");
			map.put(KeyStroke.getKeyStroke("DOWN"), "selectChild");
			map.put(KeyStroke.getKeyStroke("RIGHT"), "selectNext");
			map.put(KeyStroke.getKeyStroke("LEFT"), "selectPrevious");
			map.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "enterGroup");
			map.put(KeyStroke.getKeyStroke("PAGE_UP"), "exitGroup");
			map.put(KeyStroke.getKeyStroke("ENTER"), "expand");
			map.put(KeyStroke.getKeyStroke("BACK_SPACE"), "collapse");
			map.put(KeyStroke.getKeyStroke("control A"), "selectAll");
			map.put(KeyStroke.getKeyStroke("control D"), "selectNone");
			map.put(KeyStroke.getKeyStroke("control X"), "cut");
			map.put(KeyStroke.getKeyStroke("CUT"), "cut");
			map.put(KeyStroke.getKeyStroke("control C"), "copy");
			map.put(KeyStroke.getKeyStroke("COPY"), "copy");
			map.put(KeyStroke.getKeyStroke("control V"), "paste");
			map.put(KeyStroke.getKeyStroke("PASTE"), "paste");
			map.put(KeyStroke.getKeyStroke("control ADD"), "zoomIn");
			map.put(KeyStroke.getKeyStroke("control SUBTRACT"), "zoomOut");
		}
		if (condition == JComponent.WHEN_FOCUSED) {
			map.put(KeyStroke.getKeyStroke("control S"), "save");
			map.put(KeyStroke.getKeyStroke("control shift S"), "saveAs");
			map.put(KeyStroke.getKeyStroke("control N"), "new");
			map.put(KeyStroke.getKeyStroke("control O"), "open");
			map.put(KeyStroke.getKeyStroke("control shift O"), "openTemplate");
			map.put(KeyStroke.getKeyStroke("control Z"), "undo");
			map.put(KeyStroke.getKeyStroke("control Y"), "redo");
			map.put(KeyStroke.getKeyStroke("control shift V"), "selectVertices");
			map.put(KeyStroke.getKeyStroke("control shift E"), "selectEdges");
			for (Map.Entry<KeyStroke, Tuple<String, Action>> entry : customs.entrySet()) {
				map.put(entry.getKey(), entry.getValue().getFirst());
			}
		}
		return map;
	}

	/**
	 * Return the mapping between JTree's input map and JGraph's actions.
	 */
	protected ActionMap createActionMap() {
		ActionMap map = (ActionMap)UIManager.get("ScrollPane.actionMap");
		map.put("edit", mxGraphActions.getEditAction());
		map.put("delete", mxGraphActions.getDeleteAction());
		map.put("collapse", mxGraphActions.getCollapseAction());
		map.put("expand", mxGraphActions.getExpandAction());
		map.put("toBack", mxGraphActions.getToBackAction());
		map.put("toFront", mxGraphActions.getToFrontAction());
		map.put("selectNone", mxGraphActions.getSelectNoneAction());
		map.put("selectAll", mxGraphActions.getSelectAllAction());
		map.put("selectNext", mxGraphActions.getSelectNextAction());
		map.put("selectPrevious", mxGraphActions.getSelectPreviousAction());
		map.put("selectParent", mxGraphActions.getSelectParentAction());
		map.put("selectChild", mxGraphActions.getSelectChildAction());
		map.put("cut", TransferHandler.getCutAction());
		map.put("copy", TransferHandler.getCopyAction());
		map.put("paste", TransferHandler.getPasteAction());
		map.put("zoomIn", mxGraphActions.getZoomInAction());
		map.put("zoomOut", mxGraphActions.getZoomOutAction());
		if (Header.getInstanceOfHeader().getModCode() == UserProfile.CLASSIC) {
			map.put("save", new EditorActions.SaveAction(false));
			map.put("saveAs", new EditorActions.SaveAction(true));
			map.put("new", new EditorActions.NewAction());
			map.put("open", new EditorActions.OpenActionWithPreviewer());
			map.put("openTemplate", new EditorActions.OpenTemplate());
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
		customs.forEach((k, v) -> map.put(v.getFirst(), v.getSecond()));
		return map;
	}

	public void reload(mxGraphComponent graphComponent) {
		installKeyboardActions(graphComponent);
	}

	public void register(KeyStroke key, Action action) {
		customs.put(key, new Tuple<>(UUID.randomUUID().toString(), action));
	}
}
