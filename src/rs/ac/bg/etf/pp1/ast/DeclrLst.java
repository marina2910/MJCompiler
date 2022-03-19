// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class DeclrLst extends DeclarationList {

    private DeclarationList DeclarationList;
    private DeclarationItem DeclarationItem;

    public DeclrLst (DeclarationList DeclarationList, DeclarationItem DeclarationItem) {
        this.DeclarationList=DeclarationList;
        if(DeclarationList!=null) DeclarationList.setParent(this);
        this.DeclarationItem=DeclarationItem;
        if(DeclarationItem!=null) DeclarationItem.setParent(this);
    }

    public DeclarationList getDeclarationList() {
        return DeclarationList;
    }

    public void setDeclarationList(DeclarationList DeclarationList) {
        this.DeclarationList=DeclarationList;
    }

    public DeclarationItem getDeclarationItem() {
        return DeclarationItem;
    }

    public void setDeclarationItem(DeclarationItem DeclarationItem) {
        this.DeclarationItem=DeclarationItem;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DeclarationList!=null) DeclarationList.accept(visitor);
        if(DeclarationItem!=null) DeclarationItem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DeclarationList!=null) DeclarationList.traverseTopDown(visitor);
        if(DeclarationItem!=null) DeclarationItem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DeclarationList!=null) DeclarationList.traverseBottomUp(visitor);
        if(DeclarationItem!=null) DeclarationItem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DeclrLst(\n");

        if(DeclarationList!=null)
            buffer.append(DeclarationList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DeclarationItem!=null)
            buffer.append(DeclarationItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DeclrLst]");
        return buffer.toString();
    }
}
