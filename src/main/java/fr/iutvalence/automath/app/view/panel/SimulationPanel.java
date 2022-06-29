package fr.iutvalence.automath.app.view.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.mxgraph.util.mxResources;

import fr.iutvalence.automath.app.model.SimulationProvider;
import fr.iutvalence.automath.app.model.SimulationProvider.SimulationState;
import fr.iutvalence.automath.app.exceptions.GraphIsEmptyException;
import fr.iutvalence.automath.app.exceptions.NextCallOnNonStartedSimulationException;
import fr.iutvalence.automath.app.exceptions.NoInitialStateException;
import fr.iutvalence.automath.app.exceptions.WordIsEmptyException;

/**
 * The graphical display of the graphical simulation that works with {@link SimulationProvider}
 */
public class SimulationPanel extends JPanel {

	private static final long serialVersionUID = 3671333670496340210L;

	/**
	 * The next action
	 */
	private final ActionListener nextAction;

	/**
	 * The start action
	 */
	private final ActionListener beginAction;

	/**
	 * The IHM with functionality
	 */
	private final GUIPanel editor;

	/**
	 * The algorithm that deals with the simulation of the automaton
	 */
	private final SimulationProvider simulationProvider;

	/**
	 * A constructor of SimulationPanel, with the parameter the IHM and a provider
	 * @param ep The IHM with functionality
	 * @param simulation The simulation of the automaton
	 */
	public SimulationPanel(GUIPanel ep, SimulationProvider simulation) {
		this.simulationProvider = simulation;
		//elements du panel Simulation
		JTextFieldCustom word = new JTextFieldCustom(simulation);  // Allocate JTextArea
		JScrollPane tAreaScrollPane = new JScrollPane(word);  // Allocate JScrollPane which wraps the JTextArea
		tAreaScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		word.setMargin(new Insets(3, 3, 3, 3));
		word.setFont(new Font("Monospaced", Font.PLAIN, 12));
		word.setColumns(20);

		add(tAreaScrollPane);

		JButton start = new JButton(mxResources.get("Simulation.Start"));
		start.setIcon(new ImageIcon(SimulationPanel.class.getResource("/img/icon/execute.gif")));

		JButton reset = new JButton(mxResources.get("Simulation.Reset"));
		reset.setIcon(new ImageIcon(SimulationPanel.class.getResource("/img/icon/rollback.gif")));

		JButton toTheEnd = new JButton(mxResources.get("Simulation.ToTheEnd"));
		toTheEnd.setIcon(new ImageIcon(SimulationPanel.class.getResource("/img/icon/resume.gif")));

		reset.setEnabled(false);
		editor = ep;

		nextAction = arg0 -> {
			try {
				SimulationState s = simulation.next();
				word.repaint();
				if (s != SimulationState.RUNNING) {
					start.setEnabled(false);
					toTheEnd.setEnabled(false);
				}
				displaySimulationMessage(s);
			} catch (NextCallOnNonStartedSimulationException e) {
				e.printStackTrace();
			}
		};

		beginAction = arg0 -> {
			try {
				simulation.start(word.getText());
				firePropertyChange("SimulationState", SimulationState.END, SimulationState.RUNNING);
				editor.getUndoManager().setUndoAllowed(false);
				ep.getGraphComponent().getGraph().clearSelection();
				ep.getGraphComponent().setEnabled(false);
				ep.setRubberBandEnabled(false);
				word.setEditable(false);
				word.setCurrentChar();
				word.setSimulation(true);
				start.removeActionListener(start.getActionListeners()[0]);
				start.addActionListener(nextAction);
				start.setText(mxResources.get("Simulation.Next"));
				reset.setEnabled(true);
				editor.setAppStatusText(mxResources.get("SimulationRunning"));
			} catch (WordIsEmptyException e1) {
				editor.displayMessage(mxResources.get("ErrNoWordTip"),mxResources.get("Error")+mxResources.get("ErrNoWord"), JOptionPane.ERROR_MESSAGE);
			} catch (GraphIsEmptyException e1) {
				editor.displayMessage(mxResources.get("ErrEmptyAutomatonTip"),mxResources.get("Error")+mxResources.get("ErrEmptyAutomaton"), JOptionPane.ERROR_MESSAGE);
			} catch (NoInitialStateException e1) {
				editor.displayMessage(mxResources.get("ErrNoStartingTip"),mxResources.get("Error")+mxResources.get("ErrNoStarting"), JOptionPane.ERROR_MESSAGE);
			}
		};

		/*
		 * The final action
		 */
		ActionListener endAction = arg0 -> {
			firePropertyChange("SimulationState", SimulationState.RUNNING, SimulationState.END);
			ep.getGraphComponent().setEnabled(true);
			simulation.reset();
			ep.setRubberBandEnabled(true);
			start.setEnabled(true);
			toTheEnd.setEnabled(true);
			word.setEditable(true);
			word.setReset();
			word.setSimulation(false);
			editor.getUndoManager().setEventsEnabled(true);
			start.setText(mxResources.get("Simulation.Start"));
			start.removeActionListener(start.getActionListeners()[0]);
			start.addActionListener(beginAction);
			reset.setEnabled(false);
		};

		/*
		 * The last word in the simulation
		 */
		ActionListener toTheEndAction = e -> {
			try {
				displaySimulationMessage(simulation.toTheEnd(word.getText()));
				ep.getGraphComponent().getGraph().clearSelection();
				ep.getGraphComponent().setEnabled(false);
				ep.setRubberBandEnabled(false);
				word.setEditable(false);
				word.setCurrentChar();
				word.setSimulation(true);
				start.setEnabled(false);
				toTheEnd.setEnabled(false);
				reset.setEnabled(true);
			} catch (WordIsEmptyException e1) {
				editor.displayMessage(mxResources.get("ErrNoWordTip"), mxResources.get("Error") + mxResources.get("ErrNoWord"), JOptionPane.ERROR_MESSAGE);
			} catch (GraphIsEmptyException e1) {
				editor.displayMessage(mxResources.get("ErrEmptyAutomatonTip"), mxResources.get("Error") + mxResources.get("ErrEmptyAutomaton"), JOptionPane.ERROR_MESSAGE);
			} catch (NoInitialStateException e1) {
				editor.displayMessage(mxResources.get("ErrNoStartingTip"), mxResources.get("Error") + mxResources.get("ErrNoStarting"), JOptionPane.ERROR_MESSAGE);
			}
		};

		start.addActionListener(beginAction);
		add(start);

		reset.addActionListener(endAction);
		add(reset);

		toTheEnd.addActionListener(toTheEndAction);
		add(toTheEnd);
	}

	/**
	 * The window display according to the progress of the simulation
	 * @param s The algorithm that deals with the simulation of the automaton
	 */
	private void displaySimulationMessage(SimulationState s) {
		String title, text;
		int messageType;

		switch (s) {
		case RUNNING:return;
		case END: text = mxResources.get("SimulationEnd");
				  title = mxResources.get("SimulationEndTitle");
				  messageType = JOptionPane.PLAIN_MESSAGE;
			break;
		case NO_STATE_FOUND: text = mxResources.get("SimulationStateNotFound");
		                   title = mxResources.get("SimulationStateNotFoundTitle");
		                   messageType = JOptionPane.ERROR_MESSAGE;
			break;
		case ACCEPTED: text = mxResources.get("SimulationAccepted");
		               title = mxResources.get("SimulationAcceptedTitle");
		               messageType = JOptionPane.PLAIN_MESSAGE;
			break;
		default: text = "";
				 title = "";
				 messageType = 0;

		}
		editor.setAppStatusText(title);
		editor.displayMessage(text,title, messageType);
	}

	/**
	 * To recover the automaton simulator
	 * @return The simulator of automaton
	 */
	public SimulationProvider getSimulationProvider() {
		return simulationProvider;
	}

	/**
	 * Redefinition of the class JTextArea with a cursor on the position of advancement in the word simulate
	 */
    static class JTextFieldCustom extends JTextArea {
		/**
		 *
		 */
		private static final long serialVersionUID = -5881074223387485393L;
		/**
		 * The index of the character current of the simulation in the word
		 */
		private int index;
		/**
		 * <code>true</code> if we are in simulation;
		 * <code>false</code> otherwise.
		 */
		private boolean isSimulation;
		/**
		 * The algorithm that deals with the simulation of the automaton
		 */
		private final SimulationProvider simulation;

		/**
		 * A constructor of JTextFieldCustom, with the parameter the simulation
		 * @param simulation The simulation of the automaton
		 */
		public JTextFieldCustom(SimulationProvider simulation) {
			super();
			index = 0;
			this.simulation = simulation;
		}

		/**
		 * update the current character
		 */
		public void setCurrentChar() {
			index = simulation.getLoopNumber()-1;
		}

		/**
		 * Resets the character indexing
		 */
		public void setReset() {
			index = 0;
		}

		/**
		 * To set the status of the simulation if we are in simulation or not
		 * @param sim 	<code>true</code> if we are in simulation;
		 *            	<code>false</code> otherwise.
		 */
		public void setSimulation(boolean sim) {
			isSimulation = sim;
		}

		protected void paintComponent(Graphics g) {
		     super.paintComponent(g);

		     setCurrentChar();
		     FontMetrics metrics = g.getFontMetrics();
		     if (isSimulation) {
		    	 int blank = this.getMargin().left;
		    	 int marginTop = this.getInsets().top + this.getMargin().top;
		    	 int marginBottom = this.getMargin().bottom - 1;
			     int sizeChar = sizeCurrentChar(metrics,super.getText());
		    	 int margin = offsetCharCursor(metrics, super.getText());
		    	 int topCursor = 1;
			     Polygon p = new Polygon();

			     if (simulation.isStatePhase()) {
				     p.addPoint(blank + margin+sizeChar - 3,topCursor);
				     p.addPoint(blank + margin+sizeChar + 3,topCursor);
				     p.addPoint(blank + margin+sizeChar,marginTop);
			     } else {
				     p.addPoint(blank + margin + (sizeChar / 2) - 3,topCursor);
				     p.addPoint(blank + margin + (sizeChar / 2) + 3,topCursor);
				     p.addPoint(blank + margin + (sizeChar / 2),marginTop);
			     }

			     g.setColor(new Color( 189, 195, 199 ));
			     g.fillPolygon(p);
			     g.drawPolygon(p);
			     g.setColor(Color.RED);
			     if (simulation.isStatePhase()) {
					 g.drawRect(blank + margin + sizeChar, marginTop, 0, metrics.getAscent() - marginBottom);
				 } else {
					 g.drawRect(blank + margin,marginTop, sizeChar, metrics.getAscent() - marginBottom);
				 }
		     }
		}

		/**
		 * Calculate the size of the current character
		 * @param metrics The information of the font
		 * @param text The word
		 * @return The size in pixel
		 */
		private int offsetCharCursor(FontMetrics metrics, String text) {
			if (index >= text.length()) {
				return metrics.stringWidth(new StringBuffer(text).substring(0, index - 1));
			}
			if (index == -1) {
				return metrics.stringWidth(new StringBuffer(text).substring(0, 0));
			}
			return metrics.stringWidth(new StringBuffer(text).substring(0, index));
		}

		/**
		 * Calculates the total size from the beginning of the character string to the END
		 * @param metrics The information of the font
		 * @param text The word
		 * @return The size in pixel
		 */
		private int sizeCurrentChar(FontMetrics metrics, String text) {
			if (index == -1) {
				return metrics.stringWidth(new StringBuffer(text).substring(0, 0));
			} else if (index + 1 > text.length()) {
				return metrics.stringWidth(new StringBuffer(text).substring(index - 1, index));
			} else {
				return metrics.stringWidth(new StringBuffer(text).substring(index, index + 1));
			}
		}
	}
}
