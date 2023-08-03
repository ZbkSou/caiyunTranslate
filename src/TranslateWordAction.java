import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TranslateWordAction extends AnAction {

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
                        String[] words = res.split(" ");
                        if (words.length == 0) return;
                        StringBuilder sb = new StringBuilder();
                        for (String word : words) sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
                        Utils.replace( editor,  e,  sb.toString(),  selectionModel );
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
