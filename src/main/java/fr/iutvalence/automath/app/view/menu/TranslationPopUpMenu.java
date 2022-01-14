package fr.iutvalence.automath.app.view.menu;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.app.editor.EditorActions;
import fr.iutvalence.automath.app.model.StateInfo;
import fr.iutvalence.automath.app.view.menu.PopUpMenu;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TranslationPopUpMenu extends PopUpMenu {

    @Override
    protected void update(mxCell cell, TargetType type) {
        if (type.equals(TargetType.State)) {
            StateInfo stInfo =(StateInfo)cell.getValue();
            add(new EditorActions.SetInitialAction(mxResources.get("SetInitial"))).setSelected(stInfo.isStarting());
            addSeparator();
            addReorderAction();
            addSeparator();
            addCopyCutPasteActions();
            addDeleteAction();
        } else {
            super.update(cell, type);
        }
    }
}
