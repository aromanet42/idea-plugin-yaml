package fr.aromanet.idea.plugin.yaml;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CopyYamlPropertyPath extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        if (currentFile == null || !"yml".equals(currentFile.getExtension())) {
            Messages.showMessageDialog(project, "Action disponible uniquement avec des fichiers *.yml", "Attention", Messages.getWarningIcon());
            return;
        }

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        Editor editor = fileEditorManager.getSelectedTextEditor();


        try {
            List<String> lines = FileUtil.loadLines(currentFile.getPath());

            int lineNumber = editor.getCaretModel().getCurrentCaret().getVisualPosition().getLine();

            String property = getPropertyAtCursor(lines, lineNumber);

            copyTextToClipboard(property);
//            Messages.showMessageDialog(project, property, "Info", Messages.getInformationIcon());
        } catch (RuntimeException e) {
            Messages.showMessageDialog(project, e.getMessage(), "Erreur", Messages.getErrorIcon());
        } catch (IOException e) {
            Messages.showMessageDialog(project, e.getMessage(), "Erreur", Messages.getErrorIcon());
        }
    }

    private void copyTextToClipboard(String text) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection stringSelection = new StringSelection(text);
        clipboard.setContents(stringSelection, null);
    }

    protected String getPropertyAtCursor(List<String> lines, int lineNumber) {

        String lineText = lines.get(lineNumber);

        if (isCommentOrBlankLine(lineText)) {
            throw new RuntimeException("Pas de propriété sur cette ligne");
        }

        List<String> path = new ArrayList<String>();
        path.add(extractPropertyNameFromLine(lineText));
        LineInfos line = new LineInfos(lineText, countIndent(lineText), lineNumber);

        while (line.getIndent() > 0) {
            line = retrieveParentLineNumber(lines, line);
            path.add(extractPropertyNameFromLine(line.getText()));
        }

        return buildPropertyKeyFromList(path);

    }

    protected LineInfos retrieveParentLineNumber(List<String> lines, LineInfos currentLine) {
        int lineNumber = currentLine.getLineNumber() - 1;
        String line = "";

        while (lineNumber >= 0
                && (isCommentOrBlankLine(line = lines.get(lineNumber)) || countIndent(line) >= currentLine.getIndent())) {
            lineNumber--;
        }

        if (lineNumber < 0) {
            throw new RuntimeException("Impossible de déterminer la clé");
        }

        return new LineInfos(line, countIndent(line), lineNumber);
    }


    protected String buildPropertyKeyFromList(List<String> list) {
        Collections.reverse(list);

        return StringUtils.join(list, '.');
    }

    protected String extractPropertyNameFromLine(String line) {
        if (!line.contains(":")) {
            throw new RuntimeException("Ligne invalide : " + line);
        }

        String[] split = line.split(":");

        return split[0].trim();
    }

    protected boolean isCommentOrBlankLine(String line) {
        return StringUtils.isBlank(line) || line.trim().startsWith("#");
    }

    protected int countIndent(String line) {
        int index = 0;
        while (Character.isSpaceChar(line.charAt(index)) && index < line.length()) {
            index++;
        }

        return index;
    }
}
