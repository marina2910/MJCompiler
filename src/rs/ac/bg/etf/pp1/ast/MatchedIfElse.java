// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class MatchedIfElse extends Matched {

    private StartIfElse StartIfElse;
    private Matched Matched;
    private EslePart EslePart;
    private Matched Matched1;

    public MatchedIfElse (StartIfElse StartIfElse, Matched Matched, EslePart EslePart, Matched Matched1) {
        this.StartIfElse=StartIfElse;
        if(StartIfElse!=null) StartIfElse.setParent(this);
        this.Matched=Matched;
        if(Matched!=null) Matched.setParent(this);
        this.EslePart=EslePart;
        if(EslePart!=null) EslePart.setParent(this);
        this.Matched1=Matched1;
        if(Matched1!=null) Matched1.setParent(this);
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

    public Matched getMatched1() {
        return Matched1;
    }

    public void setMatched1(Matched Matched1) {
        this.Matched1=Matched1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(StartIfElse!=null) StartIfElse.accept(visitor);
        if(Matched!=null) Matched.accept(visitor);
        if(EslePart!=null) EslePart.accept(visitor);
        if(Matched1!=null) Matched1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(StartIfElse!=null) StartIfElse.traverseTopDown(visitor);
        if(Matched!=null) Matched.traverseTopDown(visitor);
        if(EslePart!=null) EslePart.traverseTopDown(visitor);
        if(Matched1!=null) Matched1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(StartIfElse!=null) StartIfElse.traverseBottomUp(visitor);
        if(Matched!=null) Matched.traverseBottomUp(visitor);
        if(EslePart!=null) EslePart.traverseBottomUp(visitor);
        if(Matched1!=null) Matched1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MatchedIfElse(\n");

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

        if(Matched1!=null)
            buffer.append(Matched1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MatchedIfElse]");
        return buffer.toString();
    }
}
