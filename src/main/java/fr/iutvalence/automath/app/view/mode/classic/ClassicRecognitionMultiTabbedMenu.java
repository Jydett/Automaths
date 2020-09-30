package fr.iutvalence.automath.app.view.mode.classic;

import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.app.bridge.InterfaceAutoMathBasicGraph;
import fr.iutvalence.automath.app.editor.EditorActions;
import fr.iutvalence.automath.app.io.in.ImporterRegularExpression;
import fr.iutvalence.automath.app.model.SimulationProvider;
import fr.iutvalence.automath.app.view.menu.MultiTabbedMenu;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.view.panel.SimulationPanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassicRecognitionMultiTabbedMenu extends MultiTabbedMenu {

	private static final long serialVersionUID = 4986650029408421333L;
	
	private final ImporterRegularExpression importerExpression;
	
	public ClassicRecognitionMultiTabbedMenu(GUIPanel editor) {
		super(editor);
		
		JPanel expressionPanel = new JPanel();
		JPanel processingPanel = new JPanel();
		JPanel simulationPanel = new SimulationPanel(editor, new SimulationProvider(editor));
		
		JTextField expressionText = new JTextField();
		expressionText.setColumns(15);
		importerExpression = new ImporterRegularExpression(((InterfaceAutoMathBasicGraph)editor.getGraphComponent().getGraph()), expressionText);
		
		JButton expressionButton = new JButton(mxResources.get("ImportRegex"));
		expressionButton.setToolTipText(mxResources.get("ImportRegexTip"));
		expressionButton.addActionListener(new ActionListener() {
			final String statusName = mxResources.get("GenerationFromRegularExp");
			private boolean isGenerating = false;
			@Override
			public void actionPerformed(ActionEvent e) {
				if (! isGenerating) {
					isGenerating = true;
					long t0 = System.currentTimeMillis();
					try {
						importerExpression.importAutomaton();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
					editor.setAppStatusText(statusName+" : " + (System.currentTimeMillis() - t0)+ " ms");
					isGenerating = false;
				}
			}
		});

		JButton toRegexButton = new JButton(mxResources.get("ExportRegex"));
		toRegexButton.setToolTipText(mxResources.get("ExportRegexTip"));
		toRegexButton.addActionListener(new ActionListener() {
			final String statusName = mxResources.get("GenerationToRegularExp");
			private boolean isGenerating = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (! isGenerating) {
					isGenerating = true;
					long t0 = System.currentTimeMillis();
					isGenerating = false;
					editor.setAppStatusText(statusName+" : " + (System.currentTimeMillis() - t0)+ " ms");
				}
			}
		});

		expressionPanel.add(expressionText);
		expressionPanel.add(expressionButton);
		expressionPanel.add(toRegexButton);

		processingPanel.add(new JButton(new EditorActions.OrganicAction()));
		processingPanel.add(new JButton(new EditorActions.CircularAction()));
		processingPanel.add(new JButton(new EditorActions.MinimizeAction(mxResources.get("Minimize"))));
		processingPanel.add(new JButton(new EditorActions.DeterminizeAction(mxResources.get("Determinize"))));
		
		addTab(mxResources.get("Expression"),null, expressionPanel,mxResources.get("ExpressionTip"));
		addTab(mxResources.get("Processing"),null, processingPanel,mxResources.get("ProcessingTip"));
		addTab(mxResources.get("Simulation"),null, simulationPanel,mxResources.get("SimulationTip"));
	}

}
