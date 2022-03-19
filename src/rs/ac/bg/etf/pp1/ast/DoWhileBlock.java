// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class DoWhileBlock extends Matched {

    private StartDo StartDo;
    private Statements Statements;
    private StartWhile StartWhile;
    private Condition Condition;
    private EndWhile EndWhile;

    public DoWhileBlock (StartDo StartDo, Statements Statements, StartWhile StartWhile, Condition Condition, EndWhile EndWhile) {
        this.StartDo=StartDo;
        if(StartDo!=null) StartDo.setParent(this);
        this.Statements=Statements;
        if(Statements!=null) Statements.setParent(this);
        this.StartWhile=StartWhile;
        if(StartWhile!=null) StartWhile.setParent(this);
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
        this.EndWhile=EndWhile;
        if(EndWhile!=null) EndWhile.setParent(this);
    }

    public StartDo getStartDo() {
        return StartDo;
    }

    public void setStartDo(StartDo StartDo) {
        this.StartDo=StartDo;
    }

    public Statements getStatements() {
        return Statements;
    }

    public void setStatements(Statements Statements) {
        this.Statements=Statements;
    }

    public StartWhile getStartWhile() {
        return StartWhile;
    }

    public void setStartWhile(StartWhile StartWhile) {
        this.StartWhile=StartWhile;
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public EndWhile getEndWhile() {
        return EndWhile;
    }

    public void setEndWhile(EndWhile EndWhile) {
        this.EndWhile=EndWhile;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(StartDo!=null) StartDo.accept(visitor);
        if(Statements!=null) Statements.accept(visitor);
        if(StartWhile!=null) StartWhile.accept(visitor);
        if(Condition!=null) Condition.accept(visitor);
        if(EndWhile!=null) EndWhile.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(StartDo!=null) StartDo.traverseTopDown(visitor);
        if(Statements!=null) Statements.traverseTopDown(visitor);
        if(StartWhile!=null) StartWhile.traverseTopDown(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
        if(EndWhile!=null) EndWhile.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(StartDo!=null) StartDo.traverseBottomUp(visitor);
        if(Statements!=null) Statements.traverseBottomUp(visitor);
        if(StartWhile!=null) StartWhile.traverseBottomUp(visitor);
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        if(EndWhile!=null) EndWhile.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DoWhileBlock(\n");

        if(StartDo!=null)
            buffer.append(StartDo.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statements!=null)
            buffer.append(Statements.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StartWhile!=null)
            buffer.append(StartWhile.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EndWhile!=null)
            buffer.append(EndWhile.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DoWhileBlock]");
        return buffer.toString();
    }
}
