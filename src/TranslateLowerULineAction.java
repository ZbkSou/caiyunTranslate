import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

//小写下划线
public class TranslateLowerULineAction extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            String selectedText = selectionModel.getSelectedText();
            if (selectedText != null&& !selectedText.isEmpty()) {
                String type = Utils.containsChinese(selectedText)?"zh2en":"en2zh";
                try {
                    String res =  Utils.request(selectedText,type);
                    if("en2zh".equals(type)){
                        Utils.showTooltip(editor, selectionModel.getSelectionStartPosition(),res);
                    }else {
                        res = res.toLowerCase()
                                .replaceAll(" ", "_").replace(",", "");
                        Utils.replace( editor,  e,  res,  selectionModel );
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
