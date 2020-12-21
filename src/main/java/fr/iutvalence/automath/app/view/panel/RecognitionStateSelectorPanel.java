package fr.iutvalence.automath.app.view.panel;

import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.StateInfo;

import javax.swing.*;

public class RecognitionStateSelectorPanel extends StateSelectorPanel {

	private static final long serialVersionUID = -153937820319654186L;
	private final JButton finalState = new JButton();
	private final JButton finalBeginState = new JButton();
	
	public RecognitionStateSelectorPanel() {
		super();
		init();
	}
	
	private void init() {
		configureDraggableButton(finalState, "/img/final_state.png",
				defaultState(FiniteStateAutomatonGraph.STYLE_FINAL_BEGIN_STATE),
				new StateInfo(true, false, ""));
		add(finalState);
		
		configureDraggableButton(finalBeginState, "/img/begin_final_state.png",
				defaultState(FiniteStateAutomatonGraph.STYLE_FINAL_BEGIN_STATE),
				new StateInfo(true, true, ""));
		add(finalBeginState);
	}
	
}
