package fr.iutvalence.automath.app.view.menu;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import javax.swing.JMenu;
import javax.swing.KeyStroke;

import com.mxgraph.util.mxResources;

import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.view.utils.JMenuItemWithHints;
import fr.iutvalence.automath.app.editor.EditorActions.OpenActionWithPreviewer;
import fr.iutvalence.automath.app.editor.EditorActions.OpenTemplate;
import fr.iutvalence.automath.app.editor.EditorActions.SaveAction;

public class ClassicMenuBar extends MenuBar {

	private static final long serialVersionUID = -537319135111773531L;

	public ClassicMenuBar(GUIPanel editor) {
		super(editor);
	}
	
	protected void addItemsToFile(JMenu menu) {
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Open"), new OpenActionWithPreviewer(), "/img/icon/open.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK)));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("OpenTemplate"), new OpenTemplate(), "/img/icon/open.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));
		menu.addSeparator();
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Save"), new SaveAction(false), "/img/icon/save.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK)));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("SaveAs"), new SaveAction(true), "/img/icon/saveas.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));
	}
	
}
