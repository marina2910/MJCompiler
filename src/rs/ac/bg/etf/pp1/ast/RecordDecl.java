// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class RecordDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private RecordName RecordName;
    private RecordBody RecordBody;

    public RecordDecl (RecordName RecordName, RecordBody RecordBody) {
        this.RecordName=RecordName;
        if(RecordName!=null) RecordName.setParent(this);
        this.RecordBody=RecordBody;
        if(RecordBody!=null) RecordBody.setParent(this);
    }

    public RecordName getRecordName() {
        return RecordName;
    }

    public void setRecordName(RecordName RecordName) {
        this.RecordName=RecordName;
    }

    public RecordBody getRecordBody() {
        return RecordBody;
    }

    public void setRecordBody(RecordBody RecordBody) {
        this.RecordBody=RecordBody;
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
        if(RecordName!=null) RecordName.accept(visitor);
        if(RecordBody!=null) RecordBody.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(RecordName!=null) RecordName.traverseTopDown(visitor);
        if(RecordBody!=null) RecordBody.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(RecordName!=null) RecordName.traverseBottomUp(visitor);
        if(RecordBody!=null) RecordBody.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("RecordDecl(\n");

        if(RecordName!=null)
            buffer.append(RecordName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(RecordBody!=null)
            buffer.append(RecordBody.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [RecordDecl]");
        return buffer.toString();
    }
}
