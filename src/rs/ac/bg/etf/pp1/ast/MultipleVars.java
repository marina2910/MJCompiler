// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class MultipleVars extends MultipleVarDecl {

    private MultipleVarDecl MultipleVarDecl;
    private VarDeclLine VarDeclLine;

    public MultipleVars (MultipleVarDecl MultipleVarDecl, VarDeclLine VarDeclLine) {
        this.MultipleVarDecl=MultipleVarDecl;
        if(MultipleVarDecl!=null) MultipleVarDecl.setParent(this);
        this.VarDeclLine=VarDeclLine;
        if(VarDeclLine!=null) VarDeclLine.setParent(this);
    }

    public MultipleVarDecl getMultipleVarDecl() {
        return MultipleVarDecl;
    }

    public void setMultipleVarDecl(MultipleVarDecl MultipleVarDecl) {
        this.MultipleVarDecl=MultipleVarDecl;
    }

    public VarDeclLine getVarDeclLine() {
        return VarDeclLine;
    }

    public void setVarDeclLine(VarDeclLine VarDeclLine) {
        this.VarDeclLine=VarDeclLine;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MultipleVarDecl!=null) MultipleVarDecl.accept(visitor);
        if(VarDeclLine!=null) VarDeclLine.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MultipleVarDecl!=null) MultipleVarDecl.traverseTopDown(visitor);
        if(VarDeclLine!=null) VarDeclLine.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MultipleVarDecl!=null) MultipleVarDecl.traverseBottomUp(visitor);
        if(VarDeclLine!=null) VarDeclLine.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MultipleVars(\n");

        if(MultipleVarDecl!=null)
            buffer.append(MultipleVarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclLine!=null)
            buffer.append(VarDeclLine.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MultipleVars]");
        return buffer.toString();
    }
}
