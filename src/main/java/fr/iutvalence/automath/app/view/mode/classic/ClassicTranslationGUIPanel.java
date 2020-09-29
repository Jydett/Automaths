package fr.iutvalence.automath.app.view.mode.classic;

import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.view.panel.StateSelectorPanel;
import fr.iutvalence.automath.app.view.panel.TranslationGUIPanel;

public class ClassicTranslationGUIPanel extends TranslationGUIPanel {

	private static final long serialVersionUID = 2509105745789044679L;

	public ClassicTranslationGUIPanel() {
		super(mxResources.get("ClassicTranslation"));
	}
	
	@Override
	public void initializeTabbedMenu() {
		this.tabbedPane = new ClassicTranslationMultiTabbedMenu(this);
	}
	
	@Override
	public void initializeStateSelectorPanel(mxGraph graph) {
		this.stateSelectorPanel = new StateSelectorPanel(graph);
	}
	
}
