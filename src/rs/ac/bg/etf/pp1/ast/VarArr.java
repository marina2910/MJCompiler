// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class VarArr extends VarDecl {

    private ArrName ArrName;

    public VarArr (ArrName ArrName) {
        this.ArrName=ArrName;
        if(ArrName!=null) ArrName.setParent(this);
    }

    public ArrName getArrName() {
        return ArrName;
    }

    public void setArrName(ArrName ArrName) {
        this.ArrName=ArrName;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ArrName!=null) ArrName.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ArrName!=null) ArrName.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ArrName!=null) ArrName.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarArr(\n");

        if(ArrName!=null)
            buffer.append(ArrName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarArr]");
        return buffer.toString();
    }
}
