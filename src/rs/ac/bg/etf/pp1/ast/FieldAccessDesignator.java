// generated with ast extension for cup
// version 0.8
// 17/1/2022 4:36:52


package rs.ac.bg.etf.pp1.ast;

public class FieldAccessDesignator extends DesignatorList {

    private ObjectName ObjectName;
    private DesignatorList DesignatorList;

    public FieldAccessDesignator (ObjectName ObjectName, DesignatorList DesignatorList) {
        this.ObjectName=ObjectName;
        if(ObjectName!=null) ObjectName.setParent(this);
        this.DesignatorList=DesignatorList;
        if(DesignatorList!=null) DesignatorList.setParent(this);
    }

    public ObjectName getObjectName() {
        return ObjectName;
    }

    public void setObjectName(ObjectName ObjectName) {
        this.ObjectName=ObjectName;
    }

    public DesignatorList getDesignatorList() {
        return DesignatorList;
    }

    public void setDesignatorList(DesignatorList DesignatorList) {
        this.DesignatorList=DesignatorList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ObjectName!=null) ObjectName.accept(visitor);
        if(DesignatorList!=null) DesignatorList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ObjectName!=null) ObjectName.traverseTopDown(visitor);
        if(DesignatorList!=null) DesignatorList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ObjectName!=null) ObjectName.traverseBottomUp(visitor);
        if(DesignatorList!=null) DesignatorList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FieldAccessDesignator(\n");

        if(ObjectName!=null)
            buffer.append(ObjectName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorList!=null)
            buffer.append(DesignatorList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FieldAccessDesignator]");
        return buffer.toString();
    }
}
