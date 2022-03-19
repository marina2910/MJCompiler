// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class FormParName implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private String fPName;

    public FormParName (String fPName) {
        this.fPName=fPName;
    }

    public String getFPName() {
        return fPName;
    }

    public void setFPName(String fPName) {
        this.fPName=fPName;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParName(\n");

        buffer.append(" "+tab+fPName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParName]");
        return buffer.toString();
    }
}
