package com.github.ggiamarchi.i18n.generator.test.integ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Test;

import com.github.ggiamarchi.i18n.generator.Main;

public class MainTest {

	private static final String OUTPUT_DIR = "target/generated-test-integ";
	private static final String SOURCE_TEST_DIR = "src/test-rc";

	@Test
	public void testMain() throws Exception {

		Main.main(new String [] {
				"-b", "com.github.ggiamarchi.i18n.generator.test.messages",
				"-dir", "src/test/resources",
				"-output", OUTPUT_DIR
		});

		String [] filesToCompile = {
				OUTPUT_DIR + "/com/github/ggiamarchi/i18n/generator/test/Messages.java",
				OUTPUT_DIR + "/com/github/ggiamarchi/i18n/generator/test/MessagesImpl.java",
				SOURCE_TEST_DIR + "/com/test/i18n/TestI18N.java"
		};

		Assert.assertTrue("Generated source files compilation Failed", compile("target/test-classes", filesToCompile));

		Class<?> clazz = Class.forName("com.test.i18n.TestI18N");

		Method m = clazz.getMethod("testGenratedI18N");

		try {
			m.invoke(clazz.newInstance());
		}
		catch (InvocationTargetException e) {
			Assert.fail();
		}

	}

	private static boolean compile(String outputDir, String... files) {

		JavaFileObject javaFileObjects[] = new JavaFileObject[files.length];

		int i = 0;
		for (String file : files) {
			javaFileObjects[i++] = new JavaSourceCodeObject (new File(file).toURI(), Kind.SOURCE);
		}

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		CompilationTask compilerTask = compiler.getTask(
				null,
				fileManager,
				diagnostics,
				Arrays.asList("-d", new File(outputDir).getAbsolutePath()),
				null,
				Arrays.asList(javaFileObjects)
				) ;

		boolean compileSuccess = compilerTask.call();

		if (!compileSuccess){
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()){
				System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
			}
		}

		try {
			fileManager.close() ;
		}
		catch (IOException e) {
			// Nothing to do...
		}

		return compileSuccess;
	}

	static class JavaSourceCodeObject extends SimpleJavaFileObject{

		public JavaSourceCodeObject(URI uri, Kind kind) {
			super(uri, kind);
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			StringBuilder sb;
			BufferedReader reader = null;
			try {
				sb = new StringBuilder();
				String line;
				reader = new BufferedReader(new FileReader(new File(toUri())));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
			return sb.toString();
		}
		
	}

}
