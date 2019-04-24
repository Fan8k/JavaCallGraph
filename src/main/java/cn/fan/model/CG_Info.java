package cn.fan.model;

public class CG_Info {
	private int id;
	private String source_method;
	private String source_method_dot_name;
	private String target_method;
	private String target_method_dot_name;
	private String line_number;
	private String project_name;

	public CG_Info() {

	}

	public CG_Info(int id, String source_method, String source_method_dot_name, String target_method,
			String target_method_dot_name, String line_number, String project_name) {
		this.id = id;
		this.source_method = source_method;
		this.source_method_dot_name = source_method_dot_name;
		this.target_method = target_method;
		this.target_method_dot_name = target_method_dot_name;
		this.line_number = line_number;
		this.project_name = project_name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSource_method() {
		return source_method;
	}

	public void setSource_method(String source_method) {
		this.source_method = source_method;
	}

	public String getSource_method_dot_name() {
		return source_method_dot_name;
	}

	public void setSource_method_dot_name(String source_method_dot_name) {
		this.source_method_dot_name = source_method_dot_name;
	}

	public String getTarget_method() {
		return target_method;
	}

	public void setTarget_method(String target_method) {
		this.target_method = target_method;
	}

	public String getTarget_method_dot_name() {
		return target_method_dot_name;
	}

	public void setTarget_method_dot_name(String target_method_dot_name) {
		this.target_method_dot_name = target_method_dot_name;
	}

	public String getLine_number() {
		return line_number;
	}

	public void setLine_number(String line_number) {
		this.line_number = line_number;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	@Override
	public String toString() {
		return "CG_Info [id=" + id + ", source_method=" + source_method + ", source_method_dot_name="
				+ source_method_dot_name + ", target_method=" + target_method + ", target_method_dot_name="
				+ target_method_dot_name + ", line_number=" + line_number + ", project_name=" + project_name + "]";
	}

}
