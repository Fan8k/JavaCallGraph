package cn.fan.model;

/**
 * 自定义的行号和字节码偏移量关系
 * 
 * @author Thinkpad
 *
 */
public class CodeLineNumber {
	private int start_pc;
	private int end_pc;
	private int line_number;

	public CodeLineNumber() {
		super();
	}

	public CodeLineNumber(int start_pc, int end_pc, int line_number) {
		super();
		this.start_pc = start_pc;
		this.end_pc = end_pc;
		this.line_number = line_number;
	}

	public int getStart_pc() {
		return start_pc;
	}

	public void setStart_pc(int start_pc) {
		this.start_pc = start_pc;
	}

	public int getEnd_pc() {
		return end_pc;
	}

	public void setEnd_pc(int end_pc) {
		this.end_pc = end_pc;
	}

	public int getLine_number() {
		return line_number;
	}

	public void setLine_number(int line_number) {
		this.line_number = line_number;
	}

	@Override
	public String toString() {
		return "CodeLineNumber [start_pc=" + start_pc + ", end_pc=" + end_pc + ", line_number=" + line_number + "]";
	}

}
