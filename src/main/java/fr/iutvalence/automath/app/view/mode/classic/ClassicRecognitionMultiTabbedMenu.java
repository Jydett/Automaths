package fr.iutvalence.automath.app.view.mode.classic;

import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.app.bridge.OperableGraph;
import fr.iutvalence.automath.app.editor.EditorActions;
import fr.iutvalence.automath.app.io.in.ImporterRegularExpression;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.SimulationProvider;
import fr.iutvalence.automath.app.view.menu.MultiTabbedMenu;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.view.panel.SimulationPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClassicRecognitionMultiTabbedMenu extends MultiTabbedMenu {

	private static final long serialVersionUID = 4986650029408421333L;
	
	private final ImporterRegularExpression importerExpression;
	private final JTextField expressionText;
	private final AtomicBoolean generating = new AtomicBoolean(false);

	public ClassicRecognitionMultiTabbedMenu(GUIPanel editor) {
		super();
		
		JPanel expressionPanel = new JPanel();
		JPanel processingPanel = new JPanel();
		JPanel simulationPanel = new SimulationPanel(editor, new SimulationProvider(editor));

		simulationPanel.addPropertyChangeListener("SimulationState", (e) -> {
			if (e.getNewValue() != SimulationProvider.SimulationState.END) {
				setEnabledAt(0, false);
				setEnabledAt(1, false);
				setEnabledAt(2, false);
			} else {
				setEnabledAt(0, true);
				setEnabledAt(1, true);
				setEnabledAt(2, true);
			}
		});

		expressionText = new JTextField();
		expressionText.setColumns(50);
		importerExpression = new ImporterRegularExpression(((OperableGraph)editor.getGraphComponent().getGraph()), expressionText);
		
		JButton expressionButton = new JButton(mxResources.get("ImportRegex"));
		expressionButton.setIcon(new ImageIcon(ClassicRecognitionMultiTabbedMenu.class.getResource("/img/icon/inlayGear.gif")));
		expressionButton.setToolTipText(mxResources.get("ImportRegexTip"));
		expressionButton.addActionListener(new ActionListener() {
			final String statusName = mxResources.get("GenerationFromRegularExp");
			@Override
			public void actionPerformed(ActionEvent e) {
				if (! generating.get()) {
					generating.set(true);
					long t0 = System.currentTimeMillis();
					try {
						importerExpression.importAutomaton();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
					generating.set(false);
					editor.setAppStatusText(statusName+" : " + (System.currentTimeMillis() - t0)+ " ms");
				}
			}
		});

		JButton toRegexButton = new JButton(mxResources.get("ExportRegex"));
		toRegexButton.setToolTipText(mxResources.get("ExportRegexTip"));
		toRegexButton.setIcon(new ImageIcon(ClassicRecognitionMultiTabbedMenu.class.getResource("/img/icon/regex.gif")));
		toRegexButton.addActionListener(new ActionListener() {
			final String statusName = mxResources.get("GenerationToRegularExp");

			@Override
			public void actionPerformed(ActionEvent e) {
				if (! generating.get()) {
					generating.set(true);
					long t0 = System.currentTimeMillis();
					FiniteStateAutomatonGraph graph = (FiniteStateAutomatonGraph) (editor.getGraphComponent().getGraph());
					expressionText.setText(graph.getAutomaton().getRegex(graph.getAllState(), graph.getAllTransition()));
					generating.set(false);
					editor.setAppStatusText(statusName+" : " + (System.currentTimeMillis() - t0)+ " ms");
				}
			}
		});

		expressionPanel.add(expressionText);
		expressionPanel.add(expressionButton);
		expressionPanel.add(toRegexButton);

		JButton comp = new JButton(new EditorActions.OrganicAction());
		comp.setIcon(new ImageIcon(ClassicRecognitionMultiTabbedMenu.class.getResource("/img/icon/organic.gif")));
		processingPanel.add(comp);
		JButton circular = new JButton(new EditorActions.CircularAction());
		circular.setIcon(new ImageIcon(ClassicRecognitionMultiTabbedMenu.class.getResource("/img/icon/circular.gif")));
		processingPanel.add(circular);
		processingPanel.add(new JButton(new EditorActions.MinimizeAction(mxResources.get("Minimize"))));
		processingPanel.add(new JButton(new EditorActions.DeterminizeAction(mxResources.get("Determinize"))));
		
		addTab(mxResources.get("Expression"),null, expressionPanel,mxResources.get("ExpressionTip"));
		addTab(mxResources.get("Processing"),null, processingPanel,mxResources.get("ProcessingTip"));
		addTab(mxResources.get("Simulation"),null, simulationPanel,mxResources.get("SimulationTip"));
	}

}
