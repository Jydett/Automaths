package fr.iutvalence.automath.app.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.mxgraph.layout.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;

import fr.iutvalence.automath.app.bridge.AutoMathBasicAutomatonProvider;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.view.frame.AboutFrame;
import fr.iutvalence.automath.app.view.handler.GuiKeyboardHandler;
import fr.iutvalence.automath.app.view.handler.UndoHandler;
import fr.iutvalence.automath.app.view.menu.PopUpMenu.TargetType;
import fr.iutvalence.automath.app.view.CellEditor;
import fr.iutvalence.automath.app.view.menu.PopUpMenu;

public abstract class GUIPanel extends JPanel {
	
	private static final long serialVersionUID = -8393951230686521192L;

	protected mxGraphComponent graphComponent;

	protected JLabel positionBar;

	protected File currentFile;

	protected boolean modified;

	private mxRubberband rubberband;

	private mxKeyboardHandler keyboardHandler;
	
	protected JTabbedPane tabbedPane;

	private JFrame frame;

	protected String appTitle;

	protected CellDescriptorPane cellDescriptorPanel;

	private JPanel statusBar;

	private JLabel appStatus;

	private JSplitPane userActionPanel;

	protected StateSelectorPanel stateSelectorPanel;

	private UndoHandler undoHandler;

	protected boolean allowMinimisation;

	protected boolean allowDeterminisation;

	public mxGraphLayout defaultParallelEdgeLayout;
	
	public GUIPanel(String appTitle, mxGraphComponent component) {
		super();
		this.appTitle = appTitle;
		this.graphComponent = component;
		final mxGraph graph = graphComponent.getGraph();
		initialiseOperationPermissions();
		initialiseStatusBar();
		this.rubberband = new mxRubberband(graphComponent);
		this.keyboardHandler = new GuiKeyboardHandler(graphComponent);
		initializeTabbedMenu();
		this.tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		//this.stateSelectorPanel = new StateSelectorPanel(graph);
		initializeStateSelectorPanel(graph);
		this.cellDescriptorPanel = new CellDescriptorPane(graph);
		initialiseUserActionPanel();
		setLayout(new BorderLayout());
		constructEditor();
		undoHandler = new UndoHandler();
		undoHandler.initialize(graph, this);
		installListeners();
		defaultParallelEdgeLayout = new mxParallelEdgeLayout(graph, 50);
		this.modified = false;
	}

	public void initialiseOperationPermissions() {
		this.allowDeterminisation = true;
		this.allowMinimisation = true;
	}

	private void initialiseUserActionPanel() {
		this.userActionPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,stateSelectorPanel,cellDescriptorPanel);
		this.userActionPanel.setResizeWeight(0);
		this.userActionPanel.setDividerSize(2);
		this.userActionPanel.setBorder(null);	
	}

	private void constructEditor() {
		add(statusBar, BorderLayout.SOUTH);
		add(tabbedPane, BorderLayout.NORTH);
		add(userActionPanel,BorderLayout.WEST);
		add(graphComponent, BorderLayout.CENTER);
	}

	private void initialiseStatusBar() {
		this.positionBar = new JLabel("");
		this.appStatus = new JLabel(mxResources.get("Ready"));

		this.statusBar = new JPanel();
		this.statusBar.setLayout(new BorderLayout());
		this.statusBar.add(positionBar,BorderLayout.EAST);
		this.statusBar.add(appStatus,BorderLayout.WEST);

		this.statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
	}

	public abstract void initializeTabbedMenu();
	
	public abstract void initializeStateSelectorPanel(mxGraph graph);
	
	public GUIPanel(String mode) {
		this(mxResources.get("AppName") + " (" + mode + ")", initializeComponent(new FiniteStateAutomatonGraph(new AutoMathBasicAutomatonProvider())));
	}

	private static mxGraphComponent initializeComponent(FiniteStateAutomatonGraph graph) {
		mxGraphComponent gc = new mxGraphComponent(graph) {
			private static final long serialVersionUID = -3536249402710213986L;

			protected mxICellEditor createCellEditor() {
				return new CellEditor(this);
			}
		};
		gc.setPageVisible(false);
		gc.setEnterStopsCellEditing(true);
		gc.setGridVisible(false);
		gc.setToolTips(true);
		gc.getViewport().setBackground(new Color(247,247,247));
		gc.getConnectionHandler().setCreateTarget(false);
		return gc;
	}

	public void updateTitle(){
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null){
			String title = (currentFile != null) ? currentFile.getName() : mxResources.get("newAutomaton");
			if (modified) {
				title += "*";
			}
			frame.setTitle(appTitle + " - " + title);
		}
	}

	public void setModified(boolean b) {
		this.modified = b;
	}

	public JFrame createFrame(JMenuBar menuBar){
		JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		try {
			frame.setIconImage(new ImageIcon(GUIPanel.class.getResource("/img/icon/logo.png")).getImage());
		} catch(Exception e) {
			e.printStackTrace();
		}
		this.frame = frame;
		updateTitle();

		this.frame.pack();
		return frame;
	}

	public void setRubberBandEnabled(boolean b) {
		this.rubberband.setEnabled(b);
	}

	public void displayMessage(String message,String title,int messageType) {
		JOptionPane.showMessageDialog(frame, message, title, messageType);
	}

	public Action bind(String name, final Action action){
		return bind(name, action, null);
	}

	@SuppressWarnings("serial")
	public Action bind(String name, final Action action, String iconUrl){
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(GUIPanel.class.getResource(iconUrl)) : null) {
			public void actionPerformed(ActionEvent e){
				action.actionPerformed(new ActionEvent(getGraphComponent(), e.getID(), e.getActionCommand()));
			}
		};
		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		return newAction;
	}

	public void exit(){
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);
		if (frame != null){
			frame.dispose();
		}
	}

	public UndoHandler getUndoManager() {
		return undoHandler;
	}

	protected void mouseWheelMovedEvent(MouseWheelEvent e) {
		double scale = graphComponent.getGraph().getView().getScale();

		if (e.getWheelRotation() < 0) {
			if(scale < 5)
				graphComponent.zoomIn();
		} else {
			graphComponent.zoomOut();
		}
		setAppStatusText(mxResources.get("Scale") + " : "
				+ (int) (100 * scale)
				+ "%");
	}

	public abstract void installListeners();

	protected void installRepaintListener() {
		graphComponent.getGraph().addListener(mxEvent.REPAINT, (source, evt) -> {
			String buffer = (graphComponent.getTripleBuffer() != null) ? ""
					: " (unbuffered)";
			mxRectangle dirty = (mxRectangle) evt
					.getProperty("region");

			if (dirty == null){
				setAppStatusText("Repaint all" + buffer);
			}
			else{
				appStatus.setText("Repaint: x=" + (int) (dirty.getX()) + " y="
						+ (int) (dirty.getY()) + " w="
						+ (int) (dirty.getWidth()) + " h="
						+ (int) (dirty.getHeight()) + buffer);
			}
		});
	}

	protected void mouseLocationChanged(MouseEvent e){
		positionBar.setText(e.getX() + ", " + e.getY());
	}

	protected void showGraphPopupMenu(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Object cell = graphComponent.getCellAt(x, y);
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), frame);

		//Interaction menu on the chart with basic changes such as copy, paste etc.
		PopUpMenu popUpMenu;
		if(cell != null) {
			if(((mxCell)cell).isVertex()) {
				popUpMenu = new PopUpMenu(this, ((mxCell)cell), TargetType.State);
				popUpMenu.show(SwingUtilities.windowForComponent(this),pt.x,pt.y);}
		} else {
			popUpMenu = new PopUpMenu(this, null, TargetType.GraphComponent);
			popUpMenu.show(SwingUtilities.windowForComponent(this),pt.x,pt.y);
		}
		e.consume();
	}

	public void setLookAndFeel(String clazz) {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			try {
				UIManager.setLookAndFeel(clazz);
				SwingUtilities.updateComponentTreeUI(frame);

				// Needs to assign the key bindings again
				keyboardHandler = new mxKeyboardHandler(graphComponent);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@SuppressWarnings("serial")
	public Action graphLayout(final String key) {
		final mxIGraphLayout layout = createLayout(key);

		if (layout != null) {
			return new AbstractAction(mxResources.get(key)) {
				public void actionPerformed(ActionEvent e) {
					long t0 = System.currentTimeMillis();
					eraseAllGeometryPoint();
					final mxGraph graph = graphComponent.getGraph();
					Object cell = graph.getSelectionCell();
					
					if (cell == null || graph.getModel().getChildCount(cell) == 0) {
						cell = graph.getDefaultParent();
					}
					
					graph.getModel().beginUpdate();
					try {
						layout.execute(cell);
						setAppStatusText(mxResources.get(key)+" : " + (System.currentTimeMillis() - t0)
								+ " ms");
					} finally {
						graph.getModel().endUpdate();
					}
				}
			};
		} else {
			return new AbstractAction(mxResources.get(key)) {
				public void actionPerformed(ActionEvent e) {
					displayMessage(mxResources.get("noLayout"), "", JOptionPane.ERROR_MESSAGE);
				}
			};
		}
	}

	private mxIGraphLayout createLayout(String ident) {
		mxIGraphLayout layout = null;

		if (ident != null) {
			mxGraph graph = graphComponent.getGraph();
			switch (ident) {
				case "verticalHierarchical":
					layout = new mxHierarchicalLayout(graph);
					break;
				case "horizontalHierarchical":
					layout = new mxHierarchicalLayout(graph, JLabel.WEST);
					break;
				case "verticalTree":
					layout = new mxCompactTreeLayout(graph, false);
					break;
				case "horizontalTree":
					layout = new mxCompactTreeLayout(graph, true);
					break;
				case "parallelEdges":
					layout = defaultParallelEdgeLayout;
					break;
				case "placeEdgeLabels":
					layout = new mxEdgeLabelLayout(graph);
					break;
				case "organicLayout":
					layout = new mxOrganicLayout(graph);
					break;
				case "verticalPartition":
					layout = new mxPartitionLayout(graph, false) {
						/**
						 * Overrides the empty implementation to return the size of the
						 * graph control.
						 */
						public mxRectangle getContainerSize() {
							return graphComponent.getLayoutAreaSize();
						}
					};
					break;
				case "horizontalPartition":
					layout = new mxPartitionLayout(graph, true) {
						/**
						 * Overrides the empty implementation to return the size of the
						 * graph control.
						 */
						public mxRectangle getContainerSize() {
							return graphComponent.getLayoutAreaSize();
						}
					};
					break;
				case "verticalStack":
					layout = new mxStackLayout(graph, false) {
						/**
						 * Overrides the empty implementation to return the size of the
						 * graph control.
						 */
						public mxRectangle getContainerSize() {
							return graphComponent.getLayoutAreaSize();
						}
					};
					break;
				case "horizontalStack":
					layout = new mxStackLayout(graph, true) {
						/**
						 * Overrides the empty implementation to return the size of the
						 * graph control.
						 */
						public mxRectangle getContainerSize() {
							return graphComponent.getLayoutAreaSize();
						}
					};
					break;
				case "circleLayout":
					layout = new mxCircleLayout(graph);
					break;
			}
		}
		return layout;
	}
	
	public void about() {
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null) {
			AboutFrame about = new AboutFrame(frame);
			about.setModal(true);
			try {
				frame.setIconImage(new ImageIcon(GUIPanel.class.getResource("/img/icon/logo.png")).getImage());
			} catch(Exception e) {
				e.printStackTrace();
			}
			int x = frame.getX() + (frame.getWidth() - about.getWidth()) / 2;
			int y = frame.getY() + (frame.getHeight() - about.getHeight()) / 2;
			about.setLocation(x, y);


			about.setVisible(true);
		}
	}

	public void eraseAllGeometryPoint() {
		mxGraph graph = this.graphComponent.getGraph();
		Object[] cells = graph.getChildCells(graph.getDefaultParent());
		graph.getModel().beginUpdate();
		try {
			for (Object oCell : cells) {
				mxCell cell = (mxCell) oCell;
				if (cell.isEdge()) {
					mxGeometry geo = (mxGeometry) cell.getGeometry().clone();
					List<mxPoint> points = geo.getPoints();
					if (points != null && !points.isEmpty()) {
						geo = (mxGeometry) geo.clone();
						geo.setPoints(null);
						graph.getModel().setGeometry(cell, geo);
					}
				}
			}
		} finally {
			graph.getModel().endUpdate();
		}
	}

	public CellDescriptorPane getCellDescriptorPanel() {
		return cellDescriptorPanel;
	}
	
	public mxGraphComponent getGraphComponent() {
		return this.graphComponent;
	}
	
	public void setAppStatusText(String newStatus) {
		this.appStatus.setText(newStatus);
	}
	
	public boolean getAllowMinimisation() {
		return this.allowMinimisation;
	}
	
	public boolean getAllowDeterminisation() {
		return this.allowDeterminisation;
	}

	public File getCurrentFile() {
		return this.currentFile;
	}

	public void setCurrentFile(File file) {
		this.currentFile = file;
		updateTitle();
	}
	
	private JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	public boolean isUnmodified() {
		return ! this.modified;
	}
}