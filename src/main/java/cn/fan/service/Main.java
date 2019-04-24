package cn.fan.service;

public class Main {
	public static void main(String[] args) {
		JCallGraph jCallGraph = new JCallGraph();
//		String[] jars = { "C:\\Users\\Thinkpad\\Desktop\\项目\\科研\\测试的jar\\spring-master.jar",
//				"C:\\Users\\Thinkpad\\Desktop\\项目\\科研\\测试的jar\\poi-4.0.0.jar",
//				"C:\\Users\\Thinkpad\\Desktop\\项目\\科研\\测试的jar\\JavaAlgorithms.jar" };
		String[] jars = { "C:\\Users\\Thinkpad\\Desktop\\项目\\科研\\测试的jar\\Test2.jar" };
		jCallGraph.generateCallGraph(jars);
	}
}
