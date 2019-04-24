/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gr.gousiosg.javacg.stat;

import java.util.ArrayList;
import java.util.Collections;
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

/**
 * The simplest of method visitors, prints any invoked method signature for all
 * method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class MethodVisitor extends EmptyVisitor {

	JavaClass visitedClass;
	private MethodGen mg;
	private ConstantPoolGen cp;
	private String format;
	private List<String> methodCalls = new ArrayList<>();

	public MethodVisitor(MethodGen m, JavaClass jc) {
		visitedClass = jc;
		mg = m;
		cp = mg.getConstantPool();
		format = "M:" + visitedClass.getClassName() + ":" + mg.getName() + "(" + argumentList(mg.getArgumentTypes())
				+ ")" + " " + "(%s)%s:%s(%s)";
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
					System.out.println(mg.getClassName() + mg.getName() + ":" + i.toString() + " : "
							+ currentInstructionLineNumber);
				} else {
					i.accept(this);
				}
			}
		}
		return methodCalls;
	}

	public void way() {
		LineNumberGen[] lineNumbers = mg.getLineNumbers();
		// 有多少行就有多少个行号
		int totol_lines = lineNumbers.length;
		int index = 0;
		// 第一个永远都有
		LineNumberGen currentNumberGen = mg.getLineNumbers()[0];
//		if (index < totol_lines) {
//			currentNumberGen = lineNumbers[index++];
//			InstructionHandle instruction = currentNumberGen.getInstruction();
//			// 当前指令和源码中指令相似
//			if (i.toString().equals(instruction.getInstruction().toString())
//					&& instruction.getInstruction() instanceof InvokeInstruction) {
//				System.out.println(mg.getClassName() + mg.getName() + ":" + i.toString() + " : "
//						+ currentNumberGen.getSourceLine());
//			}
//		} else {
//			if (i instanceof InvokeInstruction) {
//				System.out.println(mg.getClassName() + mg.getName() + ":" + i.toString() + " : "
//						+ currentNumberGen.getSourceLine());
//			}
//		}
	}

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();
		return ((InstructionConst.getInstruction(opcode) != null) && !(i instanceof ConstantPushInstruction)
				&& !(i instanceof ReturnInstruction));
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		methodCalls.add(String.format(format, "M", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		methodCalls.add(String.format(format, "I", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKESPECIAL(INVOKESPECIAL i) {

		methodCalls.add(String.format(format, "O", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		methodCalls.add(String.format(format, "S", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
		methodCalls.add(
				String.format(format, "D", i.getType(cp), i.getMethodName(cp), argumentList(i.getArgumentTypes(cp))));
	}
}
