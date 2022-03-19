package rs.ac.bg.etf.pp1;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.ac.bg.etf.pp1.ast.DesignatorName;

public class SemanticAnalyzer extends VisitorAdaptor {

	public static final Struct boolType = new Struct(Struct.Bool);

	Logger log = Logger.getLogger(getClass());

	protected boolean mainDefined = false;
	protected Obj currTypeObject = Tab.noObj; // Which objects are currently defined
	protected Obj currMethod = Tab.noObj; // Which method scope are we in
	protected Obj currMethodRetType = Tab.noObj;
	protected int currMethodFormPar = 0;
	protected boolean recordDeclar = false;
	protected int fieldNumRecord = 0;
	protected boolean doWhile = false;
	protected Stack<Obj> currDesignatorObj = new Stack<Obj>();
	protected HashMap<String, Queue<Obj>> currActPars = new HashMap<String, Queue<Obj>>();
	protected Stack<String> currActParMethodCall = new Stack<String>();
	protected int actPars = 0;
	protected int currDigit = 1;
	protected boolean methodError = false;
	protected static int global_var = 0;
	protected static int local_var = 0;
	private static int actParCallMarker = 0;
	protected static int nVars = 0;
	protected static boolean errorDetected = false;

	public SemanticAnalyzer() {
		Tab.init();
		Scope universe = new Scope(null);
		universe.addToLocals(new Obj(Obj.Type, "bool", boolType));
		Obj bool = Tab.insert(Obj.Type, "bool", boolType);

	}

//********************Error report************************//
	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());

	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}
//*********************************************************//
//********************Help methods************************//

	/* Returns true if object is defined */
	public boolean isObjDefined(String obj) {
		Obj objFound = Tab.find(obj);
		return objFound != Tab.noObj;
	}

	public boolean isObjDefinedInCurScope(String obj) {
		Scope curScope = Tab.currentScope();
		Obj objFound = curScope.findSymbol(obj);
		return objFound != null;
	}

	public int countNumConst(int num) {
		currDigit = currDigit * 10 + num;
		return currDigit;
	}

	public boolean isValAssignableToType(Struct varType, Struct varValue) {
		return varValue.assignableTo(varType) && varValue.compatibleWith(varType);
	}

	public boolean isStructInt(Struct s) {
		return (s.getKind() == Struct.Int);
	}
//*********************************************************//
//**********************Visit nodes************************//	
//********************Program visit************************//

	public void visit(ProgramName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
		Tab.openScope();
		this.mainDefined = false;

		report_info("ProgramName 	visited", progName);
	}

	public void visit(Program program) {
		Tab.chainLocalSymbols(program.getProgramName().obj);
		Tab.closeScope();

		if (!this.mainDefined) {
			report_error("GRESKA: Main funkcija nije definisana", null);
		}

		report_info("Program 		visited", program);
	}
//*********************************************************//
//**********************Type visit*************************//

	public void visit(Type type) {

		if (!isObjDefined(type.getTypeName())) {
			report_error("GRESKA: Nepostojeci tip", type);
			type.struct = Tab.noType;
			this.currTypeObject = Tab.noObj;
			return;
		}

		Obj typeFound = Tab.find(type.getTypeName());

		if (typeFound.getKind() == Obj.Type) {
			this.currTypeObject = typeFound;
			type.struct = typeFound.getType();
		} else {
			report_error("GRESKA: Identifikator tipa ne predstavlja tip", type);
			this.currTypeObject = Tab.noObj;
		}

		report_info("Type		visited", type);
	}
//*********************************************************//
//**********************Const visit*************************//

	public void visit(ConstDecl constDecl) {

		if (isObjDefined(constDecl.getConstName())) {
			report_error("GRESKA: Simbol sa nazivom " + constDecl.getConstName() + " postoji u tabeli simbola, greska ",
					constDecl);
			nVars++;
			return;
		}

		ConstValue constValue = constDecl.getConstValue();

		// Decide constant value
		Struct constType;
		int constVal = 0;
		if (constValue instanceof NumberVal) {
			constType = Tab.intType;
			NumConst numConst = ((NumberVal) constValue).getNumConst();
			constVal = ((NumberConst) numConst).getVal();
			constDecl.obj = new Obj(Obj.Var, "const", Tab.find("int").getType());
			constDecl.obj.setAdr(constVal);
		} else if (constValue instanceof BoolVal) {
			constType = boolType;
			Boolean boolVal = ((BoolVal) constValue).getBoolConst().getVal();
			if (boolVal)
				constVal = 1;
			else
				constVal = 0;
			constDecl.obj = new Obj(Obj.Var, "const", Tab.find("bool").getType());
			constDecl.obj.setAdr(constVal);
		} else if (constValue instanceof CharVal) {
			constType = Tab.charType;
			Character charVal = ((CharVal) constValue).getCharConst().getVal();
			constDecl.obj = new Obj(Obj.Var, "const", Tab.find("char").getType());
			constDecl.obj.setAdr(charVal);
		} else {
			report_error("GRESKA: Vrednost konstante nije validna(Nije karakter, broj ili boolean) ", constValue);
			return;
		}

		// Check if constant type and constant value are assignable
		if (currTypeObject.getType().assignableTo(constType)) {
			Obj addConst = Tab.insert(Obj.Con, constDecl.getConstName(), currTypeObject.getType());
			addConst.setAdr(constVal);
		} else {
			report_error("GRESKA: Tipu konstante nije moguce dodeliti zadatu vrednost", constValue);
		}

		report_info("Const 		visited", constValue);

	}
//*********************************************************//
//**********************Var visit*************************//

	public void visit(VarNam varNam) {

		if (isObjDefinedInCurScope(varNam.getVarName().getVarName())) {
			report_error("GRESKA: Vec deklarisana varijabla koja je pronadjena ", varNam);
			return;
		}
		String name = varNam.getVarName().getVarName();

		Obj newVar;
		if (varNam != null) {
			if (!recordDeclar)
				newVar = Tab.insert(Obj.Var, name, currTypeObject.getType());
			else {
				newVar = Tab.insert(Obj.Fld, name, currTypeObject.getType());
				newVar.setAdr(fieldNumRecord++);
			}
		}
		if (currMethod == Tab.noObj)
			global_var++;
		else
			local_var++;

		report_info("VarName		visited", varNam);
	}

	public void visit(VarArr varArr) {

		if (isObjDefinedInCurScope(varArr.getArrName().getArrName())) {
			report_error("GRESKA: Vec deklarisana varijabla koja je pronadjena ", varArr);
			return;
		}

		String name = varArr.getArrName().getArrName();
		Obj arrType = Tab.find("Arr of " + currTypeObject.getType().getKind());
		Obj newVar;

		if (arrType != Tab.noObj) {
			if (!recordDeclar)
				newVar = Tab.insert(Obj.Var, name, arrType.getType());
			else {
				newVar = Tab.insert(Obj.Fld, name, arrType.getType());
				newVar.setAdr(fieldNumRecord++);
			}

		} else {
			Struct arrStruct = new Struct(Struct.Array, currTypeObject.getType());
			if (!recordDeclar)
				newVar = Tab.insert(Obj.Var, name, arrStruct);
			else {
				newVar = Tab.insert(Obj.Fld, name, arrStruct);
				newVar.setAdr(fieldNumRecord++);
			}
			
		}

		if (currMethod == Tab.noObj)
			global_var++;
		else
			local_var++;

		report_info("VarArr		visited", varArr);
	}

//*********************************************************//
//*********************Record visit************************//

	public void visit(RecordName recordName) {
		if (isObjDefinedInCurScope(recordName.getRName())) {
			report_error("GRESKA: Naziv " + recordName.getRName() + " postoji u tabeli simbola, definisano ",
					recordName);
			return;
		}
		Struct newRecordType = new Struct(Struct.Class);
		recordName.obj = Tab.insert(Obj.Type, recordName.getRName(), newRecordType);
		Tab.openScope();
		recordDeclar = true;
		report_info("RecordName 	visited", recordName);
	}

	public void visit(RecordDecl recordDecl) {
		Obj recordDefined = Tab.find(recordDecl.getRecordName().getRName());
		recordDeclar = false;
		if (recordDefined == Tab.noObj) {
			report_error("GRESKA: Definisani tip rekorda nije dodat u tabelu simbola, greska ", recordDecl);
			return;
		}
		Tab.chainLocalSymbols(recordDefined.getType());
		Tab.closeScope();

		report_info("RecordDecl 	visited", recordDecl);
	}

//*********************************************************//
//*********************Method visit************************//

	public void visit(MethodBegin mBegin) {

		String mName = mBegin.getMName();
		MethodReturnType mReturn = mBegin.getMethodReturnType();

		if (isObjDefined(mName)) {
			report_error("GRESKA: Naziv metode postoji u tabeli simbola, greska ", mBegin);
			methodError = true;
			// Proceed parsing method to report any further errors in the code, but don't
			// insert it in symTable
		}

		if (mReturn instanceof VoidRet)
			currMethodRetType = Tab.noObj;
		else if (mReturn instanceof TypeRet)
			currMethodRetType = Tab.find(((TypeRet) mReturn).getType().getTypeName());

		if (mName == "main") {
			if (!this.mainDefined)
				this.mainDefined = true;
			else {
				report_error("GRESKA: Visestruka definicija main funkcije ", mReturn);
				// Proceed parsing method to report any further errors in code, but don't insert
				// it in Symbol Table
				methodError = true;
			}
		}

		// If error occurs at the begining of method, keep parsing method body to report
		// other errors if such exist
		// but do not add the method to the SymbolTable
		if (!methodError)
			mBegin.obj = currMethod = Tab.insert(Obj.Meth, mName, currMethodRetType.getType());
		else
			mBegin.obj = currMethod = new Obj(Obj.Meth, mName, currMethodRetType.getType());

		Tab.openScope();

		report_info("MethodBegin 	visited", mBegin);
	}

	public void visit(MethodDecl method) {
		if (this.methodError)
			return;
		this.methodError = false;

//		if (this.currMethodRetType == Tab.noObj)
//			actPars = true;

		if (method.getMethodBegin().getMName().equals("main")) {
			if (currMethodRetType != Tab.noObj || currMethodFormPar > 0 || currMethod.getLevel() > 0) {
				report_info("ret: " + currMethodRetType.getKind() + " local var: " + local_var + "currMetLevel: "
						+ currMethod.getLevel(), method);
				report_error("GRESKA: Main metoda mora da bude void metoda bez argumenata, greska ", method);
				return;
			}
			mainDefined = true;
		}

		currMethod.setLevel(currMethodFormPar);
		local_var = 0;
		currMethodFormPar = 0;
		Tab.chainLocalSymbols(currMethod);
		Tab.closeScope();

		report_info("MethodDecl 	visited", method);
	}

//--------------------------FormPars-----------------------//
	public void visit(FormDecl formPar) {

		String name = formPar.getFormParName().getFPName();

		if (isObjDefinedInCurScope(name)) {
			report_error("GRESKA: formalni parametar s datim imenom je vec definisan u ovom opsegu, greska ", formPar);
			return;
		}

		Obj newFormPar = Tab.insert(Obj.Var, name, currTypeObject.getType());

		local_var++;
		currMethodFormPar++;
		report_info("FormDecl 		visited", formPar);
	}

	public void visit(FormArrayDecl formPar) {

		String name = formPar.getFormParName().getFPName();

		if (isObjDefinedInCurScope(name)) {
			report_error("GRESKA: formalni parametar s datim imenom je vec definisan u ovom opsegu, greska ", formPar);
			return;
		}

		Obj arrType = Tab.find("Arr of " + currTypeObject.getType().getKind());
		Obj newVar;

		if (arrType != Tab.noObj) {
			newVar = Tab.insert(Obj.Var, name, arrType.getType());
		} else {
			Struct arrStruct = new Struct(Struct.Array, currTypeObject.getType());
			newVar = Tab.insert(Obj.Var, name, arrStruct);
		}

		local_var++;

		report_info("FormDecl 		visited", formPar);
	}
//*********************************************************//
//***************Factor, Designator visit******************//
	
	public void visit(DesignatorNameArr d) {
		SyntaxNode parent = d.getParent();
		String name = d.getDName();
	}

	public void visit(DesignatorName d) {
		SyntaxNode parent = d.getParent();
		String name = d.getDName();

		d.obj = Tab.find(name);
	}

	public void visit(SingleDesignator singleD) {
		String name = singleD.getDesignatorName().getDName();
		if (!isObjDefined(name)) {
			report_error("GRESKA: Simbol nije pronadjen u tabeli simbola, greska", singleD);
		}

		singleD.obj = Tab.find(name);
		if (singleD.obj.getKind() == Obj.Meth && singleD.obj.getLevel() > 0) {
			actPars++;
			if (currActPars.get(name) != null) {
				name = name + actParCallMarker++;
				// report_info("NAME: " + name, singleD);
			}
			currActPars.put(name, new LinkedList<Obj>());
			currActParMethodCall.push(name);
		}
		report_info("SingleDesignator 	visited", singleD);
	}

	public void visit(ArrayDesignator arrayD) {
		String name = arrayD.getDesignatorName().getDName();
		if (!isObjDefined(name)) {
			report_error("GRESKA: Simbol nije pronadjen u tabeli simbola, greska", arrayD);
			return;
		}
		if (Tab.find(name).getType().getKind() != Struct.Array) {
			report_error("GRESKA: Simbol " + Tab.find(name).getName() + " nije niz, greska", arrayD);

		}

		if (arrayD.getExpr().obj.getType().getKind() != Struct.Int
				&& (arrayD.getExpr().obj.getType().getKind() == Struct.Array
						&& arrayD.getExpr().obj.getType().getElemType().getKind() != Struct.Int)) {
			report_error("GRESKA: Pogresan pristup elementu niza, (nije upotrebljen int), greska", arrayD);

		}
		arrayD.obj = Tab.find(name);
		report_info("ArrayDesignator 	visited", arrayD);
	}

	public void visit(DesignList d) {
		Obj object = currDesignatorObj.pop();
		if (!isObjDefined(object.getName())) {
			report_error("GRESKA: objekat nije definisan, greska", d);
			return;
		}

		object = Tab.find(object.getName());
		if (object.getType().getKind() != Struct.Class) {
			report_error("GRESKA: Objekat" + object + " nije klasnog tipa", d);
			return;
		}

		while (!currDesignatorObj.empty()) {
			Obj nextElem = currDesignatorObj.pop();

			Collection<Obj> fields = object.getType().getMembers();

			Obj found = Tab.noObj;

			for (Obj i : fields) {
				if (i.getName().equals(nextElem.getName())) {
					found = i;
					break;
				}
			}

			if (found == Tab.noObj) {
				report_error("GRESKA: Objekat " + object.getName() + " nema polje " + nextElem.getName() + ", greska",
						d);
				return;
			}

			object = found;
		}
		d.obj = object;
		currDesignatorObj.clear();
		Collection<Obj> fields = Tab.find("t1").getType().getMembers();
		Obj found = Tab.noObj;
		for(Obj i : fields) {
			if(i.getName().equals("a")) {
				found = i;
				break;
			}
		}
		report_info("NADJENO: " + found.getKind() , d);

		report_info("DesignList 	visited", d);
	}

	public void visit(FieldAccessDesignator field) {
//		String objectName = field.getDesignatorName().getDName();
//
//		Obj fieldObj = new Obj(Obj.Fld, objectName, Tab.noType);
//		currDesignatorObj.push(fieldObj);
//		field.obj = fieldObj;
//
//		report_info("ADRESA POLJA: " + field.obj.getName(), field);
//		report_info("FieldAcceDes 	visited", field);
	}

	public void visit(LastField lf) {
		String name = lf.getFieldName().getFName();

		Obj fieldObj = new Obj(Obj.Fld, name, Tab.noType);
		currDesignatorObj.push(fieldObj);
		lf.obj = fieldObj;
		report_info("ADRESA POLJA: " + lf.obj.getName(), lf);
		report_info("LastField		visited", lf);
	}

	public void visit(LastArrayField lf) {
		String name = lf.getDesignatorNameArr().getDName();

		Obj fieldObj = new Obj(Obj.Fld, name, Tab.noType);
		currDesignatorObj.push(fieldObj);

		report_info("LastArrField 	visited", lf);
	}

//---------------------Factor visit------------------------//
	public void visit(Constr constr) {
		if (currTypeObject.getType().getKind() != Struct.Class) {
			report_error("GRESKA: Nije moguce pozvati konstruktor za tip koji nije unutrasnja klasa. greska", constr);
			return;
		}
		Collection<Obj> fields = Tab.find(constr.getType().getTypeName()).getType().getMembers();
		for (Obj i : fields) {
			report_info("POLJE STRUKTURE: " + i.getAdr(), null);
		}
		constr.obj = currTypeObject;
		report_info("Constr		visited" + constr.getType().struct.getNumberOfFields(), constr);

	}

	public void visit(ConstrArr constr) {
//		if (currTypeObject.getType().getKind() != Struct.Class ) {
//			report_error("GRESKA: Nije moguce pozvati konstruktor za tip koji nije unutrasnja klasa, greska", constr);
//
//		}

		if (constr.getExpr().obj.getType().getKind() != Struct.Int) {
			report_error("GRESKA: Izraz nije celobrojnog tipa,  greska", constr);

		}

		constr.obj = currTypeObject;
		report_info("Constr		visited", constr);

	}

	public void visit(NumCons num) {
		int value = ((NumberConst) num.getNumConst()).getVal();
		num.obj = new Obj(Obj.Con, "value", Tab.find("int").getType());
		num.obj.setAdr(value);
		report_info("NumCons		visited", num);
	}

	public void visit(CharCons chr) {
		chr.obj = new Obj(Obj.Con, "", Tab.find("char").getType());
		chr.obj.setAdr(chr.getCharConst().getVal());
		report_info("CharCons	visited", chr);
	}

	public void visit(BoolCons b) {
		Boolean boolVal = b.getBoolConst().getVal();
		int bval = 0;
		if (boolVal)
			bval = 1;
		b.obj = new Obj(Obj.Con, "value", Tab.find("bool").getType());
		b.obj.setAdr(bval);
		report_info("BoolCons		visited", b);

	}

	public void visit(Objct object) {
		object.obj = object.getDesignator().obj;

		report_info("Objct		visited", object);
	}

	public void visit(FuncCall func) {
		String funcName = func.getDesignator().obj.getName();

		if (!isObjDefined(funcName)) {
			report_error("GRESKA: Poziv funkcije koja nije definisana, greska", func);
			func.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.None));
			return;
		}
		Obj funcFound = Tab.find(funcName);
		if (funcFound.getKind() != Obj.Meth) {
			report_error("GRESKA: Simbol nije funkcija ili konstruktor, greska ", func);
			func.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
			return;
		}
		if (funcFound.getLevel() > 0) {
			report_error("GRESKA: Ocekivano " + funcFound.getLevel() + " parametara, prosledjeno 0", func);
			func.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
			return;
		}
		func.obj = funcFound;
		report_info("FuncCall 		visited", func);
	}

	public void visit(FuncCallPars f) {
		String funcName = f.getDesignator().obj.getName();
		Obj func = f.getDesignator().obj;
		Queue<Obj> currActPars1 = new LinkedList<Obj>();

		if (!isObjDefined(funcName)) {
			report_error("GRESKA: Poziv funkcije koja nije definisana, greska", f);
			currActPars.get(currActParMethodCall.peek()).clear();
			currActParMethodCall.pop();
			if (actPars > 0)
				actPars--;
			f.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
			return;
		} else {
			if (func.getKind() != Obj.Meth) {
				report_error("GRESKA: " + funcName + " nije funkcija, greska", f);
				currActPars.get(currActParMethodCall.peek()).clear();
				currActParMethodCall.pop();
				if (actPars > 0)
					actPars--;
				f.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
				return;
			}
		}
		if (!isObjDefined(funcName)) {
			report_error("GRESKA: Poziv funkcije koja nije definisana, greska", f);
			currActPars.get(currActParMethodCall.peek()).clear();
			currActParMethodCall.pop();
			if (actPars > 0)
				actPars--;
			f.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
			return;
		}
		Obj funcFound = Tab.find(funcName);
		if (funcFound.getKind() != Obj.Meth) {
			report_error("GRESKA: Simbol nije funkcija ili konstruktor, greska ", f);
			currActPars.get(currActParMethodCall.peek()).clear();
			currActParMethodCall.pop();
			if (actPars > 0)
				actPars--;
			f.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
			return;
		}
		Collection<Obj> formPars = funcFound.getLocalSymbols();
		int cnt = 0;
		int parNum = funcFound.getLevel();

		if (funcFound.getLevel() == 0) {
			report_error("GRESKA: Neispravan broj stvarnih argumenata, greska", f);
			if (actPars > 0)
				actPars--;
			return;
		}
		currActPars1 = currActPars.get(currActParMethodCall.peek());
		if (parNum != currActPars1.size()) {
			report_error("GRESKA:  Ocekivano " + parNum + " " + currActParMethodCall.peek() + " prosledjeno "
					+ currActPars1.size(), f);
//			while(!currActPars1.isEmpty()) {
//				report_info("p: " + currActPars1.remove(), null);
//			}

			if (actPars > 0)
				actPars--;
			currActPars.get(funcName).clear();
			currActParMethodCall.pop();
			f.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
			return;
		}
		for (Obj i : formPars) {
			if (++cnt > parNum)
				break;

			if (i.getType().getKind() != currActPars1.remove().getType().getKind()) {
				report_error(
						"GRESKA: Tip ocekivanog i prosledjenog parametra se ne poklapaju (" + i.getName() + "), greska",
						f);
				break;
			}
		}

		f.obj = funcFound;
		currActPars.get(currActParMethodCall.peek()).clear();
		currActParMethodCall.pop();
		// if(actPars > 0 ) actPars--;
		report_info("FuncParCall 	visited", f);
	}

	public void visit(FactorLst f) {
		if (!isStructInt(f.getFactor().obj.getType()))
			if (f.getFactor().obj.getType().getKind() == Struct.Array
					&& (f.getFactor().obj.getType().getElemType().getKind() != Struct.Int)) {
				report_error("GRESKA: Vrednost izraza mora biti celobrojnog tipa, greska", f);
				f.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
				return;
			}
		f.obj = f.getFactor().obj;
		int val1 = f.getFactor().obj.getFpPos();
		int val2 = 1;
		if (f.getFactorList().obj != null)
			val2 = f.getFactorList().obj.getFpPos();
		f.obj.setFpPos(val1 * val2);
//		report_info("VRSTA: " + f.obj.getType().getKind(), null);
		report_info("FactorList 	visited", f);
	}

	public void visit(SingleFactor f) {

		f.obj = f.getFactor().obj;

		report_info("SingleFactor 	visited", f);
	}

	public void visit(Expression e) {
		e.obj = e.getExpr().obj;
		report_info("Expression 	visited", e);
	}

//*********************************************************//
//************************Term visit***********************//

	public void visit(Term t) {
		t.obj = t.getFactorList().obj;
		report_info("Term 		visited", t);
	}

	public void visit(TermLst t) {
		if (!isStructInt(t.getTerm().obj.getType())) {
			if (t.getTerm().obj.getType().getKind() == Struct.Array
					&& (t.getTerm().obj.getType().getElemType().getKind() != Struct.Int)) {
				report_error("GRESKA: Vrednost izraza mora biti celobrojnog tipa, greska", t);
				t.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
				return;
			}
		}
		t.obj = t.getTerm().obj;
		int val1 = t.getTerm().obj.getFpPos();
		int val2 = 0;
		if (t.getTermList().obj != null)
			val2 = t.getTermList().obj.getFpPos();
		t.obj.setFpPos(val1 + val2);
		if (actPars > 0 && t.getTerm().obj.getType().getKind() != Struct.Array) {
			currActPars.get(currActParMethodCall.peek()).add(t.obj);
			report_info(currActParMethodCall.peek() + t.getTerm().obj.getName(), null);
		}
		report_info("TermLst 		visited", t);
	}

	public void visit(SingleTerm t) {

		t.obj = t.getTerm().obj;
		if (actPars > 0 && t.getTerm().obj.getType().getKind() != Struct.Array) {
			currActPars.get(currActParMethodCall.peek()).add(t.obj);
			// report_info(currActParMethodCall.peek() + t.getTerm().obj.getName(), null);
		}
		report_info("SingleTerm 	visited", t);
	}

	public void visit(Express e) {

		e.obj = e.getTermList().obj;
		// e.obj.setLocals(null);
		report_info("Express	 	visited", e);
	}

	public void visit(MinExpr e) {
		if (!isStructInt(e.getTermList().obj.getType())) {
			report_error("GRESKA: Vrednost izraza mora biti celobrojnog tipa, greska", e);
			e.obj = new Obj(Obj.NO_VALUE, "func", new Struct(Struct.Int));
			return;
		}
		e.obj = e.getTermList().obj;
		e.obj.setLocals(null);
		report_info("Express	 	visited", e);
	}

	public void visit(ExprLst e) {

		e.obj = e.getExpr().obj;

		report_info("ExprLst	 	visited", e);
	}

	public void visit(SingleExpr e) {

		e.obj = e.getExpr().obj;
		report_info("SingleExpr	 	visited", e);
	}
//*********************************************************//
//*********************Act pars visit**********************//

	public void visit(ActParams a) {
		actPars--;
//		report_info(" " + actPars, a);
		report_info("ActParams 		visited", a);
	}
//*********************Statement visit**********************//

	public void visit(DesignStmt s) {
		report_info("DesignatorS 	visiter", s);
	}

	public void visit(Assign a) {
		Designator d = a.getDesignator();
		Expr e = a.getExpr();

		if (d.obj.getKind() != Obj.Var && d.obj.getKind() != Obj.Fld && d.obj.getType().getKind() != Struct.Array) {
			report_error("GRESKA: Simbol " + d.obj.getName() + " nije objekat, niz ili polje klase, greska", a);
		}

		if (d.obj.getType().getKind() != e.obj.getType().getKind()) {
			if (d.obj.getType().getKind() == Struct.Array
					&& (d.obj.getType().getElemType().getKind() != e.obj.getType().getKind())) {
				report_info("D: " + d.obj.getType().getKind() + " e: " + e.obj.getType().getKind(), null);
				report_error("GRESKA: Promenljiva i izraz nisu kompatibilnog tipa, greska", a);
			}
		}

		a.obj = d.obj;
		report_info("Assign 		visited", a);
	}

	public void visit(Increment s) {
		Designator d = s.getDesignator();

		if (!isObjDefined(d.obj.getName())) {
			report_error("GRESKA: Simbol " + d.obj.getName() + " nije definisan, greska", s);
		}

		// Provera
		// Da li je d int ili niz intova
		if ((d.obj.getType().getKind() != Struct.Int
				&& (d.obj.getType().getKind() != Struct.Array && d.obj.getType().getElemType().getKind() != Struct.Int))
				// i da li je d polje ili objekat
				|| (d.obj.getKind() != Obj.Var && d.obj.getKind() != Obj.Fld)) {
			report_error("GRESKA: Simbol " + d.obj.getName() + " nije polje/objekat celobrojnog tipa, greska", s);
		}

		s.obj = d.obj;
		s.obj.setFpPos(d.obj.getFpPos() + 1);

		report_info("Increment 		visited", d);

	}

	public void visit(Decrement s) {
		Designator d = s.getDesignator();

		if (!isObjDefined(d.obj.getName())) {
			report_error("GRESKA: Simbol " + d.obj.getName() + " nije definisan, greska", s);
		}

		// Provera
		// Da li je d int ili niz intova
		if ((d.obj.getType().getKind() != Struct.Int
				&& (d.obj.getType().getKind() != Struct.Array && d.obj.getType().getElemType().getKind() != Struct.Int))
				// i da li je d polje ili objekat
				|| (d.obj.getKind() != Obj.Var && d.obj.getKind() != Obj.Fld)) {
			report_error("GRESKA: Simbol " + d.obj.getName() + " nije polje/objekat celobrojnog tipa, greska", s);
		}

		s.obj = d.obj;
		s.obj.setFpPos(d.obj.getFpPos() - 1);

		report_info("Decrement 		visited", d);
	}

	public void visit(RetNoExpr r) {
		if (currMethod == Tab.noObj) {
			report_error("GRESKA: Poziv return naredbe izvan funkcije, greska", r);
			return;
		}
		if (currMethod.getType().getKind() != Struct.None) {
			report_error("GRESKA: Metodi " + currMethod.getName() + " nedostaje povratna vrednost, greska", r);
		}

		report_info("RetNoExpr 		visited", r);
	}

	public void visit(RetExpr r) {
		if (currMethod == Tab.noObj) {
			report_error("GRESKA: Poziv return naredbe izvan funkcije, greska", r);
			return;
		}
		if (currMethod.getType().getKind() == Struct.None) {
			report_error("GRESKA: Metoda " + currMethod.getName() + " ne treba da ima povratnu vrednost, greska", r);
		} else if (currMethod.getType().getKind() != r.getExpr().obj.getType().getKind()) {
			report_error("GRESKA: Metoda " + currMethod.getName() + " ima drugaciji povratni tip, greska", r);
		}

		report_info("RetExpr 		visited", r);
	}

	public void visit(BreakStmt s) {
		if (!doWhile) {
			report_error("GRESKA: Nije dozvoljeno koristiti break naredbu van do-while petlje", s);
		}

		report_info("BreakStmt 		visited", s);
	}

	public void visit(ContinueStmt s) {
		if (!doWhile) {
			report_error("GRESKA: Nije dozvoljeno koristiti continue naredbu van do-while petlje", s);
		}

		report_info("ContinueStmt 		visited", s);
	}

	public void visit(ReadStmt s) {
		Designator d = s.getDesignator();

		// Provera
		// Da li je d int/char/bool ili niz intova/charova/boolova
		if ((d.obj.getType().getKind() != Struct.Int && d.obj.getType().getKind() != Struct.Bool
				&& d.obj.getType().getKind() != Struct.Char
				&& (d.obj.getType().getKind() != Struct.Array && d.obj.getType().getKind() != Struct.Int
						&& d.obj.getType().getKind() != Struct.Bool && d.obj.getType().getKind() != Struct.Char))
				// i da li je d polje ili objekat
				|| (d.obj.getKind() != Obj.Var && d.obj.getKind() != Obj.Fld)) {
			report_error("GRESKA: Simbol " + d.obj.getName() + " nije polje/objekat tipa int, char ili bool, greska",
					s);
		}

		report_info("ReadStmt 		visited", s);
	}

	public void visit(PrintNoNum s) {
		Expr d = s.getExpr();

//		if (e.obj.getType().getKind() != Struct.Bool && e.obj.getType().getKind() != Struct.Char
//				&& e.obj.getType().getKind() != Struct.Int) {
		if ((d.obj.getType().getKind() != Struct.Int && d.obj.getType().getKind() != Struct.Bool
				&& d.obj.getType().getKind() != Struct.Char
				&& (d.obj.getType().getKind() != Struct.Array && d.obj.getType().getKind() != Struct.Int
						&& d.obj.getType().getKind() != Struct.Bool && d.obj.getType().getKind() != Struct.Char))) {

			report_error("GRESKA: Izraz nije tipa int, char ili bool, greska", d);
		}

		report_info("PrintNoNum 	visited", s);
	}

	public void visit(PrintNum s) {
		Expr e = s.getExpr();

		if (e.obj.getType().getKind() != Struct.Bool && e.obj.getType().getKind() != Struct.Char
				&& e.obj.getType().getKind() != Struct.Int) {
			report_error("GRESKA: Izraz nije tipa int, char ili bool, greska", e);
		}

		report_info("PrintNoNum 	visited", s);
	}

	public void visit(StartDoWhile s) {
		doWhile = true;
		report_info("StartDoWhile 	visited", s);
	}

	public void visit(DoWhileBlock s) {
		doWhile = false;

		report_info("DoWhileBlock 	visited", s);
	}

	public void visit(FuncCallNoParam f) {

	}

	public void visit(FuncCallParam f) {

	}

//*********************************************************//
//********************Condition visit**********************//

	public void visit(CondFactRelop c) {
		Expr e1 = c.getExpr();
		Expr e2 = c.getExpr1();
		Relop r = c.getRelop();

		if (!e1.obj.equals(e2.obj) && !e1.obj.getType().equals(e2.obj.getType())
				&& !(e1.obj.getType().getKind() == Struct.Array
						&& e1.obj.getType().getElemType().getKind() == e2.obj.getType().getKind())
				&& !(e2.obj.getType().getKind() == Struct.Array
						&& e2.obj.getType().getElemType().getKind() == e1.obj.getType().getKind())) {
			// report_info(" LOL 1: " + e1.obj.getType().getKind() + ", 2: " +
			// e2.obj.getType().getKind(), null);
			report_error("GRESKA: Izrazi nisu kompatibilni, greska", e2);
		}

		if (e1.obj.getType().getKind() == Struct.Array || e2.obj.getType().getKind() == Struct.Class) {
			if (!r.obj.getName().equals("same") && !r.obj.getName().equals("differ")) {
				report_error(
						"GRESKA: Uz promenljive tipa klase ili niza, od relacionih operatora, mogu se koristiti samo != i ==, greska",
						r);
			}
		}
		c.obj = new Obj(Obj.Var, "value", boolType);
		report_info("CondFRelop 	visited", c);
	}

	public void visit(CondFactExpr c) {
		c.obj = c.getExpr().obj;
		report_info("CondFExpr 		visited", c);
	}

	public void visit(CondFactLst c) {
		report_info("CondFList  	visited", c);
	}

	public void visit(SingleCondFact c) {
		report_info("SingleCondList	visited", c);
	}

	public void visit(CondTerm c) {
		report_info("CondTerm	  	visited", c);
	}

	public void visit(SingleCondTerm c) {
		report_info("SingleCondTerm 	visited", c);
	}

	public void visit(CondTermLst c) {
		report_info("CondTermLst  	visited", c);
	}

	public void visit(Condition c) {
		report_info("Condition		visited", c);
	}

	public void visit(Same r) {
		r.obj = new Obj(Obj.Con, "same", Tab.find("null").getType());
		report_info("Same 		visited", r);
	}

	public void visit(Differ r) {
		r.obj = new Obj(Obj.Con, "differ", Tab.find("null").getType());
		report_info("Differ			 visited", r);
	}

	public void visit(GreaterThan r) {
		r.obj = new Obj(Obj.Con, "gre", Tab.find("null").getType());
		report_info("GreaterThan	 visited", r);
	}

	public void visit(GreaterEqualTo r) {
		r.obj = new Obj(Obj.Con, "greequ", Tab.find("null").getType());
		report_info("GreaterEqualTo	 visited", r);
	}

	public void visit(LessThan r) {
		r.obj = new Obj(Obj.Con, "less", Tab.find("null").getType());
		report_info("LessThan		 visited", r);
	}

	public void visit(LessEqualTo r) {
		r.obj = new Obj(Obj.Con, "lessequ", Tab.find("null").getType());
		report_info("LessEqualTo	 visited", r);
	}

}
