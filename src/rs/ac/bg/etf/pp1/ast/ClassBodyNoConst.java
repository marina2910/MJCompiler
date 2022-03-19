// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class ClassBodyNoConst extends ClassBody {

    private NoConstructor NoConstructor;

    public ClassBodyNoConst (NoConstructor NoConstructor) {
        this.NoConstructor=NoConstructor;
        if(NoConstructor!=null) NoConstructor.setParent(this);
    }

    public NoConstructor getNoConstructor() {
        return NoConstructor;
    }

    public void setNoConstructor(NoConstructor NoConstructor) {
        this.NoConstructor=NoConstructor;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(NoConstructor!=null) NoConstructor.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(NoConstructor!=null) NoConstructor.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(NoConstructor!=null) NoConstructor.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassBodyNoConst(\n");

        if(NoConstructor!=null)
            buffer.append(NoConstructor.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassBodyNoConst]");
        return buffer.toString();
    }
}
