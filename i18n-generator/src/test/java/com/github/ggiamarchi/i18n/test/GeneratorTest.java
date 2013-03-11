package com.github.ggiamarchi.i18n.test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.ggiamarchi.i18n.Generator;
import com.github.ggiamarchi.i18n.GeneratorLauncher.Method;

public class GeneratorTest {

	/*
	 * Test that generation engine ends without error but does not check the correctness
	 * of the generated output
	 */
	@Test
	public void generateInterfaceTest() {
		Generator generator = new Generator();
		generator.generate(buildModel(), "i18n-java-interface.ftl", new StringWriter());		
	}
	
	/*
	 * Test that generation engine ends without error but does not check the correctness
	 * of the generated output
	 */
	@Test
	public void generateClassTest() {
		Generator generator = new Generator();
		generator.generate(buildModel(), "i18n-java-class.ftl", new StringWriter());
	}
	
	private Map<String, Object> buildModel() {
		
		Method [] methods = new Method[2];
		methods[0] = new Method("hello.world", "hello_world");
		methods[1] = new Method("hello.single", "hello_single", new String [] { "arg0", "arg1" });
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("interfacePackageName", "pac.ka.ge");
		model.put("classPackageName", "pac.ka.ge");
		model.put("interfaceName", "I18N");
		model.put("methods", methods);       	
		model.put("className", "I18NImpl");
		model.put("bundleName", "com.github.ggiamarchi.i18n.test.I18N");
		
		return model;
		
	}
	
}
