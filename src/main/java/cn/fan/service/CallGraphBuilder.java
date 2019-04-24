package cn.fan.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LineNumberGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;

import cn.fan.model.CodeLineNumber;

public class CallGraphBuilder extends EmptyVisitor {
	JavaClass visitedClass;
	private MethodGen mg;
	private ConstantPoolGen cp;
	private String format;
	private List<String> methodCalls = new ArrayList<>();
	private HashMap<Instruction, String> instructionLineNum;

	public CallGraphBuilder(MethodGen m, JavaClass jc) {
		visitedClass = jc;
		mg = m;
		cp = mg.getConstantPool();
		instructionLineNum = new HashMap<Instruction, String>();
		format = "M:" + visitedClass.getClassName() + ":" + mg.getName() + "(" + argumentList(mg.getArgumentTypes())
				+ ")" + " " + "(%s)%s:%s(%s) %s";
	}

	private String argumentList(Type[] arguments) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arguments.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(arguments[i].toString());
		}
		return sb.toString();
	}

	public List<String> start() {
		if (mg.isAbstract() || mg.isNative())
			return Collections.emptyList();

		// 先根据字节码和原代码之间的偏移关系 建立偏移表
		LineNumberGen[] lineNumbers = mg.getLineNumbers();
		List<CodeLineNumber> codeLineRelation = new ArrayList<CodeLineNumber>();
		for (int i = 0; i < lineNumbers.length - 1; i++) {
			codeLineRelation.add(new CodeLineNumber(lineNumbers[i].getLineNumber().getStartPC(),
					lineNumbers[i + 1].getLineNumber().getStartPC() - 1,
					lineNumbers[i].getLineNumber().getLineNumber()));
		}
		codeLineRelation.add(new CodeLineNumber(lineNumbers[lineNumbers.length - 1].getLineNumber().getStartPC(),
				Integer.MAX_VALUE, lineNumbers[lineNumbers.length - 1].getLineNumber().getLineNumber()));
		// 建立偏移表之后 就需要进行 指令规划 看看是哪一行
		int currentInstruction = 0;
		int byteIndex = 0;
		int currentCodeLineNumber = 0;
		int currentInstructionLineNumber = 0;
		for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
			Instruction i = ih.getInstruction();
			// 先判断当前currentInstruction 是第几个
			// 在判断属于哪一行源码
			currentInstruction = mg.getInstructionList().getInstructionPositions()[byteIndex++];
			CodeLineNumber codeLineNumber = codeLineRelation.get(currentCodeLineNumber);
			if (currentInstruction > codeLineNumber.getEnd_pc()) {
				currentCodeLineNumber++;
				currentInstructionLineNumber = codeLineRelation.get(currentCodeLineNumber).getLine_number();
			} else {
				currentInstructionLineNumber = codeLineNumber.getLine_number();
			}
			if (!visitInstruction(i)) {
				if (i instanceof InvokeInstruction) {
					instructionLineNum.put(i, String.valueOf(currentInstructionLineNumber));
				}
				i.accept(this);
			}
		}
		return methodCalls;
	}

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();
		return ((InstructionConst.getInstruction(opcode) != null) && !(i instanceof ConstantPushInstruction)
				&& !(i instanceof ReturnInstruction));
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		String callGraphInfo = String.format(format, "M", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp)), this.instructionLineNum.get(i));
		methodCalls.add(callGraphInfo);
		System.out.println(callGraphInfo);
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		String callGraphInfo = String.format(format, "I", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp)), this.instructionLineNum.get(i));
		methodCalls.add(callGraphInfo);
		System.out.println(callGraphInfo);
	}

	@Override
	public void visitINVOKESPECIAL(INVOKESPECIAL i) {
		String callGraphInfo = String.format(format, "O", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp)), this.instructionLineNum.get(i));
		methodCalls.add(callGraphInfo);
		System.out.println(callGraphInfo);
	}

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		String callGraphInfo = String.format(format, "S", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp)), this.instructionLineNum.get(i));
		methodCalls.add(callGraphInfo);
		System.out.println(callGraphInfo);
	}

	@Override
	public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
		String callGraphInfo = String.format(format, "D", i.getType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp)), this.instructionLineNum.get(i));
		methodCalls.add(callGraphInfo);
		System.out.println(callGraphInfo);
	}
};
