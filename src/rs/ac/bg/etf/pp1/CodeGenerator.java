package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.HashTableDataStructure;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;

import java.util.Collection;
import java.util.Stack;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.CounterVisitor.FormParamCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.*;

public class CodeGenerator extends VisitorAdaptor {

	Logger logger = Logger.getLogger(getClass());
	private boolean ArrField = false;
	private int mainPC;
	private String currObj;
	private String currField;
	private Stack<PatchAddress> patchAddr = new Stack<>();
	private Stack<PatchAddress> patchAddrBreak = new Stack<>();
	private Stack<PatchAddress> patchAddrContinue = new Stack<>();
	private int doStart = 0;
	private int whileCond = 0;
	private int endWhile = 0;
	
	public int getMainPC() {
		return mainPC;
	};

	public void visit(ProgramName pn) {
		Obj chr = Tab.find("chr");
		Obj ord = Tab.find("ord");
		chr.setAdr(Code.pc);
		ord.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);
		
		Obj len = Tab.find("len");
		len.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.arraylength);
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
//********************************PRINT, READ **********************************
	public void visit(PrintNoNum p) {
		if (p.getExpr().obj.getType().getKind() == Struct.Int || p.getExpr().obj.getType().getKind() == Struct.Array
				&& p.getExpr().obj.getType().getElemType().getKind() == Struct.Int) {
			Code.loadConst(5);
			Code.put(Code.print);
		} else {
			Code.loadConst(1);
			Code.put(Code.bprint);
		}
	}
	
	public void visit(ReadStmt p) {
		if (p.getDesignator().obj.getType().getKind() == Struct.Int || p.getDesignator().obj.getType().getKind() == Struct.Array
				&& p.getDesignator().obj.getType().getElemType().getKind() == Struct.Int) {
			Code.put(Code.read);
		} else {
			Code.put(Code.bread);
		}
		
		
		Code.store(p.getDesignator().obj);
	}

//********************************CONST VALUES**********************************

	public void visit(ConstDecl cnst) {
		Obj con = Tab.insert(Obj.Con, "$", cnst.obj.getType());
		con.setLevel(0);
		con.setAdr(cnst.obj.getAdr());

		Code.load(con);
	}

	public void visit(NumCons cnst) {
		Obj con = Tab.insert(Obj.Con, "$", cnst.obj.getType());
		con.setLevel(0);
		con.setAdr(cnst.obj.getAdr());

		Code.load(con);
	}

	public void visit(CharCons cnst) {
		Obj con = Tab.insert(Obj.Con, "$", cnst.obj.getType());
		con.setLevel(0);
		con.setAdr(cnst.obj.getAdr());

		Code.load(con);
	}

	public void visit(BoolCons cnst) {
		Obj con = Tab.insert(Obj.Con, "$", cnst.obj.getType());
		con.setLevel(0);
		con.setAdr(cnst.obj.getAdr());

		Code.load(con);
	}

//********************************* METHOD **************************************

	public void visit(MethodBegin method) {
		if (method.obj.getName().equals("main")) {
			mainPC = Code.pc;
		}

		method.obj.setAdr(Code.pc);
		int fpCnt = method.obj.getLevel();
		int fpLocCnt = method.obj.getLocalSymbols().size();

		// Generate the entry
		Code.put(Code.enter);
		Code.put(fpCnt);
		Code.put(fpLocCnt);

	}

	public void visit(MethodDecl methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(RetExpr returnExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(RetNoExpr returnNoExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(FuncCallPars procCall) {
		Obj functionObj = procCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
//		logger.error(" OFFSET " + offset + " addr " + functionObj.getAdr() );
//		if(procCall.getDesignator().obj.getType() != Tab.noType){
//			Code.put(Code.pop);
//		}
	}

	public void visit(FuncCall procCall) {
		Obj functionObj = procCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);

//		if(procCall.getDesignator().obj.getType() != Tab.noType){
//			Code.put(Code.pop);
//		}
	}

//********************************* ASSIGN **************************************

	public void visit(Assign assignment) {
		// Skida vrednost
		Designator des = (Designator) assignment.getDesignator();

		if (des instanceof ArrayDesignator || des instanceof DesignList && ArrField) {
			// Skida adresu, index i vrednost
			ArrField = false;

			// Provera da li je int(koristi aload)
			// ili char/byte(koristi baload)
			if (assignment.getDesignator().obj.getType().getElemType().getKind() == Struct.Int) {
				Code.put(Code.astore);
			} else {
				Code.put(Code.bastore);
			}
		} else {
			// Na steku se nalazi vrednost koju treba smestiti u objekat
			Code.store(assignment.getDesignator().obj);
		}

	}

//	public void visit(SingleDesignator designator){
//		SyntaxNode parent = designator.getParent();
//		
//		if(Assign.class != parent.getClass() && FuncCallParam.class != parent.getClass() 
//				&& FuncCallNoParam.class != parent.getClass()){
//			Code.load(designator.obj);
//		}
//	}

	// Designator
	public void visit(DesignatorName designator) {
		Obj o = designator.obj;
		SyntaxNode parent = designator.getParent();
		SyntaxNode parent1 = designator.getParent();
		
		if (Assign.class != parent.getClass() &&  LastField.class != parent.getClass()
				&& LastArrayField.class != parent.getClass()) {
			
			while(!(parent instanceof Assign ) && !(parent instanceof Expr) && (parent != null)) parent = parent.getParent();
			if(parent instanceof Assign && o.getType().getKind() != Struct.Array) return;
			if(parent instanceof Assign && (o.getType().getKind() == Struct.Array && parent1 instanceof SingleDesignator)) return;
			
			Code.load(designator.obj); // obj addr
		}
	}

	// ArrayDesignator
	public void visit(ArrayDesignator designator) {

		SyntaxNode parent = designator.getParent();

		// Objct se nalazi u faktoru, treba da se ucita adresa tog objekta na stek
		if (parent instanceof Objct || parent instanceof Expr) {
			if (designator.obj.getType().getKind() == Struct.Array) {
				if (designator.obj.getType().getElemType().getKind() == Struct.Int)
					Code.put(Code.aload);
				else
					Code.put(Code.baload);
			}
		}

	}

//	public void visit(LastField f) {
	// Na steku se nalazi adresa objekta cijem se polju pristupa
	// U d.obj se smesti taj objekat

//		SyntaxNode parent = f.getParent();
//		DesignatorName d = null;
//		if(parent instanceof FieldAccessDesignator) 
//			d.obj = ((FieldAccessDesignator) parent).getDesignatorName().obj;
//		
//		//Dohvate se njegova polja i na stek se stavi vrednost tog polja
//		Collection<Obj> fields = d.obj.getType().getMembers();
//
//		Obj found = Tab.noObj;
//
//		for (Obj i : fields) {
//			if (i.getName().equals(d.obj.getName())) {
//				found = i;
//				break;
//			}
//		}
//		
//		Code.load(found);
//	}
//************************************** NEW ****************************************

	public void visit(ConstrArr arr) {
		// Skida sa steka velicinu niza

		Code.put(Code.newarray);
		if (arr.getType().struct.getKind() == Struct.Int) {
			Code.put(1);
		} else {
			Code.put(0);
		}
		// Ostavlja na steku adresu niza kog je alocirao na hipu
	}

	public void visit(Constr t) {
		// Skida sa steka velicinu niza
		Code.put(Code.new_);
		Struct str = t.getType().struct;

		Code.put2(str.getNumberOfFields() * 4);
		// Ostavlja na steku adresu niza kog je alocirao na hipu
	}

	public void visit(TermLst term) {
		// skida sa steka poslednje dve vrednosti

		if (term.getAddop() instanceof Plus) {
			Code.put(Code.add);
		} else {
			Code.put(Code.sub);
		}

		// Ostavlja na stek vrednost izraza
	}

	public void visit(FactorLst term) {
		// skida sa steka poslednje dve vrednosti

		if (term.getMulop() instanceof Mul) {
			Code.put(Code.mul);
		} else if (term.getMulop() instanceof Div) {
			Code.put(Code.div);
		} else {
			Code.put(Code.rem);
		}

		// Ostavlja na stek vrednost izraza
	}

	public void visit(Increment i) {

		Designator des = i.getDesignator();

		if (des.obj.getType().getKind() == Struct.Int) {
			// Na steku se nalazi vrednost promenljive
			Code.put(Code.const_1);
			Code.put(Code.add);
			Code.store(des.obj);
		}

		if (des.obj.getType().getKind() == Struct.Array) {
			// Na steku se nalaze adresa niza i indeks
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.put(Code.const_1);
			Code.put(Code.add);
			Code.put(Code.astore);
		}
	}

	public void visit(Decrement i) {

		Designator des = i.getDesignator();

		if (des.obj.getType().getKind() == Struct.Int) {
			// Na steku se nalazi vrednost promenljive
			Code.put(Code.const_1);
			Code.put(Code.sub);
			Code.store(des.obj);
		}

		if (des.obj.getType().getKind() == Struct.Array) {
			// Na steku se nalaze adresa niza i indeks
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.put(Code.const_1);
			Code.put(Code.sub);
			Code.put(Code.astore);
		}
	}

	public void visit(DesignList designator) {
		SyntaxNode parent = designator.getParent();
		if (FuncCallParam.class != parent.getClass() && FuncCallNoParam.class != parent.getClass()) {
//			Collection<Obj> fields = Tab.find(currObj).getType().getMembers();
//			Obj found = Tab.noObj;
//			for(Obj i : fields) {
//				if(i.getName().equals(currField)) {
//					found = i;
//					break;
//				}
//			}
//			
//			Code.load(found);
		}
	}

	public void visit(FieldAccessDesignator f) {
		currObj = f.getObjectName().getOName();
	}

	public void visit(LastField f) {
//		SyntaxNode parent1 = f.getParent().getParent().getParent();
//		if (Assign.class != parent1.getClass() && FuncCallParam.class != parent1.getClass()
//				&& FuncCallNoParam.class != parent1.getClass()) {
//			SyntaxNode parent = f.getParent();
//			Obj designatorObj = ((FieldAccessDesignator) parent).getObjectName().obj;
//			currField = f.getObjectName().getOName();
//			Collection<Obj> fields = designatorObj.getType().getMembers();
//			Obj found = Tab.noObj;
//			for (Obj i : fields) {
//				if (i.getName().equals(currField)) {
//					found = i;
//					break;
//				}
//			}
//
//			Code.load(found);
//		}
	}

	public void visit(LastArrayField f) {
		SyntaxNode parent1 = f.getParent().getParent().getParent();
		SyntaxNode parent = f.getParent();
		Obj designatorObj = ((FieldAccessDesignator) parent).getObjectName().obj;
		currField = f.getDesignatorNameArr().getDName();
		SymbolDataStructure sds = designatorObj.getType().getMembersTable();
		Obj found = sds.searchKey(currField);
		ArrField = true;
		int index = (byte) Code.get(Code.pc);

		if (!(parent1 instanceof Assign))
			Code.put(Code.aload);
		Obj cons = new Obj(Obj.Con, "$", new Struct(Struct.Int));
		cons.setAdr(index);
		// Code.load(cons);

	}

	public void visit(DesignatorNameArr f) {
		SyntaxNode parent1 = f.getParent().getParent();
		Obj designatorObj = ((FieldAccessDesignator) parent1).getObjectName().obj;
		currField = f.getDName();
		Collection<Obj> fields = designatorObj.getType().getMembers();
		Obj found = Tab.noObj;
		for (Obj i : fields) {
			if (i.getName().equals(currField)) {
				found = i;
				break;
			}
		}

		Code.load(found);
	}

//	public void visit (DesignatorArrName d) {
//		ArrayDesignator a = (ArrayDesignator)d.getParent();
//		Obj o = a.obj;
//		Code.load(o);
//	}

	public void visit(MinExpr min) {
		Code.put(Code.const_m1);
		Code.put(Code.mul);
	}

//**************************************CONDITION********************************************
	private int getRelOpCode(Relop relOp) {
		if (relOp instanceof Same)
			return Code.eq;
		if (relOp instanceof Differ)
			return Code.ne;
		if (relOp instanceof GreaterThan)
			return Code.gt;
		if (relOp instanceof GreaterEqualTo)
			return Code.ge;
		if (relOp instanceof LessThan)
			return Code.lt;
		return Code.le;
	}

	public void visit(CondFactRelop cond) {
		int relop = getRelOpCode(cond.getRelop());
		int pcVal = Code.pc + 3; // Pokazuje na instrukciju iza jmpcc
		pcVal += 1; // Pokazuje iza konstane
		pcVal += 3; // Pokazuje iza sledeceg jmp-a
		// pcVal += 1;
		Code.putFalseJump(relop, pcVal);
		Code.loadConst(1);
		pcVal = Code.pc + 3;// Pokazuje na instrukciju iza jmpcc

		Code.putJump(pcVal + 1);
		Code.loadConst(0);
	}

	public void visit(SingleCondFact cond) {
		// CondFactExpr nalazi se vec expr na steku
	}

	public void visit(CondFactLst cond) {
		// Na steku su vrednosti Single cond fact, uradimo mul tih vrednosti i ako je
		// neka od njih false, dobije se false vrednost
		Code.put(Code.mul);
	}

	public void visit(CondTermLst cond) {
		// Na steku su vrednosti svih uslova
		Code.put(Code.add);
		Code.loadConst(0);
		int pcVal = Code.pc + 3; // Pokazuje na instrukciju iza jmpcc
		pcVal += 1; // Pokazuje iza konstane
		pcVal += 3; // Pokazuje iza sledeceg jmp-a
//		pcVal += 1;
		Code.putFalseJump(Code.ne, pcVal);
		Code.loadConst(1);
		pcVal = Code.pc + 3;// Pokazuje na instrukciju iza jmpcc

		Code.putJump(pcVal + 1);
		Code.loadConst(0);
	}
//***************************************IF ELSE*********************************************

	public void visit(StartIfElse startIfElse) {
		Code.loadConst(0);
		PatchAddress pa = new PatchAddress(Code.pc + 1);
		patchAddr.add(pa);
		Code.putFalseJump(Code.ne, 0);
	}

	public void visit(UnmatchedIf umif) {
		int patchAdd = patchAddr.pop().getAddr();
		if (patchAdd == -1) {
			logger.error("Greska u vreme izvrsavanja");
			return;
		}
		Code.fixup(patchAdd);
	}

	public void visit(ElsePart elsePart) {
		// Preskoci else part
		PatchAddress pa = new PatchAddress(Code.pc + 1);
		Code.putJump(0);

		int patchAdd = patchAddr.pop().getAddr();
		if (patchAdd == -1) {
			logger.error("Greska u vreme izvrsavanja");
			return;
		}
		Code.fixup(patchAdd);
		patchAddr.add(pa);
	}

	public void visit(MatchedIfElse matchedIfElse) {
		int patchAdd = patchAddr.pop().getAddr();
		if (patchAdd == -1) {
			logger.error("Greska u vreme izvrsavanja");
			return;
		}
		Code.fixup(patchAdd);
	}

	public void visit(UnmatchedIfElse umIfElse) {

		while (!patchAddr.isEmpty()) {
			int patchAdd = patchAddr.pop().getAddr();
			if (patchAdd == -1) {
				logger.error("Greska u vreme izvrsavanja");
				return;
			}
			Code.fixup(patchAdd);
		}
	}

//***************************************DO WHILE*********************************************

	public void visit(StartDoWhile start) {
		doStart = Code.pc;
	}

	public void visit(StartWhile start) {
		// Na doStart adresu (pocetak do/while) se skace ako je uslov tacan
		whileCond = Code.pc;
	}

	public void visit(EndWhile end) {
		// Na steku se nalazi vrednost iz uslova

		Code.loadConst(0);
		Code.putFalseJump(Code.eq, doStart);
		endWhile = Code.pc;
		while (!patchAddrBreak.isEmpty()) {
			int addrFix = patchAddrBreak.pop().getAddr();
			Code.fixup(addrFix);
		}

		// Ako je uslov ispunjen skoci na pocetak do petlje
	}

	public void visit(ContinueStmt cont) {
		Code.putJump(doStart);
	}

	public void visit(BreakStmt cont) {

		PatchAddress pa = new PatchAddress(Code.pc + 1);
		Code.putJump(0);
		patchAddrBreak.add(pa);
	}
}
