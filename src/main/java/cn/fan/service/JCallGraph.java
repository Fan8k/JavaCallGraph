package cn.fan.service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.bcel.classfile.ClassParser;

import cn.fan.dao.CGDao;
import cn.fan.model.CG_Info;
import cn.fan.tools.MySqlConnectFactory;
import gr.gousiosg.javacg.stat.ClassVisitor;

public class JCallGraph {
	/**
	 * 输入所有jar 程序会根据jar自动的解析各个方法的callGraph
	 * 
	 * @param jars
	 */
	public void generateCallGraph(String[] jars) {
		Function<ClassParser, ClassVisitor> getClassVisitor = (ClassParser cp) -> {
			try {
				return new ClassVisitor(cp.parse());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
		Connection connection = MySqlConnectFactory.getConnection();
		CGDao cgDao = new CGDao(connection);
		try {
			for (String arg : jars) {

				File f = new File(arg);

				if (!f.exists()) {
					System.err.println("Jar file " + arg + " does not exist");
				}

				try (JarFile jar = new JarFile(f)) {
					Stream<JarEntry> entries = enumerationAsStream(jar.entries());

					String methodCalls = entries.flatMap(e -> {
						if (e.isDirectory() || !e.getName().endsWith(".class"))
							return (new ArrayList<String>()).stream();

						ClassParser cp = new ClassParser(arg, e.getName());
						return getClassVisitor.apply(cp).start().methodCalls().stream();
					}).map(s -> s + "\n").reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
							.toString();
					// 生成的结果
					List<CG_Info> cgInfos = CGservice.convertStrToCGInfos(jar.getName().substring(
							jar.getName().lastIndexOf('\\') + 1, jar.getName().lastIndexOf('.')), methodCalls);
					int i = 0;
					for (CG_Info cg_Info : cgInfos) {
						if (cgDao.insertOne(cg_Info)) {
							System.out.println("已经为" + jar.getName().substring(jar.getName().lastIndexOf('\\') + 1,
									jar.getName().lastIndexOf('.')) + "jar存储" + (i++) + "个");
						}
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error while processing jar: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("全部入库完毕");
		}
	}

	public static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
			public T next() {
				return e.nextElement();
			}

			public boolean hasNext() {
				return e.hasMoreElements();
			}
		}, Spliterator.ORDERED), false);
	}
}
