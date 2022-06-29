package fr.iutvalence.automath.app.view.mode.classic;

import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.view.panel.RecognitionGUIPanel;
import fr.iutvalence.automath.app.view.panel.RecognitionStateSelectorPanel;

public class ClassicRecognitionGUIPanel extends RecognitionGUIPanel {

	private static final long serialVersionUID = -2152847047697336042L;

	public ClassicRecognitionGUIPanel() {
		super(mxResources.get("ClassicRecognition"));
	}

	@Override
	public void initializeTabbedMenu() {
		this.tabbedPane = new ClassicRecognitionMultiTabbedMenu(this);
	}

	@Override
	public void initializeStateSelectorPanel(mxGraph graph) {
		this.stateSelectorPanel = new RecognitionStateSelectorPanel(graph);
	}

}
