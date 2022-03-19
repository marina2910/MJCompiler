// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class UnmatchedIfElse extends Unmatched {

    private StartIfElse StartIfElse;
    private Matched Matched;
    private EslePart EslePart;
    private Unmatched Unmatched;

    public UnmatchedIfElse (StartIfElse StartIfElse, Matched Matched, EslePart EslePart, Unmatched Unmatched) {
        this.StartIfElse=StartIfElse;
        if(StartIfElse!=null) StartIfElse.setParent(this);
        this.Matched=Matched;
        if(Matched!=null) Matched.setParent(this);
        this.EslePart=EslePart;
        if(EslePart!=null) EslePart.setParent(this);
        this.Unmatched=Unmatched;
        if(Unmatched!=null) Unmatched.setParent(this);
    }

    public StartIfElse getStartIfElse() {
        return StartIfElse;
    }

    public void setStartIfElse(StartIfElse StartIfElse) {
        this.StartIfElse=StartIfElse;
    }

    public Matched getMatched() {
        return Matched;
    }

    public void setMatched(Matched Matched) {
        this.Matched=Matched;
    }

    public EslePart getEslePart() {
        return EslePart;
    }

    public void setEslePart(EslePart EslePart) {
        this.EslePart=EslePart;
    }

    public Unmatched getUnmatched() {
        return Unmatched;
    }

    public void setUnmatched(Unmatched Unmatched) {
        this.Unmatched=Unmatched;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(StartIfElse!=null) StartIfElse.accept(visitor);
        if(Matched!=null) Matched.accept(visitor);
        if(EslePart!=null) EslePart.accept(visitor);
        if(Unmatched!=null) Unmatched.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(StartIfElse!=null) StartIfElse.traverseTopDown(visitor);
        if(Matched!=null) Matched.traverseTopDown(visitor);
        if(EslePart!=null) EslePart.traverseTopDown(visitor);
        if(Unmatched!=null) Unmatched.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(StartIfElse!=null) StartIfElse.traverseBottomUp(visitor);
        if(Matched!=null) Matched.traverseBottomUp(visitor);
        if(EslePart!=null) EslePart.traverseBottomUp(visitor);
        if(Unmatched!=null) Unmatched.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("UnmatchedIfElse(\n");

        if(StartIfElse!=null)
            buffer.append(StartIfElse.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Matched!=null)
            buffer.append(Matched.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EslePart!=null)
            buffer.append(EslePart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Unmatched!=null)
            buffer.append(Unmatched.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [UnmatchedIfElse]");
        return buffer.toString();
    }
}
