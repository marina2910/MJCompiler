// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class ClassBodyConst extends ClassBody {

    private YesConstructor YesConstructor;

    public ClassBodyConst (YesConstructor YesConstructor) {
        this.YesConstructor=YesConstructor;
        if(YesConstructor!=null) YesConstructor.setParent(this);
    }

    public YesConstructor getYesConstructor() {
        return YesConstructor;
    }

    public void setYesConstructor(YesConstructor YesConstructor) {
        this.YesConstructor=YesConstructor;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(YesConstructor!=null) YesConstructor.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(YesConstructor!=null) YesConstructor.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(YesConstructor!=null) YesConstructor.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassBodyConst(\n");

        if(YesConstructor!=null)
            buffer.append(YesConstructor.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassBodyConst]");
        return buffer.toString();
    }
}
