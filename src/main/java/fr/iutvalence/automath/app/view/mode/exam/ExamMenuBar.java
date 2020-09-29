package fr.iutvalence.automath.app.view.mode.exam;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import javax.swing.JMenu;
import javax.swing.KeyStroke;

import com.mxgraph.util.mxResources;

import fr.iutvalence.automath.app.view.panel.GUIPanel;
import fr.iutvalence.automath.app.view.menu.MenuBar;
import fr.iutvalence.automath.app.view.utils.JMenuItemWithHints;
import fr.iutvalence.automath.app.editor.EditorActions.SaveExamAction;

public class ExamMenuBar extends MenuBar {

	private static final long serialVersionUID = -7947924545017835841L;

	public ExamMenuBar(GUIPanel editor) {
		super(editor);
	}
	
	protected void addItemsToFile(JMenu menu) {
		menu.addSeparator();
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("Save"), new SaveExamAction(false), "/img/icon/save.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)));
		menu.add(new JMenuItemWithHints(editor.bind(mxResources.get("SaveAs"), new SaveExamAction(true), "/img/icon/saveas.gif")).setAcceleratorBuilder(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)));
	}
	
}
