package fr.iutvalence.automath.app.view.panel;

import com.mxgraph.view.mxGraph;

public abstract class TranslationGUIPanel extends GUIPanel {

	private static final long serialVersionUID = 1052680525362724789L;

	public TranslationGUIPanel(String mode) {
		super(mode);
	}
	
	public abstract void initializeTabbedMenu();
	
	public abstract void initializeStateSelectorPanel(mxGraph graph);
}

