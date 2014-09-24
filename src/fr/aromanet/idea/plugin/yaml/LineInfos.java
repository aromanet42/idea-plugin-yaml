package fr.aromanet.idea.plugin.yaml;

public class LineInfos {

    private String text;
    private int indent;
    private int lineNumber;

    public LineInfos(String text, int indent, int lineNumber) {
        this.text = text;
        this.indent = indent;
        this.lineNumber = lineNumber;
    }

    public String getText() {
        return text;
    }

    public int getIndent() {
        return indent;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
