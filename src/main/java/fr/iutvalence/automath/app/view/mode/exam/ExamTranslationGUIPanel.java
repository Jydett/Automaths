package fr.iutvalence.automath.app.view.mode.exam;

import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.view.panel.StateSelectorPanel;
import fr.iutvalence.automath.app.view.panel.TranslationGUIPanel;

public class ExamTranslationGUIPanel extends TranslationGUIPanel {

	private static final long serialVersionUID = 3319837912024875317L;

	public ExamTranslationGUIPanel() {
		super(mxResources.get("ExamTranslation"));
	}
	
	@Override
	public void initializeTabbedMenu() {
		this.tabbedPane = new ExamTranslationMultiTabbedMenu();
	}
	
	@Override
	public void initializeStateSelectorPanel(mxGraph graph) {
		this.stateSelectorPanel = new StateSelectorPanel();
	}
	
}
