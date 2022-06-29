package fr.iutvalence.automath.app.view.panel;

import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.StateInfo;

import javax.swing.*;

public class RecognitionStateSelectorPanel extends StateSelectorPanel {

	private static final long serialVersionUID = -153937820319654186L;
	private final JButton finalState = new JButton();
	private final JButton finalBeginState = new JButton();

	public RecognitionStateSelectorPanel(mxGraph mxGraph) {
		super(mxGraph);
		init(mxGraph);
	}

	private void init(mxGraph graph) {
		StateInfo info = new StateInfo(true, false, "");
		configureDraggableButton(finalState, "/img/final_state.png",
				defaultState(FiniteStateAutomatonGraph.STYLE_FINAL_BEGIN_STATE),
				info);
		addSelectedCellModifierOnClick(finalState, graph, info);
		add(finalState);

		StateInfo info1 = new StateInfo(true, true, "");
		configureDraggableButton(finalBeginState, "/img/begin_final_state.png",
				defaultState(FiniteStateAutomatonGraph.STYLE_FINAL_BEGIN_STATE),
				info1);
		addSelectedCellModifierOnClick(finalBeginState, graph, info1);
		add(finalBeginState);
	}

}
