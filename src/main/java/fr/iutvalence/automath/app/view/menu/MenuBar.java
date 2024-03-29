package fr.iutvalence.automath.app.view.menu;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.editor.EditorActions.*;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.view.utils.JMenuItemWithHints;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public abstract class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 8727734888101745133L;

	protected final GUIPanel editor;

	protected MenuBar(GUIPanel editor) {
		this.editor = editor;
		init();
	}

	private void init() {
		JMenu menu = add(new JMenu(mxResources.get("File")));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("New"), new NewAction(), "/img/icon/new.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)));
		addItemsToFile(menu);
		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("Exit"), new ExitAction(), "/img/icon/exit.gif"));

		menu = add(new JMenu(mxResources.get("Edit")));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Undo"), new HistoryAction(true), "/img/icon/undo.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK)));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Redo"), new HistoryAction(false), "/img/icon/redo.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK)));
		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("parallelEdges"), editor.graphLayout("parallelEdges"), "/img/icon/parrallel.gif"));
		menu.addSeparator();
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Cut"), TransferHandler.getCutAction(), "/img/icon/cut.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK)));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Copy"), TransferHandler.getCopyAction(), "/img/icon/copy.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK)));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Paste"), TransferHandler.getPasteAction(), "/img/icon/paste.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK)));
		menu.addSeparator();
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Delete"), mxGraphActions.getDeleteAction(), "/img/icon/delete.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0)));
		menu.addSeparator();
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("SelectAll"), mxGraphActions.getSelectAllAction(), "/img/icon/select.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK)));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("SelectNone"), mxGraphActions.getSelectNoneAction(), "/img/icon/deselectAll.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)));

		menu = add(new JMenu(mxResources.get("View")));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("resetZoom"), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.getGraphComponent().zoomActual();
				editor.zoomUpdate(1);
			}
		})).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0)));
		menu.addSeparator();
		JCheckBoxMenuItem toggleGridItem = new JCheckBoxMenuItem();
		toggleGridItem.setAction(new AbstractAction(mxResources.get("EnableGrid")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				graph.setGridEnabled(! graph.isGridEnabled());
				toggleGridItem.setState(graph.isGridEnabled());
				graphComponent.setGridVisible(graph.isGridEnabled());
				graphComponent.repaint();
			}
		});
		menu.add(toggleGridItem);
		menu.add(editor.bind(mxResources.get("organicLayout"), new OrganicAction(), "/img/icon/organic.gif"));
		menu.add(editor.bind(mxResources.get("circleLayout"), new CircularAction(), "/img/icon/circular.gif"));

		menu = add(new JMenu(mxResources.get("WindowStyle")));
		ButtonGroup buttonGroup = new ButtonGroup();
		String defaultLookAndFeel = UIManager.getLookAndFeel().getName();
		UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		for (UIManager.LookAndFeelInfo lookAndFeel : lookAndFeels) {
			final String clazz = lookAndFeel.getClassName();
			JMenuItem currentMenu = menu.add(new JRadioButtonMenuItem(new AbstractAction(lookAndFeel.getName()) {
				private static final long serialVersionUID = 7588919504149148501L;

				public void actionPerformed(ActionEvent e) {
					editor.setLookAndFeel(clazz);
				}
			}));
			buttonGroup.add(currentMenu);
			if (defaultLookAndFeel.equals(lookAndFeel.getName())) {
				currentMenu.setSelected(true);
			}
		}

		menu = add(new JMenu(mxResources.get("Help")));
		JMenuItem aboutButton = new JMenuItem(mxResources.get("OpenAboutFrame"));
		aboutButton.setIcon(new ImageIcon(MenuBar.class.getResource("/img/icon/about.png")));
		aboutButton.addActionListener(e -> editor.about());
		menu.add(aboutButton);
		JMenuItem helpButton = new JMenuItem(editor.bind(mxResources.get("OpenHelpFrame"), new OpenHelpAction(), "/img/icon/help.png"));
		menu.add(helpButton);
	}

	protected abstract void addItemsToFile(JMenu menu);

}
