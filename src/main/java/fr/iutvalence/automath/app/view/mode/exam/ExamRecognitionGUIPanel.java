package fr.iutvalence.automath.app.view.mode.exam;

import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.view.panel.RecognitionGUIPanel;
import fr.iutvalence.automath.app.view.panel.RecognitionStateSelectorPanel;

public class ExamRecognitionGUIPanel extends RecognitionGUIPanel {

	private static final long serialVersionUID = -4885595027804514476L;

	public ExamRecognitionGUIPanel() {
		super(mxResources.get("ExamRecognition"));
	}
	
	@Override
	public void initializeTabbedMenu() {
		this.tabbedPane = new ExamRecognitionMultiTabbedMenu();
	}
	
	@Override
	public void initializeStateSelectorPanel(mxGraph graph) {
		this.stateSelectorPanel = new RecognitionStateSelectorPanel();
	}
	
}
